package xfacthd.packetlogger.logger.data;

import org.jetbrains.annotations.NotNull;

public final class PacketStats implements Comparable<PacketStats>
{
    private final Class<?> pktType;
    private int count = 0;
    private int totalSize = 0;
    private int minSize = Integer.MAX_VALUE;
    private int maxSize = 0;

    public PacketStats(Class<?> pktType)
    {
        this.pktType = pktType;
    }

    public void account(PacketInfo info)
    {
        count++;
        int size = info.getSize().getSize();
        totalSize += size;
        minSize = Math.min(minSize, size);
        maxSize = Math.max(maxSize, size);
    }

    public Class<?> getPacketType()
    {
        return pktType;
    }

    public int getCount()
    {
        return count;
    }

    public int getTotalSize()
    {
        return totalSize;
    }

    public int getMedianSize()
    {
         return totalSize / count;
    }

    public int getMinSize()
    {
        return minSize;
    }

    public int getMaxSize()
    {
        return maxSize;
    }

    @Override
    public int compareTo(@NotNull PacketStats other)
    {
        return Integer.compare(other.count, count);
    }
}
