use std::{
    io::{self, ErrorKind, Read},
    os::{
        android::net::SocketAddrExt,
        unix::net::{SocketAddr, UnixStream},
    },
};

#[derive(Debug)]
pub struct UnixSocketTransport {
    inner: UnixStream,
    buffer: Option<(usize, usize, Vec<u8>)>,
}

impl UnixSocketTransport {
    pub fn new(name: &str) -> io::Result<Self> {
        let address = SocketAddr::from_abstract_name(name)?;
        let inner = UnixStream::connect_addr(&address)?;
        inner.set_nonblocking(true)?;
        Ok(Self {
            inner,
            buffer: None,
        })
    }

    pub fn receive(&mut self) -> io::Result<Option<Vec<u8>>> {
        fn handle_error<T>(result: io::Result<T>) -> io::Result<Option<T>> {
            match result {
                Ok(result) => Ok(Some(result)),
                Err(err) => {
                    if err.kind() == ErrorKind::WouldBlock {
                        Ok(None)
                    } else {
                        Err(err)
                    }
                }
            }
        }

        // Length-prefixed encoding
        loop {
            match self.buffer.take() {
                Some((message_length, read_length, mut buffer)) => {
                    let read_result =
                        handle_error(self.inner.read(&mut buffer[read_length..message_length]))?;
                    if let Some(length) = read_result {
                        let read_length = read_length + length;
                        if read_length >= message_length {
                            return Ok(Some(buffer));
                        } else {
                            self.buffer = Some((message_length, read_length, buffer));
                            return Ok(None);
                        }
                    } else {
                        self.buffer = Some((message_length, read_length, buffer));
                        return Ok(None);
                    }
                }
                None => {
                    let mut buffer = [0u8; 1];
                    if let Some(read_length) = handle_error(self.inner.read(&mut buffer))? {
                        if read_length == 0 {
                            return Ok(None);
                        }

                        let packet_length = buffer[0];
                        // Ignore packet with zero length
                        if packet_length == 0 {
                            continue;
                        }
                        let packet_length = packet_length as usize;
                        self.buffer = Some((packet_length, 0, vec![0; packet_length]));
                        continue;
                    } else {
                        return Ok(None);
                    }
                }
            }
        }
    }
}
