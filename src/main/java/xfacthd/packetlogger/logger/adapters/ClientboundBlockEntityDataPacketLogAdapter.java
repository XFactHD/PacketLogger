package xfacthd.packetlogger.logger.adapters;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import xfacthd.packetlogger.logger.*;
import xfacthd.packetlogger.logger.data.PacketInfo;
import xfacthd.packetlogger.logger.data.PacketLogEntry;
import xfacthd.packetlogger.utils.PacketPrinter;

public final class ClientboundBlockEntityDataPacketLogAdapter implements PacketLogAdapter<ClientboundBlockEntityDataPacket>
{
    @Override
    public PacketInfo analyze(ClientboundBlockEntityDataPacket pkt, PacketLogContext logCtx)
    {
        PacketInfo info = PacketLogConverter.DEFAULT_ADAPTER.analyze(pkt, logCtx);
        return PacketInfo.blockEntity(pkt.getPos(), pkt.getType(), info.getSize(), info.getDump());
    }

    @Override
    public void print(PacketPrinter printer, PacketLogContext logCtx, PacketLogEntry entry)
    {
        if (entry.info() instanceof PacketInfo.BlockEntity beInfo)
        {
            //noinspection ConstantConditions
            String typeName = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(beInfo.getType()).toString();
            printer.append("Position: ").append(beInfo.getPos().toString()).newLine(true)
                    .append("Type: ").append(typeName).newLine(true);
        }
        PacketLogConverter.DEFAULT_ADAPTER.print(printer, logCtx, entry);
    }
}
