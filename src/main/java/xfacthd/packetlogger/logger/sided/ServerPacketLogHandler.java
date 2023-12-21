package xfacthd.packetlogger.logger.sided;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import xfacthd.packetlogger.PacketLogger;
import xfacthd.packetlogger.logger.PacketLogContext;
import xfacthd.packetlogger.logger.PacketLogHandler;

@Mod.EventBusSubscriber(modid = PacketLogger.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public final class ServerPacketLogHandler extends PacketLogHandler
{
    ServerPacketLogHandler(PacketLogContext logCtx)
    {
        super(logCtx);
    }

    @Override
    protected void installInterceptors()
    {
        ServerLifecycleHooks.getCurrentServer()
                .getPlayerList()
                .getPlayers()
                .forEach(player -> installInterceptor(player.connection.connection));
    }

    @Override
    protected void removeInterceptors()
    {
        ServerLifecycleHooks.getCurrentServer()
                .getPlayerList()
                .getPlayers()
                .forEach(player -> removeInterceptor(player.connection.connection));
    }

    @SubscribeEvent
    public static void onServerTick(final TickEvent.ServerTickEvent event)
    {
        if (getInstance() instanceof ServerPacketLogHandler handler)
        {
            handler.tick();
        }
    }
}
