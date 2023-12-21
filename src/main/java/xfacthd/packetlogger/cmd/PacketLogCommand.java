package xfacthd.packetlogger.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import xfacthd.packetlogger.logger.PacketLogContext;
import xfacthd.packetlogger.logger.PacketLogHandler;
import xfacthd.packetlogger.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * <b>Usage:</b> /pktlog start [<inbound> <outbound>] [log packets] [log size] [hex dump] [time limit] [filters] <br>
 * <b>Description:</b> Start logging packets with the given configuration <br>
 * <b>Argument 'inbound':</b> If true, packets being received are logged (optional, default true) <br>
 * <b>Argument 'outbound':</b> If true, packets being sent are logged (optional, but required when 'inbound' is specified, default true) <br>
 * <b>Argument 'full_log':</b> If true, the individual packets are logged, otherwise only stats are logged (optional, default false) <br>
 * <b>Argument 'log_size':</b> If true, the exact size of the individual packets is logged (optional, default false) <br>
 * <b>Argument 'hex_dump':</b> If true, the raw packet data of the individual packets is logged (optional, default false) <br>
 * <b>Argument 'time_limit':</b> The capture time limit in seconds or -1 for unlimited time (optional, default -1) <br>
 * <b>Argument 'filters':</b> A space-separated list of packet types to log (optional, default empty for unfiltered logging) <br>
 * <b>Warning:</b> The 'hex_dump' option produces very large amounts of data and may fill up RAM very fast, you have been warned! <br>
 * <br>
 * <b>Usage:</b> /pktlog stop <br>
 * <b>Description:</b> Stop logging packets and save the logged data to disk <br>
 * <br>
 * <b>Usage:</b> /pktlog filters
 * <b>Description:</b> List all possible packet type filters to use with the 'filters' argument <br>
 */
public final class PacketLogCommand
{
    private static final String CLIENT_PREFIX = "pktlogc";
    private static final String SERVER_PREFIX = "pktlog";
    private static final String LITERAL_PRINT_FILTERS = "filters";
    private static final String LITERAL_START = "start";
    private static final String LITERAL_STOP = "stop";
    private static final String ARG_PAGE = "page";
    private static final String ARG_INBOUND = "inbound";
    private static final String ARG_OUTBOUND = "outbound";
    private static final String ARG_FULL_LOG = "full_log";
    private static final String ARG_LOG_SIZE = "log_size";
    private static final String ARG_HEXDUMP = "hexdump";
    private static final String ARG_TIME = "time_limit";
    private static final String ARG_FILTERS = "filters";
    private static final int FILTER_PAGE_SIZE = 9;
    public static final String MSG_FILTERS_OPTIONS = Utils.translateKey("msg", "cmd.filters.options");

    public static void register()
    {
        if (FMLEnvironment.dist.isDedicatedServer())
        {
            NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) ->
                    registerCommand(event.getDispatcher(), SERVER_PREFIX, true)
            );
        }
        else
        {
            NeoForge.EVENT_BUS.addListener((RegisterClientCommandsEvent event) ->
                    registerCommand(event.getDispatcher(), CLIENT_PREFIX, false)
            );
        }
    }

    private static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, String prefix, boolean requireOp)
    {
        dispatcher.register(Commands.literal(prefix)
                .requires(src -> !requireOp || src.hasPermission(Commands.LEVEL_OWNERS))
                .then(Commands.literal(LITERAL_PRINT_FILTERS)
                        .executes(ctx -> printFilters(ctx, false))
                        .then(Commands.argument(ARG_PAGE, IntegerArgumentType.integer(1, getFilterPageCount()))
                                .executes(ctx -> printFilters(ctx, true))
                        )
                )
                .then(Commands.literal(LITERAL_START)
                        .executes(ctx -> startLogger(ctx, 0))
                        .then(Commands.argument(ARG_INBOUND, BoolArgumentType.bool())
                                .then(Commands.argument(ARG_OUTBOUND, BoolArgumentType.bool())
                                        .executes(ctx -> startLogger(ctx, 2))
                                        .then(Commands.argument(ARG_FULL_LOG, BoolArgumentType.bool())
                                                .executes(ctx -> startLogger(ctx, 3))
                                                .then(Commands.argument(ARG_LOG_SIZE, BoolArgumentType.bool())
                                                        .executes(ctx -> startLogger(ctx, 4))
                                                        .then(Commands.argument(ARG_HEXDUMP, BoolArgumentType.bool())
                                                                .executes(ctx -> startLogger(ctx, 5))
                                                                .then(Commands.argument(ARG_TIME, IntegerArgumentType.integer(-1))
                                                                        .executes(ctx -> startLogger(ctx, 6))
                                                                        .then(Commands.argument(ARG_FILTERS, StringArgumentType.greedyString())
                                                                                .suggests(PacketLogCommand::suggestPacketTypeNames)
                                                                                .executes(ctx -> startLogger(ctx, 7))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal(LITERAL_STOP)
                        .executes(ctx -> PacketLogHandler.stop(ctx.getSource()))
                )
        );
    }

    private static int printFilters(CommandContext<CommandSourceStack> ctx, boolean pageArg)
    {
        List<String> types = PacketLogHandler.getPacketTypeNames();

        int count = types.size();
        int page = pageArg ? IntegerArgumentType.getInteger(ctx, ARG_PAGE) : 1;
        int pageCount = getFilterPageCount();

        MutableComponent msg = Component.translatable(MSG_FILTERS_OPTIONS, count, page, pageCount);

        int start = (page - 1) * FILTER_PAGE_SIZE;
        for (int i = start; i < start + FILTER_PAGE_SIZE && i < count; i++)
        {
            msg.append("\n - ").append(types.get(i));
        }

        ctx.getSource().sendSuccess(() -> msg, false);

        return 0;
    }

    private static int getFilterPageCount()
    {
        int size = PacketLogHandler.getPacketTypeNames().size();
        int count = size / FILTER_PAGE_SIZE;
        if (size % FILTER_PAGE_SIZE != 0)
        {
            count++;
        }
        return count;
    }

    private static int startLogger(CommandContext<CommandSourceStack> ctx, int argc)
    {
        boolean inbound = PacketLogHandler.INBOUND_DEFAULT;
        boolean outbound = PacketLogHandler.OUTBOUND_DEFAULT;
        boolean fullLog = PacketLogHandler.FULL_LOG_DEFAULT;
        boolean logSize = PacketLogHandler.LOG_SIZE_DEFAULT;
        boolean hexDump = PacketLogHandler.HEXDUMP_DEFAULT;
        int time = -1;
        String[] filters = new String[0];

        if (argc >= 2)
        {
            inbound = BoolArgumentType.getBool(ctx, ARG_INBOUND);
            outbound = BoolArgumentType.getBool(ctx, ARG_OUTBOUND);
        }
        if (argc >= 3)
        {
            fullLog = BoolArgumentType.getBool(ctx, ARG_FULL_LOG);
        }
        if (argc >= 4)
        {
            logSize = BoolArgumentType.getBool(ctx, ARG_LOG_SIZE);
        }
        if (argc >= 5)
        {
            hexDump = BoolArgumentType.getBool(ctx, ARG_HEXDUMP);
        }
        if (argc >= 6)
        {
            time = IntegerArgumentType.getInteger(ctx, ARG_TIME);
        }
        if (argc == 7)
        {
            filters = StringArgumentType.getString(ctx, ARG_FILTERS).split(" ");
        }

        return PacketLogHandler.start(ctx.getSource(), new PacketLogContext(
                new WeakReference<>(ctx.getSource().source), inbound, outbound, fullLog, logSize, hexDump, time, filters
        ));
    }

    private static CompletableFuture<Suggestions> suggestPacketTypeNames(
            CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder
    )
    {
        String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
        Set<String> existing = new HashSet<>();

        int idx = remaining.lastIndexOf(' ');
        String startedValue = remaining;
        if (idx > -1)
        {
            existing.addAll(Arrays.asList(remaining.substring(0, idx).split(" ")));
            startedValue = remaining.substring(idx + 1);
            builder = builder.createOffset(builder.getStart() + idx + 1);
        }

        for (String entry : PacketLogHandler.getPacketTypeNames())
        {
            String entryLC = entry.toLowerCase(Locale.ROOT);
            if (!existing.contains(entryLC) && SharedSuggestionProvider.matchesSubStr(startedValue, entryLC))
            {
                builder.suggest(entry);
            }
        }

        return builder.buildFuture();
    }



    private PacketLogCommand() { }
}
