package xfacthd.packetlogger.logger;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import io.netty.channel.ChannelPipeline;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import xfacthd.packetlogger.PacketLogger;
import xfacthd.packetlogger.logger.data.PacketLogEntry;
import xfacthd.packetlogger.logger.sided.ClientPacketLogHandlerFactory;
import xfacthd.packetlogger.logger.sided.ServerPacketLogHandlerFactory;
import xfacthd.packetlogger.logger.pipeline.LoggingDecoder;
import xfacthd.packetlogger.logger.pipeline.LoggingEncoder;
import xfacthd.packetlogger.utils.Utils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class PacketLogHandler
{
    public static final boolean INBOUND_DEFAULT = true;
    public static final boolean OUTBOUND_DEFAULT = true;
    public static final boolean FULL_LOG_DEFAULT = false;
    public static final boolean LOG_SIZE_DEFAULT = false;
    public static final boolean HEXDUMP_DEFAULT = false;
    public static final Component MSG_ALREADY_RUNNING = Utils.translate("msg", "logger.already_running");
    public static final Component MSG_NOT_RUNNING = Utils.translate("msg", "logger.not_running");
    public static final Component MSG_NOTHING_TO_CAPTURE = Utils.translate("msg", "logger.nothing_to_capture");
    public static final Component MSG_HEX_DUMP_WITHOUT_FULL_LOG = Utils.translate("msg", "logger.hexdump_without_full_log");
    public static final String MSG_INVALID_FILTER = Utils.translateKey("msg", "logger.invalid_filter");
    public static final String MSG_STARTED = Utils.translateKey("msg", "logger.started");
    public static final Component MSG_STOPPED = Utils.translate("msg", "logger.stopped");
    public static final Component MSG_STOPPED_ASYNC = Utils.translate("msg", "logger.stopped_async");
    private static final String NAME_INBOUND_INTERCEPTOR = PacketLogger.MODID + ":" + "log_inbound";
    private static final String NAME_OUTBOUND_INTERCEPTOR = PacketLogger.MODID + ":" + "log_outbound";
    private static final Map<String, Class<?>> PACKET_BY_SHORT_NAME = collectPacketTypes();
    private static final List<String> PACKET_NAMES = PACKET_BY_SHORT_NAME.keySet().stream().sorted().toList();

    private static final AtomicBoolean LOCKED = new AtomicBoolean();
    private static boolean running = false;
    private static long startTime = 0;
    private static PacketLogHandler instance = null;

    protected final PacketLogContext logCtx;
    private final Set<Class<?>> typeFilter = Sets.newIdentityHashSet();
    private final List<PacketLogEntry> logEntries = new LinkedList<>();
    private boolean filtered = false;

    protected PacketLogHandler(PacketLogContext logCtx)
    {
        this.logCtx = logCtx;
    }

    public static int start(CommandSourceStack src, PacketLogContext logCtx)
    {
        if (running || LOCKED.get())
        {
            src.sendFailure(MSG_ALREADY_RUNNING);
            return 0;
        }

        if (!logCtx.inbound() && !logCtx.outbound())
        {
            src.sendFailure(MSG_NOTHING_TO_CAPTURE);
            return 0;
        }

        if (!logCtx.fullLog() && logCtx.hexDump())
        {
            src.sendFailure(MSG_HEX_DUMP_WITHOUT_FULL_LOG);
            return 0;
        }

        Set<Class<?>> filters = new HashSet<>();
        for (String filterString : logCtx.filters())
        {
            Class<?> filterType = PACKET_BY_SHORT_NAME.get(filterString);
            if (filterType == null)
            {
                src.sendFailure(Component.translatable(MSG_INVALID_FILTER, filterString));
                return 0;
            }
            filters.add(filterType);
        }

        if (FMLEnvironment.dist.isDedicatedServer())
        {
            instance = ServerPacketLogHandlerFactory.create(logCtx);
        }
        else
        {
            instance = ClientPacketLogHandlerFactory.create(logCtx);
        }
        instance.installInterceptors();

        instance.filtered = filters.size() > 0;
        instance.typeFilter.addAll(filters);

        running = true;
        startTime = System.currentTimeMillis();

        src.sendSuccess(Component.translatable(MSG_STARTED, logCtx.toMessageArgs()), true);
        return 1;
    }

    public static int stop(CommandSourceStack src)
    {
        if (!running)
        {
            src.sendFailure(MSG_NOT_RUNNING);
            return 0;
        }

        boolean async = stop(false);
        src.sendSuccess(async ? MSG_STOPPED_ASYNC : MSG_STOPPED, true);
        return 1;
    }

    public static boolean stop(boolean sendMsg)
    {
        if (running)
        {
            running = false;
            long endTime = System.currentTimeMillis();
            instance.removeInterceptors();
            PacketLogContext logCtx = instance.logCtx;
            boolean async = PacketLogWriter.collectAndOutput(logCtx, startTime, endTime, instance.logEntries, LOCKED);
            instance = null;

            if (sendMsg)
            {
                Utils.ifPresent(logCtx.source(), src -> src.sendSystemMessage(async ? MSG_STOPPED_ASYNC : MSG_STOPPED));
            }

            return async;
        }
        return false;
    }

    public static List<String> getPacketTypeNames()
    {
        return PACKET_NAMES;
    }

    protected static PacketLogHandler getInstance()
    {
        return instance;
    }

    public static void installInterceptorOnJoin(Connection connection)
    {
        if (!running) { return; }
        instance.installInterceptor(connection);
    }

    public static void removeInterceptorOnLeave(Connection connection)
    {
        if (!running) { return; }
        instance.removeInterceptor(connection);
    }

    public static void logInbound(Connection connection, Packet<?> packet)
    {
        PacketLogger.LOGGER.warn("Inbound packet about to be captured");
        if (!running) { return; }
        instance.logInboundPacket(connection, packet);
    }

    public static void logOutbound(Connection connection, Packet<?> packet)
    {
        PacketLogger.LOGGER.warn("Outbound packet about to be captured");
        if (!running) { return; }
        instance.logOutboundPacket(connection, packet);
    }



    protected abstract void installInterceptors();

    protected abstract void removeInterceptors();

    protected final void installInterceptor(Connection connection)
    {
        ChannelPipeline pipeline = connection.channel().pipeline();
        if (logCtx.inbound())
        {
            pipeline.addBefore("packet_handler", NAME_INBOUND_INTERCEPTOR, new LoggingDecoder(connection));
        }
        if (logCtx.outbound())
        {
            pipeline.addBefore("packet_handler", NAME_OUTBOUND_INTERCEPTOR, new LoggingEncoder(connection));
        }

        PacketLogger.LOGGER.warn(
                "Interceptor installed. Pipeline: {}",
                String.join(" -> ", pipeline.names())
        );
    }

    protected final void removeInterceptor(Connection connection)
    {
        ChannelPipeline pipeline = connection.channel().pipeline();
        if (logCtx.inbound())
        {
            pipeline.remove(NAME_INBOUND_INTERCEPTOR);
        }
        if (logCtx.outbound())
        {
            pipeline.remove(NAME_OUTBOUND_INTERCEPTOR);
        }

        PacketLogger.LOGGER.warn(
                "Interceptor removed. Pipeline: {}",
                String.join(" -> ", pipeline.names())
        );
    }

    protected final void tick()
    {
        int timeLimit = logCtx.timeLimit();
        if (timeLimit != -1 && System.currentTimeMillis() - startTime > (timeLimit * 1000L))
        {
            stop(true);
        }
    }

    private void logInboundPacket(Connection connection, Packet<?> packet)
    {
        if (filtered && !typeFilter.contains(packet.getClass()))
        {
            return;
        }
        logEntries.add(PacketLogConverter.toLogEntry(logCtx, PacketLogEntry.Type.INBOUND, connection, packet));
        PacketLogger.LOGGER.warn("Inbound packet captured");
    }

    private void logOutboundPacket(Connection connection, Packet<?> packet)
    {
        if (filtered && !typeFilter.contains(packet.getClass()))
        {
            return;
        }
        logEntries.add(PacketLogConverter.toLogEntry(logCtx, PacketLogEntry.Type.OUTBOUND, connection, packet));
        PacketLogger.LOGGER.warn("Outbound packet captured");
    }

    private static Map<String, Class<?>> collectPacketTypes()
    {
        Map<Class<? extends Packet<?>>, ConnectionProtocol> protocolByPacket = ObfuscationReflectionHelper.getPrivateValue(
                ConnectionProtocol.class, null, "f_129572_"
        );
        Preconditions.checkNotNull(protocolByPacket, "ConnectionProtocol#PROTOCOL_BY_PACKET == null");

        Map<String, Class<?>> packetByShortName = new HashMap<>();
        protocolByPacket.keySet().forEach(pktClazz ->
        {
            String name = Utils.printClassName(pktClazz);
            packetByShortName.put(name, pktClazz);
        });
        return packetByShortName;
    }
}
