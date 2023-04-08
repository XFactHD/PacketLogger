package xfacthd.packetlogger.logger.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import xfacthd.packetlogger.logger.PacketLogHandler;

import java.util.List;

public class LoggingEncoder extends MessageToMessageEncoder<Packet<?>>
{
    private final Connection connection;

    public LoggingEncoder(Connection connection)
    {
        this.connection = connection;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet<?> msg, List<Object> out)
    {
        PacketLogHandler.logOutbound(connection, msg);
        out.add(msg);
    }
}
