package xfacthd.packetlogger.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import xfacthd.packetlogger.logger.PacketLogHandler;

public class ServerEvents
{
    public static void init()
    {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(ServerEvents::onPlayerConnect);
        bus.addListener(ServerEvents::onPlayerDisconnect);
    }

    // Abuse OnDatapackSyncEvent for interceptor registration because it somehow allows capturing packets
    // immediately while PlayerEvent.PlayerLoggedInEvent does not
    private static void onPlayerConnect(final OnDatapackSyncEvent event)
    {
        ServerPlayer player = event.getPlayer();
        if (player != null)
        {
            PacketLogHandler.installInterceptorOnJoin(player.connection.connection);
        }
    }

    private static void onPlayerDisconnect(final PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer player)
        {
            PacketLogHandler.removeInterceptorOnLeave(player.connection.connection);
        }
    }
}
