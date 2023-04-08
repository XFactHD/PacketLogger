package xfacthd.packetlogger.logger.sided;

import xfacthd.packetlogger.logger.PacketLogContext;
import xfacthd.packetlogger.logger.PacketLogHandler;

public final class ServerPacketLogHandlerFactory
{
    public static PacketLogHandler create(PacketLogContext logCtx)
    {
        return new ServerPacketLogHandler(logCtx);
    }

    private ServerPacketLogHandlerFactory() { }
}
