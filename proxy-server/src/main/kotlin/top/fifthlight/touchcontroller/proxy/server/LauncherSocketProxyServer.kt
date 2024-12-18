package top.fifthlight.touchcontroller.proxy.server

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import top.fifthlight.touchcontroller.proxy.message.ProxyMessage
import top.fifthlight.touchcontroller.proxy.server.message.MessageDecodeException
import top.fifthlight.touchcontroller.proxy.server.message.decodeMessage
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet6Address
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import kotlin.coroutines.resume

class LauncherSocketProxyServer internal constructor(private val socket: DatagramSocket) : AutoCloseable {
    private val receiveLock = Mutex()
    private val receiveQueue = mutableListOf<ProxyMessage>()
    private val _listening = MutableStateFlow(false)
    val listening = _listening.asStateFlow()

    suspend fun start() {
        val buffer = ByteBuffer.allocate(1024)
        val packet = DatagramPacket(buffer.array(), buffer.remaining())
        while (true) {
            buffer.clear()
            _listening.value = true
            try {
                suspendCancellableCoroutine { continuation ->
                    Thread {
                        socket.receive(packet)
                        continuation.resume(Unit)
                    }.also { thread ->
                        continuation.invokeOnCancellation {
                            thread.interrupt()
                        }
                        thread.start()
                    }
                }
                @Suppress("RedundantUnitExpression")
                Unit
            } catch (_: InterruptedException) {
                throw kotlin.coroutines.cancellation.CancellationException()
            } catch (ex: IOException) {
                ex.printStackTrace()
                return
            }
            buffer.limit(packet.length)

            if (buffer.remaining() < 4) {
                continue
            }

            val type = buffer.getInt()
            val message = try {
                decodeMessage(type, buffer)
            } catch (ex: MessageDecodeException) {
                continue
            }
            receiveLock.withLock {
                receiveQueue.add(message)
            }
        }
    }

    override fun close() {
        socket.close()
    }

    suspend fun receive(): ProxyMessage? = receiveLock.withLock {
        receiveQueue.removeFirstOrNull()
    }
}

fun localhostLauncherSocketProxyServer(port: Int): LauncherSocketProxyServer? {
    val address = Inet6Address.getByAddress(
        "localhost",
        byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1)
    )
    return try {
        val socket = DatagramSocket(InetSocketAddress(address, port))
        val proxy = LauncherSocketProxyServer(socket)
        proxy
    } catch (ex: IOException) {
        ex.printStackTrace()
        null
    }
}