package xfacthd.packetlogger.logger.adapters;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import xfacthd.packetlogger.logger.PacketLogAdapter;
import xfacthd.packetlogger.logger.PacketLogContext;
import xfacthd.packetlogger.logger.data.*;
import xfacthd.packetlogger.utils.PacketPrinter;
import xfacthd.packetlogger.utils.Utils;

public final class DefaultPacketLogAdapter implements PacketLogAdapter<Packet<?>>
{
    @Override
    public PacketInfo analyze(Packet<?> pkt, PacketLogContext logCtx)
    {
        boolean logSize = logCtx.logSize();
        boolean hexDump = logCtx.hexDump();

        if (!logSize && !hexDump)
        {
            return PacketInfo.empty();
        }

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        int start = buf.writerIndex();
        pkt.write(buf);
        int count = buf.writerIndex() - start;

        PacketSize size = logSize ? PacketSize.single(count) : PacketSize.empty();
        PacketDump dump = hexDump ? new PacketDump(buf, count) : PacketDump.EMPTY;

        buf.release();

        return PacketInfo.single(size, dump);
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
            printer.append("Dump:").newLine();
            Utils.printHexDump(printer, entry.info().getDump());
        }
    }
}
