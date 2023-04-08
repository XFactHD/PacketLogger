package xfacthd.packetlogger.logger.sided;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.packetlogger.PacketLogger;
import xfacthd.packetlogger.logger.PacketLogContext;
import xfacthd.packetlogger.logger.PacketLogHandler;

@Mod.EventBusSubscriber(modid = PacketLogger.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientPacketLogHandler extends PacketLogHandler
{
    ClientPacketLogHandler(PacketLogContext logCtx)
    {
        super(logCtx);
    }

    @Override
    protected void installInterceptors()
    {
        installInterceptor(Minecraft.getInstance().player.connection.getConnection());
    }

    @Override
    protected void removeInterceptors()
    {
        removeInterceptor(Minecraft.getInstance().player.connection.getConnection());
    }

    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event)
    {
        if (getInstance() instanceof ClientPacketLogHandler handler)
        {
            handler.tick();
        }
    }
}
