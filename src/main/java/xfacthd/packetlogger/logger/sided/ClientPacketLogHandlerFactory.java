package xfacthd.packetlogger.logger.sided;

import io.netty.channel.ChannelPipeline;
import net.minecraft.network.Connection;
import xfacthd.packetlogger.logger.PacketLogContext;
import xfacthd.packetlogger.logger.PacketLogHandler;

public final class ClientPacketLogHandlerFactory
{
    public static PacketLogHandler create(PacketLogContext logCtx, Connection connection, ChannelPipeline pipeline)
    {
        return new ClientPacketLogHandler(logCtx, connection, pipeline);
    }

    private ClientPacketLogHandlerFactory() { }
}
