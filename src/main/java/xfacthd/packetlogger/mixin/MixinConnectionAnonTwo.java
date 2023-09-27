package xfacthd.packetlogger.mixin;

import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.packetlogger.event.ClientEvents;

@Mixin(targets = "net/minecraft/network/Connection$2")
public class MixinConnectionAnonTwo
{
    @Final
    @Shadow
    Connection val$connection;

    @Inject(method = "initChannel", at = @At("TAIL"), remap = false)
    private void packetlogger$capturePlayerConnectionInit(Channel channel, CallbackInfo ci)
    {
        ClientEvents.onClientStartConnecting(val$connection, channel.pipeline());
    }
}
