package xfacthd.packetlogger.event;

import io.netty.channel.ChannelPipeline;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.Connection;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;
import xfacthd.packetlogger.logger.PacketLogContext;
import xfacthd.packetlogger.logger.PacketLogHandler;
import xfacthd.packetlogger.screen.ConfigureAutoAttachScreen;
import xfacthd.packetlogger.screen.LoggerIndicatorOverlay;
import xfacthd.packetlogger.utils.Utils;

public final class ClientEvents
{
    private static boolean autoAttachArmed = false;
    private static PacketLogContext autoAttachContext = null;

    public static void init(IEventBus modBus)
    {
        modBus.addListener(ClientEvents::onRegisterGuiOverlays);

        NeoForge.EVENT_BUS.addListener(ClientEvents::onWorldSelectOrMultiplayerScreenInit);
        NeoForge.EVENT_BUS.addListener(ClientEvents::onWorldSelectOrMultiplayerScreenOpen);
        NeoForge.EVENT_BUS.addListener(ClientEvents::onClientDisconnect);
    }

    private static void onRegisterGuiOverlays(final RegisterGuiOverlaysEvent event)
    {
        event.registerAboveAll(Utils.rl("logger_state"), new LoggerIndicatorOverlay());
    }

    private static void onWorldSelectOrMultiplayerScreenOpen(final ScreenEvent.Opening event)
    {
        Screen screen = event.getScreen();
        if (screen instanceof JoinMultiplayerScreen || screen instanceof SelectWorldScreen)
        {
            // Reset auto-attach
            autoAttachArmed = false;
            autoAttachContext = null;
        }
    }

    private static void onWorldSelectOrMultiplayerScreenInit(final ScreenEvent.Init.Post event)
    {
        Screen screen = event.getScreen();
        if (screen instanceof JoinMultiplayerScreen || screen instanceof SelectWorldScreen)
        {
            ConfigureAutoAttachScreen.injectButton(event.getScreen(), event::addListener);
        }
    }

    public static void onClientStartConnecting(Connection connection, ChannelPipeline pipeline)
    {
        if (autoAttachArmed)
        {
            PacketLogHandler.startFromClientsideAutoAttach(autoAttachContext, connection, pipeline);
            autoAttachArmed = false;
            autoAttachContext = null;
        }
    }

    private static void onClientDisconnect(final ClientPlayerNetworkEvent.LoggingOut event)
    {
        PacketLogHandler.stop(true);
    }

    public static void armAutoAttach(PacketLogContext ctx)
    {
        autoAttachArmed = true;
        autoAttachContext = ctx;
    }

    public static boolean isAutoAttachArmed()
    {
        return autoAttachArmed;
    }

    public static PacketLogContext getAutoAttachContext()
    {
        return autoAttachContext;
    }



    private ClientEvents() { }
}
