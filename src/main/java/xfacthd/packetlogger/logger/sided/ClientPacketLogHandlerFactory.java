package xfacthd.packetlogger.logger.sided;

import xfacthd.packetlogger.logger.PacketLogContext;
import xfacthd.packetlogger.logger.PacketLogHandler;

public final class ClientPacketLogHandlerFactory
{
    public static PacketLogHandler create(PacketLogContext logCtx)
    {
        return new ClientPacketLogHandler(logCtx);
    }

    private ClientPacketLogHandlerFactory() { }
}
