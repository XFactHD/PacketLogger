package xfacthd.packetlogger.logger.adapters;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.world.level.ChunkPos;
import xfacthd.packetlogger.logger.PacketLogAdapter;
import xfacthd.packetlogger.logger.PacketLogContext;
import xfacthd.packetlogger.logger.data.*;
import xfacthd.packetlogger.utils.PacketPrinter;
import xfacthd.packetlogger.utils.Utils;

public final class ClientboundLevelChunkWithLightPacketLogAdapter implements PacketLogAdapter<ClientboundLevelChunkWithLightPacket>
{
    @Override
    public PacketInfo analyze(ClientboundLevelChunkWithLightPacket pkt, PacketLogContext logCtx)
    {
        boolean logSize = logCtx.logSize();
        boolean hexDump = logCtx.hexDump();

        ChunkPos pos = new ChunkPos(pkt.getX(), pkt.getZ());
        PacketSize size = PacketSize.empty();
        PacketDump dump = PacketDump.EMPTY;
        if (logSize || hexDump)
        {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

            int startChunk = buf.writerIndex();
            pkt.getChunkData().write(buf);
            int countChunk = buf.writerIndex() - startChunk;

            buf.resetWriterIndex();
            int startLight = buf.writerIndex();
            pkt.getLightData().write(buf);
            int countLight = buf.writerIndex() - startLight;

            buf.resetWriterIndex();
            int start = buf.writerIndex();
            pkt.write(buf);
            int count = buf.writerIndex() - start;

            if (logSize)
            {
                size = PacketSize.levelChunkWithLight(count, countChunk, countLight);
            }
            if (hexDump)
            {
                dump = new PacketDump(buf, count);
            }
        }
        return PacketInfo.levelChunkWithLight(pos, size, dump);
    }

    @Override
    public void print(PacketPrinter printer, PacketLogContext logCtx, PacketLogEntry entry)
    {
        if (entry.info() instanceof PacketInfo.LevelChunkWithLight chunkWithLight)
        {
            printer.append("Position: ").append(chunkWithLight.getPos().toString()).newLine(true);
        }
        if (logCtx.logSize() && entry.info().getSize() instanceof PacketSize.LevelChunkWithLight chunkWithLight)
        {
            int fullSize = chunkWithLight.getSize();
            int chunkSize = chunkWithLight.getChunkSize();
            int lightSize = chunkWithLight.getLightSize();
            printer.append("Total size: ").append(Utils.formatByteCount(fullSize)).newLine(true);
            printer.append("Chunk data size: ").append(Utils.formatByteCount(chunkSize)).newLine(true);
            printer.append("Light data size: ").append(Utils.formatByteCount(lightSize)).newLine(true);
        }
        if (logCtx.hexDump())
        {
            printer.append("Dump:").newLine();
            Utils.printHexDump(printer, entry.info().getDump());
        }
    }
}
