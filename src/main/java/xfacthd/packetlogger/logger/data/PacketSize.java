package xfacthd.packetlogger.logger.data;

public abstract class PacketSize
{
    public static PacketSize empty()
    {
        return Empty.INSTANCE;
    }

    public static PacketSize single(int size)
    {
        return new Single(size);
    }

    public static PacketSize customQuery(int full, int contained)
    {
        return new CustomQuery(full, contained);
    }

    public static PacketSize customPayload(int full, int contained)
    {
        return new CustomPayload(full, contained);
    }

    public static PacketSize levelChunkWithLight(int full, int chunk, int light)
    {
        return new LevelChunkWithLight(full, chunk, light);
    }



    public abstract int getSize();



    public static class Empty extends PacketSize
    {
        private static final PacketSize INSTANCE = new Empty();

        @Override
        public int getSize()
        {
            return 0;
        }
    }

    public static class Single extends PacketSize
    {
        private final int size;

        private Single(int size) { this.size = size; }

        @Override
        public int getSize()
        {
            return size;
        }
    }

    public static class CustomQuery extends PacketSize
    {
        private final int full;
        private final int contained;

        public CustomQuery(int full, int contained)
        {
            this.full = full;
            this.contained = contained;
        }

        @Override
        public int getSize()
        {
            return full;
        }

        public int getContainedSize()
        {
            return contained;
        }
    }

    public static class CustomPayload extends PacketSize
    {
        private final int full;
        private final int contained;

        public CustomPayload(int full, int contained)
        {
            this.full = full;
            this.contained = contained;
        }

        @Override
        public int getSize()
        {
            return full;
        }

        public int getContainedSize()
        {
            return contained;
        }
    }

    public static class LevelChunkWithLight extends PacketSize
    {
        private final int full;
        private final int chunk;
        private final int light;

        public LevelChunkWithLight(int full, int chunk, int light)
        {
            this.full = full;
            this.chunk = chunk;
            this.light = light;
        }

        @Override
        public int getSize()
        {
            return full;
        }

        public int getChunkSize()
        {
            return chunk;
        }

        public int getLightSize()
        {
            return light;
        }
    }
}
