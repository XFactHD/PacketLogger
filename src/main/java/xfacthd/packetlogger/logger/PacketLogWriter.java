package xfacthd.packetlogger.logger;

import net.minecraft.Util;
import net.minecraft.network.CompressionDecoder;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.loading.FMLPaths;
import xfacthd.packetlogger.PacketLogger;
import xfacthd.packetlogger.logger.data.*;
import xfacthd.packetlogger.utils.PacketPrinter;
import xfacthd.packetlogger.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class PacketLogWriter
{
    public static final Component MSG_EXPORT_COMPLETE = Utils.translate("msg", "logger.export.complete");

    public static boolean collectAndOutput(
            PacketLogContext logCtx, long startTime, long endTime, List<PacketLogEntry> logEntries, AtomicBoolean locked
    )
    {
        // Printing large amounts of hex dumps (or very long logs in general) takes a long time, so do it
        // on a background thread to avoid timing out the watchdog
        if (logCtx.fullLog())
        {
            locked.set(true);
            Util.backgroundExecutor().submit(() ->
                    collectAndOutputMaybeAsync(logCtx, startTime, endTime, logEntries, locked)
            );
            return true;
        }
        else
        {
            collectAndOutputMaybeAsync(logCtx, startTime, endTime, logEntries, null);
            return false;
        }
    }

    private static void collectAndOutputMaybeAsync(
            PacketLogContext logCtx, long startTime, long endTime, List<PacketLogEntry> logEntries, AtomicBoolean locked
    )
    {
        long printStartTime = System.currentTimeMillis();

        Map<Class<?>, PacketStats> stats = new HashMap<>();
        logEntries.forEach(entry -> stats.computeIfAbsent(entry.pktClazz(), PacketStats::new).account(entry.info()));
        List<PacketStats> sortedStats = stats.values().stream().sorted().toList();

        long duration = endTime - startTime;
        Duration dur = Duration.ofMillis(duration);
        long minutes = dur.toMinutes();
        int seconds = dur.toSecondsPart();

        int countTotal = logEntries.size();
        long countInbound = logEntries.stream().filter(e -> e.type() == PacketLogEntry.Type.INBOUND).count();
        long countOutbound = logEntries.stream().filter(e -> e.type() == PacketLogEntry.Type.OUTBOUND).count();

        PacketPrinter printer = new PacketPrinter();
        printer.append("# Statistics").newLine().newLine()
                .append("Capture duration: ").append("%d:%02d".formatted(minutes, seconds)).newLine(true);
        if (logCtx.filters().length > 0)
        {
            printer.append("Capture filters:").newLine().newLine();
            for (String filter : logCtx.filters())
            {
                printer.append("- ").append(filter).newLine();
            }
            printer.newLine();
        }
        printer.append("Captured packets:").newLine().newLine()
                .append("- Total: ").append(Integer.toString(countTotal)).newLine()
                .append("- Inbound: ").append(Long.toString(countInbound)).newLine()
                .append("- Outbound: ").append(Long.toString(countOutbound)).newLine()
                .newLine();
        if (logCtx.logSize())
        {
            int totalSize = logEntries.stream().mapToInt(e -> e.info().getSize().getSize()).sum();
            printer.append("Captured size: ").append(Utils.formatByteCount(totalSize)).newLine(true);
        }

        printer.append("Stats by type:").newLine().newLine();
        sortedStats.forEach(stat ->
        {
            printer.append("- ").append(Utils.printClassName(stat.getPacketType())).newLine()
                    .incIndent()
                    .append("- Count: ").append(Integer.toString(stat.getCount())).newLine();

            if (logCtx.logSize())
            {
                printer.append("- Total size: ").append(Utils.formatByteCount(stat.getTotalSize())).newLine()
                        .append("- Min size: ").append(Utils.formatByteCount(stat.getMinSize())).newLine()
                        .append("- Max size: ").append(Utils.formatByteCount(stat.getMaxSize())).newLine()
                        .append("- Median size: ").append(Utils.formatByteCount(stat.getMedianSize())).newLine();
            }

            printer.decIndent();
        });

        if (logCtx.fullLog())
        {
            printer.newLine()
                    .append("# Packet log").newLine().newLine();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'['HH:mm:ss.SSS'] '");
            ZoneId zone = Clock.systemDefaultZone().getZone();
            logEntries.forEach(entry ->
            {
                LocalTime time = LocalTime.ofInstant(Instant.ofEpochMilli(entry.timestamp()), zone);
                printer.append(formatter.format(time)).append(Utils.printClassName(entry.pktClazz())).newLine(true)
                        .append("Direction: ").append(entry.type().print()).newLine(true);

                if (logCtx.logSize() && entry.info().getSize().getSize() > CompressionDecoder.MAXIMUM_UNCOMPRESSED_LENGTH)
                {
                    printer.append("Warning: Packet oversized").newLine(true);
                }

                PacketLogConverter.printPacket(printer, logCtx, entry);

                printer.newLine();
            });
        }

        try
        {
            Path dirPath = FMLPaths.GAMEDIR.get().resolve("logs/packet_logger/");
            FMLPaths.getOrCreateGameRelativePath(dirPath);

            Path path = dirPath.resolve("packet_log_%d.md".formatted(endTime));
            Files.writeString(path, printer.print());
        }
        catch (IOException e)
        {
            PacketLogger.LOGGER.error("Encountered an error while saving packet log", e);
        }

        if (locked != null)
        {
            locked.set(false);
            Utils.ifPresent(logCtx.source(), src -> src.sendSystemMessage(MSG_EXPORT_COMPLETE));
        }

        long printDuration = System.currentTimeMillis() - printStartTime;
        PacketLogger.LOGGER.info("Packet log exported, took {}ms", printDuration);
    }

    private PacketLogWriter() { }
}
