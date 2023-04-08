package xfacthd.packetlogger.logger.data;

import net.minecraft.network.protocol.Packet;

public record PacketLogEntry(
        long timestamp,
        Type type,
        Class<? extends Packet<?>> pktClazz,
        int conHash,
        PacketInfo info
)
{
    public enum Type
    {
        INBOUND("Inbound"),
        OUTBOUND("Outbound");

        private final String name;

        Type(String name)
        {
            this.name = name;
        }

        public String print()
        {
            return name;
        }
    }
}
