package xfacthd.packetlogger.logger.adapters;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagNetworkSerialization;
import xfacthd.packetlogger.PacketLogger;
import xfacthd.packetlogger.logger.*;
import xfacthd.packetlogger.logger.data.PacketInfo;
import xfacthd.packetlogger.logger.data.PacketLogEntry;
import xfacthd.packetlogger.utils.PacketPrinter;
import xfacthd.packetlogger.utils.Utils;

import java.lang.invoke.MethodHandle;
import java.util.Map;

public abstract sealed class ClientboundUpdateDatapackDataPacketLogAdapter<T extends Packet<?>> implements PacketLogAdapter<T>
        permits ClientboundUpdateDatapackDataPacketLogAdapter.Recipes, ClientboundUpdateDatapackDataPacketLogAdapter.Tags
{
    private final String type;

    protected ClientboundUpdateDatapackDataPacketLogAdapter(String type)
    {
        this.type = type;
    }

    protected final PacketInfo analyze(T pkt, int count, PacketLogContext logCtx)
    {
        PacketInfo defaultInfo = PacketLogConverter.DEFAULT_ADAPTER.analyze(pkt, logCtx);
        return PacketInfo.datapackData(count, defaultInfo.getSize(), defaultInfo.getDump());
    }

    @Override
    public final void print(PacketPrinter printer, PacketLogContext logCtx, PacketLogEntry entry)
    {
        if (entry.info() instanceof PacketInfo.DatapackData dataInfo)
        {
            printer.append(type).append(" count: ").append(Integer.toString(dataInfo.getCount())).newLine(true);
        }
        PacketLogConverter.DEFAULT_ADAPTER.print(printer, logCtx, entry);
    }



    public static final class Recipes extends ClientboundUpdateDatapackDataPacketLogAdapter<ClientboundUpdateRecipesPacket>
    {
        public Recipes()
        {
            super("Recipe");
        }

        @Override
        public PacketInfo analyze(ClientboundUpdateRecipesPacket pkt, PacketLogContext logCtx)
        {
            return analyze(pkt, pkt.getRecipes().size(), logCtx);
        }
    }

    public static final class Tags extends ClientboundUpdateDatapackDataPacketLogAdapter<ClientboundUpdateTagsPacket>
    {
        private static final MethodHandle MTH_GET_TAGS_MAP = Utils.unreflectField(
                TagNetworkSerialization.NetworkPayload.class,
                "tags"
        );

        public Tags()
        {
            super("Tag");
        }

        @Override
        public PacketInfo analyze(ClientboundUpdateTagsPacket pkt, PacketLogContext logCtx)
        {
            if (!logCtx.fullLog())
            {
                // Avoid larger count collection overhead when the data wouldn't be shown anyway
                return analyze(pkt, 0, logCtx);
            }

            int[] count = new int[] { 0 };
            pkt.getTags().values().forEach(payload ->
            {
                try
                {
                    //noinspection unchecked
                    var tags = (Map<ResourceLocation, IntList>) MTH_GET_TAGS_MAP.invoke(payload);
                    count[0] += tags.size();
                }
                catch (Throwable t)
                {
                    PacketLogger.LOGGER.error("Encountered an error while analyzing ClientboundUpdateTagsPacket", t);
                }
            });
            return analyze(pkt, count[0], logCtx);
        }
    }
}
