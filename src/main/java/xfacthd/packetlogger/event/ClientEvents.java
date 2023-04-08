package xfacthd.packetlogger.event;

import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import xfacthd.packetlogger.logger.PacketLogHandler;

public class ClientEvents
{
    public static void init()
    {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(ClientEvents::onClientDisconnect);
    }

    private static void onClientDisconnect(final ClientPlayerNetworkEvent.LoggingOut event)
    {
        PacketLogHandler.stop(true);
    }
}
