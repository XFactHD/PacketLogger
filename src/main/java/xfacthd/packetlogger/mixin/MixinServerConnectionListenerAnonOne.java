package xfacthd.packetlogger.mixin;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xfacthd.packetlogger.logger.PacketLogHandler;

@Debug(export = true)
@SuppressWarnings("MethodMayBeStatic")
@Mixin(targets = "net/minecraft/server/network/ServerConnectionListener$1")
public class MixinServerConnectionListenerAnonOne
{
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(method = "initChannel", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private void packetlogger$capturePlayerConnectionInit(
            Channel channel, CallbackInfo ci, ChannelPipeline pipeline, int rateLimit, Connection connection
    )
    {
        PacketLogHandler.installInterceptorOnJoin(connection, pipeline);
    }
}
