package top.fifthlight.touchcontroller.proxy.client.android.transport

import android.net.LocalServerSocket
import android.net.LocalSocket
import android.util.Log
import top.fifthlight.touchcontroller.proxy.client.MessageTransport
import java.io.IOException
import java.nio.ByteBuffer

private const val TAG = "UnixSocketTransport"

private class UnixSocketTransport(
    private val serverSocket: LocalServerSocket,
) : MessageTransport {
    private var clientSocket: LocalSocket? = null

    override fun send(buffer: ByteBuffer) {
        while (true) {
            val client = clientSocket
            if (client == null) {
                clientSocket = serverSocket.accept()
                continue
            }

            try {
                val stream = client.outputStream

                if (buffer.remaining() > 255) {
                    // Message too big
                    throw IOException("Message too big: ${buffer.remaining()}")
                } else if (!buffer.hasRemaining()) {
                    throw IllegalArgumentException("Empty message")
                }
                // Length-prefixed encoding
                stream.write(buffer.remaining())

                if (buffer.hasArray() && !buffer.isReadOnly) {
                    // Directly use array
                    val array = buffer.array()
                    stream.write(array, buffer.arrayOffset(), buffer.remaining())
                    buffer.position(buffer.position() + buffer.remaining())
                } else {
                    // Copy to a new array
                    val array = ByteArray(buffer.remaining())
                    buffer.get(array)
                    stream.write(array)
                }
                break
            } catch (ex: IOException) {
                Log.w(TAG, "Send message failed: ", ex)
                try {
                    clientSocket?.close()
                } catch (ex: IOException) {
                }
                clientSocket = null
            }
        }
    }
}

fun UnixSocketTransport(name: String): MessageTransport =
    UnixSocketTransport(LocalServerSocket(name))