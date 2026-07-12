package io.github.xffc.packetcatcher

import io.github.xffc.packetcatcher.config.ModConfig.Filter.Type
import io.netty.channel.Channel
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.PacketFlow
import java.io.File
import java.lang.reflect.Modifier
import java.net.InetSocketAddress
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

object PacketLogger {
    var enabled by Delegates.notNull<Boolean>()

    lateinit var titleFormat: String
    lateinit var valueFormat: String

    lateinit var dateFormatter: DateTimeFormatter
    lateinit var filters: Map<PacketFlow, Pair<Type, List<String>>>

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    private var files: MutableMap<Channel, File> = mutableMapOf()

    @JvmStatic
    fun write(ctx: Channel, packet: Packet<*>) = scope.launch {
        writeAsync(ctx, packet)
    }

    @JvmStatic
    suspend fun writeAsync(ctx: Channel, packet: Packet<*>) {
        if (!enabled) return

        val packetId = packet.type().id.toString()

        val filter = filters[packet.type().flow] ?: return
        val passed = when (filter.first) {
            Type.WHITELIST -> packetId in filter.second
            Type.BLACKLIST -> packetId !in filter.second
        }

        if (!passed) return

        mutex.withLock {
            withContext(Dispatchers.IO) {
                val file = files[ctx] ?: return@withContext null

                val time = LocalDateTime.now().format(dateFormatter)

                val values = packet::class.java.declaredFields.mapNotNull {
                    if (!it.trySetAccessible() || Modifier.isStatic(it.modifiers)) return@mapNotNull null
                    val value = it.get(packet) ?: return@mapNotNull null
                    valueFormat
                        .replace("%name%", it.name)
                        .replace("%value%", value.toString())
                }.joinToString(System.lineSeparator())

                val text = titleFormat
                    .replace("%type%", packet.type().flow.id())
                    .replace("%time%", time)
                    .replace("%id%", packetId)
                    .replace("%nl%", System.lineSeparator())
                    .replace("%values%", values)

                file.appendText(text + System.lineSeparator())
            }
        }
    }

    @JvmStatic
    fun start(ctx: Channel) {
        if (!enabled) return
        val host = ctx.remoteAddress() as InetSocketAddress
        files[ctx] = File("PD-${host.hostString}-${System.currentTimeMillis() / 1000}.txt")
    }

    @JvmStatic
    fun stop(ctx: Channel) {
        scope.launch {
            mutex.withLock {
                files.remove(ctx)
            }
        }
    }
}