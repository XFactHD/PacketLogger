package xfacthd.packetlogger.mixin;

import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.packetlogger.event.ClientEvents;

@Mixin(targets = "net/minecraft/network/Connection$1")
public class MixinConnectionAnonOne
{
    @Final
    @Shadow
    Connection val$pConnection;

    @Inject(method = "initChannel", at = @At("TAIL"), remap = false)
    private void packetlogger$capturePlayerConnectionInit(Channel channel, CallbackInfo ci)
    {
        ClientEvents.onClientStartConnecting(val$pConnection, channel.pipeline());
    }
}
