package xfacthd.packetlogger.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import xfacthd.packetlogger.event.ClientEvents;

@Mixin(targets = "net/minecraft/network/Connection$1")
public class MixinConnectionAnonOne
{
    @WrapOperation(method = "initChannel", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;configurePacketHandler(Lio/netty/channel/ChannelPipeline;)V"))
    private static void packetlogger$capturePlayerConnectionInit(Connection connection, ChannelPipeline pipeline, Operation<Void> operation)
    {
        operation.call(connection, pipeline);
        ClientEvents.onClientStartConnecting(connection, pipeline);
    }
}
