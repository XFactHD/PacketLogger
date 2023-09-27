package xfacthd.packetlogger.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import xfacthd.packetlogger.logger.PacketLogHandler;

public final class ServerEvents
{
    public static void init()
    {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(ServerEvents::onPlayerDisconnect);
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
