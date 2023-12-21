package xfacthd.packetlogger.logger.adapters;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import xfacthd.packetlogger.logger.*;
import xfacthd.packetlogger.logger.data.PacketInfo;
import xfacthd.packetlogger.logger.data.PacketLogEntry;
import xfacthd.packetlogger.utils.PacketPrinter;

public final class ClientboundAddEntityPacketLogAdapter implements PacketLogAdapter<ClientboundAddEntityPacket>
{
    @Override
    public PacketInfo analyze(ClientboundAddEntityPacket pkt, PacketLogContext logCtx)
    {
        PacketInfo defaultInfo = PacketLogConverter.DEFAULT_ADAPTER.analyze(pkt, logCtx);
        return PacketInfo.entity(
                pkt.getId(),
                pkt.getUUID(),
                pkt.getX(),
                pkt.getY(),
                pkt.getZ(),
                defaultInfo.getSize(),
                defaultInfo.getDump()
        );
    }

    @Override
    public void print(PacketPrinter printer, PacketLogContext logCtx, PacketLogEntry entry)
    {
        if (entry.info() instanceof PacketInfo.Entity entityInfo)
        {
            printer.append("Entity ID: ").append(Integer.toString(entityInfo.getId())).newLine(true);
            printer.append("Entity UUID: ").append(entityInfo.getUUID().toString()).newLine(true);
            printer.append("Entity Pos: ").append(entityInfo.getPos().toString()).newLine(true);
        }
        PacketLogConverter.DEFAULT_ADAPTER.print(printer, logCtx, entry);
    }
}
