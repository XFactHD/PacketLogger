package xfacthd.packetlogger.datagen.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.common.data.LanguageProvider;
import xfacthd.packetlogger.PacketLogger;
import xfacthd.packetlogger.cmd.PacketLogCommand;
import xfacthd.packetlogger.logger.*;
import xfacthd.packetlogger.screen.ConfigureAutoAttachScreen;
import xfacthd.packetlogger.screen.LoggerIndicatorOverlay;
import xfacthd.packetlogger.utils.Utils;

public class PacketLogLanguageProvider extends LanguageProvider
{
    public PacketLogLanguageProvider(PackOutput output)
    {
        super(output, PacketLogger.MODID, "en_us");
    }

    @Override
    protected void addTranslations()
    {
        add(PacketLogHandler.MSG_ALREADY_RUNNING, "Packet logger is already running");
        add(PacketLogHandler.MSG_NOT_RUNNING, "Packet logger has not been started");
        add(PacketLogHandler.MSG_NOTHING_TO_CAPTURE, "Can't disable both inbound and outbound packet logging");
        add(PacketLogHandler.MSG_HEX_DUMP_WITHOUT_FULL_LOG, "Can't use hex dump without full packet logging");
        add(PacketLogHandler.MSG_INVALID_FILTER, "Invalid filter entry: %s");
        add(PacketLogHandler.MSG_STARTED, "Packet logger started (inbound: %s, outbound: %s, log packets: %s, log size: %s, hex dump: %s, time limit: %s)");
        add(PacketLogHandler.MSG_STOPPED, "Packet logger stopped and log saved");
        add(PacketLogHandler.MSG_STOPPED_ASYNC, "Packet logger stopped. Log output may be delayed due to amount of logged data");
        add(PacketLogWriter.MSG_EXPORT_COMPLETE, "Packet log saved");
        add(PacketLogCommand.MSG_FILTERS_OPTIONS, "Packet filter types [%d] <page %s / %s> :");
        add(Utils.VALUE_FALSE, "false");
        add(Utils.VALUE_TRUE, "true");
        add(PacketLogContext.VALUE_TIME_SECONDS, "%s seconds");
        add(PacketLogContext.VALUE_TIME_UNLIMITED, "unlimited");
        add(ConfigureAutoAttachScreen.TITLE, "PacketLogger Auto-Attach Config");
        add(ConfigureAutoAttachScreen.BTN_TOOLTIP_DISARMED, "PacketLogger Auto-Attach: Disarmed");
        add(ConfigureAutoAttachScreen.BTN_TOOLTIP_ARMED, "PacketLogger Auto-Attach: Armed");
        add(ConfigureAutoAttachScreen.LINES[0], "Capture inbound:");
        add(ConfigureAutoAttachScreen.LINES[1], "Capture outbound:");
        add(ConfigureAutoAttachScreen.LINES[2], "Detailed logging:");
        add(ConfigureAutoAttachScreen.LINES[3], "Log packet size:");
        add(ConfigureAutoAttachScreen.LINES[4], "Print hex dump");
        add(ConfigureAutoAttachScreen.LINE_FILTERS, "Packet filter (comma-separated):");
        add(ConfigureAutoAttachScreen.MSG_INVALID_FILTERS, "Invalid filters: %s");
        add(ConfigureAutoAttachScreen.BTN_TOOLTIP_NO_CAPTURE, "Nothing to capture, enable at least one of inbound or outbound");
        add(LoggerIndicatorOverlay.MSG_IDLE, "Idle");
        add(LoggerIndicatorOverlay.MSG_RECORDING, "Recording");
    }

    private void add(Component key, String value)
    {
        ComponentContents contents = key.getContents();
        if (contents instanceof TranslatableContents translatable)
        {
            add(translatable.getKey(), value);
        }
        else
        {
            add(key.getString(), value);
        }
    }
}
