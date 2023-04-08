package xfacthd.packetlogger.logger.adapters;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import xfacthd.packetlogger.logger.*;
import xfacthd.packetlogger.logger.data.PacketInfo;
import xfacthd.packetlogger.logger.data.PacketLogEntry;
import xfacthd.packetlogger.utils.PacketPrinter;

import java.util.UUID;

public abstract sealed class ClientboundAddEntityPacketLogAdapter<T extends Packet<?>> implements PacketLogAdapter<T>
    permits ClientboundAddEntityPacketLogAdapter.Entity, ClientboundAddEntityPacketLogAdapter.Player
{
    protected final PacketInfo analyze(T pkt, int id, UUID uuid, double x, double y, double z, PacketLogContext logCtx)
    {
        PacketInfo defaultInfo = PacketLogConverter.DEFAULT_ADAPTER.analyze(pkt, logCtx);
        return PacketInfo.entity(id, uuid, x, y, z, defaultInfo.getSize(), defaultInfo.getDump());
    }

    @Override
    public final void print(PacketPrinter printer, PacketLogContext logCtx, PacketLogEntry entry)
    {
        if (entry.info() instanceof PacketInfo.Entity entityInfo)
        {
            printer.append("Entity ID: ").append(Integer.toString(entityInfo.getId())).newLine(true);
            printer.append("Entity UUID: ").append(entityInfo.getUUID().toString()).newLine(true);
            printer.append("Entity Pos: ").append(entityInfo.getPos().toString()).newLine(true);
        }
        PacketLogConverter.DEFAULT_ADAPTER.print(printer, logCtx, entry);
    }



    public static final class Entity extends ClientboundAddEntityPacketLogAdapter<ClientboundAddEntityPacket>
    {
        @Override
        public PacketInfo analyze(ClientboundAddEntityPacket pkt, PacketLogContext logCtx)
        {
            return analyze(pkt, pkt.getId(), pkt.getUUID(), pkt.getX(), pkt.getY(), pkt.getZ(), logCtx);
        }
    }

    public static final class Player extends ClientboundAddEntityPacketLogAdapter<ClientboundAddPlayerPacket>
    {
        @Override
        public PacketInfo analyze(ClientboundAddPlayerPacket pkt, PacketLogContext logCtx)
        {
            return analyze(pkt, pkt.getEntityId(), pkt.getPlayerId(), pkt.getX(), pkt.getY(), pkt.getZ(), logCtx);
        }
    }
}
