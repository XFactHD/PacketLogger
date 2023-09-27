package xfacthd.packetlogger.logger.adapters;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.resources.ResourceLocation;
import xfacthd.packetlogger.logger.PacketLogAdapter;
import xfacthd.packetlogger.logger.PacketLogContext;
import xfacthd.packetlogger.logger.data.*;
import xfacthd.packetlogger.utils.PacketPrinter;
import xfacthd.packetlogger.utils.Utils;

public abstract sealed class CustomQueryPacketLogAdapter<T extends Packet<?>> implements PacketLogAdapter<T>
{
    protected final PacketInfo analyze(T pkt, int transactionId, ResourceLocation id, FriendlyByteBuf data, PacketLogContext logCtx)
    {
        boolean logSize = logCtx.logSize();
        boolean hexDump = logCtx.hexDump();

        PacketSize size = PacketSize.empty();
        PacketDump dump = PacketDump.EMPTY;
        if (logSize || hexDump)
        {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

            int contStart = buf.writerIndex();
            buf.writeBytes(data.slice());
            int contCount = buf.writerIndex() - contStart;

            buf.resetWriterIndex();
            int fullStart = buf.writerIndex();
            pkt.write(buf);
            int fullCount = buf.writerIndex() - fullStart;

            if (logSize)
            {
                size = PacketSize.customQuery(fullCount, contCount);
            }
            if (hexDump)
            {
                dump = new PacketDump(buf, fullCount);
            }

            buf.release();
        }

        return PacketInfo.customQuery(transactionId, id, size, dump);
    }

    @Override
    public void print(PacketPrinter printer, PacketLogContext logCtx, PacketLogEntry entry)
    {
        if (entry.info() instanceof PacketInfo.CustomQuery customInfo)
        {
            printer.append("Transaction ID: ").append(Integer.toString(customInfo.getTransactionId())).newLine(true);
            if (entry.pktClazz() == ClientboundCustomQueryPacket.class)
            {
                // ServerboundCustomQueryPacket does not have a textual ID
                printer.append("ID: ").append(customInfo.getId().toString()).newLine(true);
            }
        }
        if (logCtx.logSize() && entry.info().getSize() instanceof PacketSize.CustomQuery customSize)
        {
            int fullSize = customSize.getSize();
            int contSize = customSize.getContainedSize();
            printer.append("Total size: ").append(Utils.formatByteCount(fullSize)).newLine(true);
            printer.append("Contained size: ").append(Utils.formatByteCount(contSize)).newLine(true);
        }
        if (logCtx.hexDump())
        {
            printer.append("Dump:").newLine();
            Utils.printHexDump(printer, entry.info().getDump());
        }
    }



    public static final class Clientbound extends CustomQueryPacketLogAdapter<ClientboundCustomQueryPacket>
    {
        @Override
        public PacketInfo analyze(ClientboundCustomQueryPacket pkt, PacketLogContext logCtx)
        {
            return analyze(pkt, pkt.getTransactionId(), pkt.getIdentifier(), pkt.getData(), logCtx);
        }
    }

    public static final class Serverbound extends CustomQueryPacketLogAdapter<ServerboundCustomQueryPacket>
    {
        @Override
        public PacketInfo analyze(ServerboundCustomQueryPacket pkt, PacketLogContext logCtx)
        {
            return analyze(pkt, pkt.getTransactionId(), null, pkt.getData(), logCtx);
        }
    }
}
