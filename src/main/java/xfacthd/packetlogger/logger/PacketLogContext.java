package xfacthd.packetlogger.logger;

import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import xfacthd.packetlogger.utils.Utils;

import java.lang.ref.WeakReference;

public record PacketLogContext(
        WeakReference<CommandSource> source, boolean inbound, boolean outbound, boolean fullLog, boolean logSize, boolean hexDump, int timeLimit, String[] filters
)
{
    public static final String VALUE_TIME_SECONDS = Utils.translateKey("value", "time.seconds");
    public static final Component VALUE_TIME_UNLIMITED = Utils.translate("value", "time.unlimited");

    public Object[] toMessageArgs()
    {
        return new Object[] {
                Utils.logBool(inbound),
                Utils.logBool(outbound),
                Utils.logBool(fullLog),
                Utils.logBool(logSize),
                Utils.logBool(hexDump),
                timeLimit > -1 ? Component.translatable(VALUE_TIME_SECONDS, timeLimit) : VALUE_TIME_UNLIMITED
        };
    }
}
