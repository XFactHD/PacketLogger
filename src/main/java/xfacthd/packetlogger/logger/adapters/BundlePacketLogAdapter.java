package xfacthd.packetlogger.logger.adapters;

import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import xfacthd.packetlogger.logger.*;
import xfacthd.packetlogger.logger.data.PacketInfo;
import xfacthd.packetlogger.logger.data.PacketLogEntry;
import xfacthd.packetlogger.utils.PacketPrinter;
import xfacthd.packetlogger.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public final class BundlePacketLogAdapter implements PacketLogAdapter<BundlePacket<?>>
{
    @Override
    @SuppressWarnings("unchecked")
    public PacketInfo analyze(BundlePacket<?> pkt, PacketLogContext logCtx)
    {
        boolean logSize = logCtx.logSize();
        boolean hexDump = logCtx.hexDump();

        if (!logSize && !hexDump)
        {
            return null;
        }

        List<Class<? extends Packet<?>>> types = new ArrayList<>();
        List<PacketInfo> infos = new ArrayList<>();

        pkt.subPackets().forEach(subPkt ->
        {
            PacketLogAdapter<Packet<?>> adapter = PacketLogConverter.getAdapter(subPkt);
            types.add((Class<? extends Packet<?>>) subPkt.getClass());
            infos.add(adapter.analyze(subPkt, logCtx));
        });

        return PacketInfo.bundle(types, infos);
    }

    @Override
    public void print(PacketPrinter printer, PacketLogContext logCtx, PacketLogEntry entry)
    {
        if (logCtx.logSize())
        {
            int size = entry.info().getSize().getSize();
            printer.append("Size: ").append(Utils.formatByteCount(size)).newLine(true);
        }
        
        if (entry.info() instanceof PacketInfo.Bundle bundleInfo)
        {
            printer.append("Bundled packets:").newLine().incIndent();

            List<Class<? extends Packet<?>>> types = bundleInfo.getTypes();
            List<PacketInfo> infos = bundleInfo.getInfos();
            for (int i = 0; i < types.size(); i++)
            {
                if (i > 0)
                {
                    printer.newLine().newLine();
                }

                printer.append("Type: ").append(Utils.printClassName(types.get(i))).newLine();

                Class<? extends Packet<?>> pktType = types.get(i);
                PacketLogConverter.getAdapter(pktType).print(printer, logCtx, new PacketLogEntry(
                        entry.timestamp(), entry.type(), pktType, entry.conHash(), infos.get(i)
                ));
            }

            printer.decIndent();
        }
    }
}
