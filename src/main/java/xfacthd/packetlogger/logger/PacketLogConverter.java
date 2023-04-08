package xfacthd.packetlogger.logger;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import xfacthd.packetlogger.logger.adapters.DefaultPacketLogAdapter;
import xfacthd.packetlogger.logger.data.*;
import xfacthd.packetlogger.utils.PacketPrinter;

import java.util.IdentityHashMap;
import java.util.Map;

public final class PacketLogConverter
{
    private static final Map<Class<? extends Packet<?>>, PacketLogAdapter<?>> LOG_ADAPTERS = new IdentityHashMap<>();
    public static final PacketLogAdapter<Packet<?>> DEFAULT_ADAPTER = new DefaultPacketLogAdapter();

    @SuppressWarnings("unchecked")
    public static <T extends Packet<?>> PacketLogEntry toLogEntry(PacketLogContext logCtx, PacketLogEntry.Type type, Connection con, T pkt)
    {
        PacketLogAdapter<T> adapter = getAdapter(pkt);
        return new PacketLogEntry(
                System.currentTimeMillis(),
                type,
                (Class<? extends Packet<?>>) pkt.getClass(),
                con.hashCode(),
                adapter.analyze(pkt, logCtx)
        );
    }

    public static void printPacket(PacketPrinter printer, PacketLogContext logCtx, PacketLogEntry entry)
    {
        getAdapter(entry.pktClazz()).print(printer, logCtx, entry);
    }

    public static <T extends Packet<?>> PacketLogAdapter<T> registerAdapter(Class<? extends T> pktClazz, PacketLogAdapter<T> adapter)
    {
        LOG_ADAPTERS.put(pktClazz, adapter);
        return adapter;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Packet<?>> PacketLogAdapter<T> getAdapter(T pkt)
    {
        return (PacketLogAdapter<T>) LOG_ADAPTERS.getOrDefault(pkt.getClass(), DEFAULT_ADAPTER);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Packet<?>> PacketLogAdapter<T> getAdapter(Class<T> pkt)
    {
        return (PacketLogAdapter<T>) LOG_ADAPTERS.getOrDefault(pkt, DEFAULT_ADAPTER);
    }



    private PacketLogConverter() {}
}
