package io.github.xffc.packetcatcher.mixins;

import io.github.xffc.packetcatcher.PacketLogger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class PacketMixin {
    @Shadow
    private Channel channel;

    @Inject(
            method = "channelActive",
            at = @At(value = "INVOKE", target = "Lio/netty/channel/ChannelHandlerContext;channel()Lio/netty/channel/Channel;")
    )
    private void openChannel(ChannelHandlerContext ctx, CallbackInfo ci) {
        PacketLogger.start(ctx.channel());
    }

    @Inject(method = "channelInactive", at = @At("HEAD"))
    private void closeChannel(ChannelHandlerContext ctx, CallbackInfo ci) {
        PacketLogger.stop(ctx.channel());
    }

    @Inject(
            method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;genericsFtw(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;)V")
    )
    private static void handlePacket(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo ci) {
        PacketLogger.write(ctx.channel(), packet);
    }

    @Inject(method = "doSendPacket", at = @At("TAIL"))
    private void handlePacket(Packet<?> packet, @Nullable ChannelFutureListener listener, boolean flush, CallbackInfo ci) {
        PacketLogger.write(channel, packet);
    }
}
