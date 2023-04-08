package xfacthd.packetlogger.logger.adapters;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import xfacthd.packetlogger.logger.PacketLogAdapter;
import xfacthd.packetlogger.logger.PacketLogContext;
import xfacthd.packetlogger.logger.data.*;
import xfacthd.packetlogger.utils.PacketPrinter;
import xfacthd.packetlogger.utils.Utils;

public final class SensitiveDataPacketLogAdapter implements PacketLogAdapter<Packet<?>>
{
    public static final SensitiveDataPacketLogAdapter INSTANCE = new SensitiveDataPacketLogAdapter();

    private SensitiveDataPacketLogAdapter() { }

    @Override
    public PacketInfo analyze(Packet<?> pkt, PacketLogContext logCtx)
    {
        PacketSize size = PacketSize.empty();
        if (logCtx.logSize())
        {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

            int start = buf.writerIndex();
            pkt.write(buf);
            int count = buf.writerIndex() - start;

            buf.release();

            size = PacketSize.single(count);
        }
        return PacketInfo.single(size, PacketDump.EMPTY);
    }

    @Override
    public void print(PacketPrinter printer, PacketLogContext logCtx, PacketLogEntry entry)
    {
        if (logCtx.logSize())
        {
            int size = entry.info().getSize().getSize();
            printer.append("Size: ").append(Utils.formatByteCount(size)).newLine();
        }
        if (logCtx.hexDump())
        {
            printer.append("Dump: [REDACTED]").newLine();
        }
    }
}
