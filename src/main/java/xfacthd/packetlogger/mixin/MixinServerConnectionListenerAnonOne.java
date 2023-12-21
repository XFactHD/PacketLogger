package xfacthd.packetlogger.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xfacthd.packetlogger.logger.PacketLogHandler;

@Debug(export = true)
@Mixin(targets = "net/minecraft/server/network/ServerConnectionListener$1")
public class MixinServerConnectionListenerAnonOne
{
    @WrapOperation(method = "initChannel", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;configurePacketHandler(Lio/netty/channel/ChannelPipeline;)V"))
    private static void packetlogger$capturePlayerConnectionInit(Connection connection, ChannelPipeline pipeline, Operation<Void> operation)
    {
        operation.call(connection, pipeline);
        PacketLogHandler.installInterceptorOnJoin(connection, pipeline);
    }
}
