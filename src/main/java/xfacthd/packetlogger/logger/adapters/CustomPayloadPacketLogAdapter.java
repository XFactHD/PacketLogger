package xfacthd.packetlogger.logger.adapters;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import xfacthd.packetlogger.logger.PacketLogAdapter;
import xfacthd.packetlogger.logger.PacketLogContext;
import xfacthd.packetlogger.logger.data.*;
import xfacthd.packetlogger.utils.PacketPrinter;
import xfacthd.packetlogger.utils.Utils;

public abstract sealed class CustomPayloadPacketLogAdapter<T extends Packet<?>> implements PacketLogAdapter<T>
        permits CustomPayloadPacketLogAdapter.Clientbound, CustomPayloadPacketLogAdapter.Serverbound
{
    protected final PacketInfo analyze(T pkt, ResourceLocation id, PacketLogContext logCtx)
    {
        boolean logSize = logCtx.logSize();
        boolean hexDump = logCtx.hexDump();

        PacketSize size = PacketSize.empty();
        PacketDump dump = PacketDump.EMPTY;
        if (logSize || hexDump)
        {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

            int fullStart = buf.writerIndex();
            pkt.write(buf);
            int fullCount = buf.writerIndex() - fullStart;
            buf.readResourceLocation();
            int contCount = fullCount - buf.readerIndex();

            if (logSize)
            {
                size = PacketSize.customPayload(fullCount, contCount);
            }
            if (hexDump)
            {
                dump = new PacketDump(buf, fullCount);
            }

            buf.release();
        }

        return PacketInfo.customPayload(id, size, dump);
    }

    @Override
    public final void print(PacketPrinter printer, PacketLogContext logCtx, PacketLogEntry entry)
    {
        if (entry.info() instanceof PacketInfo.CustomPayload customInfo)
        {
            printer.append("ID: ").append(customInfo.getId().toString()).newLine(true);
        }
        if (logCtx.logSize() && entry.info().getSize() instanceof PacketSize.CustomPayload customSize)
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



    public static final class Clientbound extends CustomPayloadPacketLogAdapter<ClientboundCustomPayloadPacket>
    {
        @Override
        public PacketInfo analyze(ClientboundCustomPayloadPacket pkt, PacketLogContext logCtx)
        {
            return analyze(pkt, pkt.payload().id(), logCtx);
        }
    }

    public static final class Serverbound extends CustomPayloadPacketLogAdapter<ServerboundCustomPayloadPacket>
    {
        @Override
        public PacketInfo analyze(ServerboundCustomPayloadPacket pkt, PacketLogContext logCtx)
        {
            return analyze(pkt, pkt.payload().id(), logCtx);
        }
    }
}
