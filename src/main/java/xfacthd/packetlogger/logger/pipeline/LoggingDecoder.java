package xfacthd.packetlogger.logger.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import xfacthd.packetlogger.logger.PacketLogHandler;

import java.util.List;

public class LoggingDecoder extends MessageToMessageDecoder<Packet<?>>
{
    private final Connection connection;

    public LoggingDecoder(Connection connection)
    {
        this.connection = connection;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Packet<?> msg, List<Object> out)
    {
        PacketLogHandler.logInbound(connection, msg);
        out.add(msg);
    }
}
