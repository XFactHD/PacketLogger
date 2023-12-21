package xfacthd.packetlogger.event;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import xfacthd.packetlogger.logger.PacketLogHandler;

public final class ServerEvents
{
    public static void init(IEventBus modBus)
    {
        modBus.addListener(ServerEvents::onPlayerDisconnect);
    }

    private static void onPlayerDisconnect(final PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer player)
        {
            PacketLogHandler.removeInterceptorOnLeave(player.connection.connection);
        }
    }



    private ServerEvents() { }
}
