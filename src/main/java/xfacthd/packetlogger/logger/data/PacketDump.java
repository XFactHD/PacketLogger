package xfacthd.packetlogger.logger.data;

import net.minecraft.network.FriendlyByteBuf;

public final class PacketDump
{
    public static final PacketDump EMPTY = new PacketDump();

    private final byte[] dump;

    public PacketDump(FriendlyByteBuf buf, int size)
    {
        this.dump = new byte[size];
        buf.readBytes(dump);
    }

    private PacketDump()
    {
        this.dump = new byte[0];
    }

    public byte[] getDump()
    {
        return dump;
    }
}
