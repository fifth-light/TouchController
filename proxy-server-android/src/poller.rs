use std::{
    io,
    os::{android::net::SocketAddrExt, unix::net::SocketAddr},
};

use log::info;
use tokio::{
    io::{AsyncReadExt, AsyncWriteExt},
    net::UnixStream,
    runtime::Runtime,
    sync::mpsc,
};

#[derive(Debug)]
pub struct Poller {
    receive_queue: mpsc::UnboundedReceiver<io::Result<Vec<u8>>>,
    send_queue: mpsc::UnboundedSender<Vec<u8>>,
    _runtime: Runtime,
}

impl Poller {
    pub fn new(name: &str) -> io::Result<Self> {
        let address = SocketAddr::from_abstract_name(name)?;
        let inner = std::os::unix::net::UnixStream::connect_addr(&address)?;

        let runtime = Runtime::new()?;

        let (receive_tx, receive_rx) = mpsc::unbounded_channel::<io::Result<Vec<u8>>>();
        let (send_tx, mut send_rx) = mpsc::unbounded_channel::<Vec<u8>>();

        info!("Start tokio runtime");
        runtime.block_on(async {
            let inner = UnixStream::from_std(inner)?;
            let (mut reader, mut sender) = inner.into_split();

            runtime.spawn(async move {
                let result: Result<(), io::Error> = async {
                    loop {
                        let mut length = [0; 1];
                        reader.read_exact(&mut length).await?;

                        let length = length[0] as usize;
                        if length == 0 {
                            continue;
                        }

                        let mut buffer = vec![0; length];
                        reader.read_exact(&mut buffer).await?;
                        let _ = receive_tx.send(Ok(buffer));
                    }
                }
                .await;
                if let Err(err) = result {
                    let _ = receive_tx.send(Err(err));
                }
            });
            runtime.spawn(async move {
                let _: io::Result<()> = async {
                    while let Some(message) = send_rx.recv().await {
                        if message.len() >= 256 {
                            continue;
                        }

                        let length = [message.len() as u8; 1];
                        sender.write_all(&length).await?;
                        sender.write_all(&message).await?;
                        sender.flush().await?;
                    }
                    Ok(())
                }
                .await;
            });
            Ok::<_, io::Error>(())
        })?;

        Ok(Self {
            receive_queue: receive_rx,
            send_queue: send_tx,
            _runtime: runtime,
        })
    }

    pub fn receive(&mut self) -> io::Result<Option<Vec<u8>>> {
        self.receive_queue.try_recv().ok().transpose()
    }

    pub fn send(&mut self, message: &[u8]) {
        let _ = self.send_queue.send(message.to_vec());
    }
}
