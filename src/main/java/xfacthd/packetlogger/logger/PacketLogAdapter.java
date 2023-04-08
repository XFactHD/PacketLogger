package xfacthd.packetlogger.logger;

import net.minecraft.network.protocol.Packet;
import xfacthd.packetlogger.logger.data.*;
import xfacthd.packetlogger.utils.PacketPrinter;

public interface PacketLogAdapter<T extends Packet<?>>
{
    PacketInfo analyze(T pkt, PacketLogContext logCtx);

    void print(PacketPrinter printer, PacketLogContext logCtx, PacketLogEntry entry);
}
