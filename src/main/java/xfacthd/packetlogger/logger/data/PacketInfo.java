package xfacthd.packetlogger.logger.data;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public abstract class PacketInfo
{
    public static PacketInfo empty()
    {
        return Empty.INSTANCE;
    }

    public static PacketInfo single(PacketSize size, PacketDump dump)
    {
        return new Single(size, dump);
    }

    public static PacketInfo bundle(List<Class<? extends Packet<?>>> types, List<PacketInfo> infos)
    {
        return new Bundle(types, infos);
    }

    public static PacketInfo customPayload(ResourceLocation id, PacketSize size, PacketDump dump)
    {
        return new CustomPayload(id, size, dump);
    }

    public static PacketInfo blockEntity(BlockPos pos, BlockEntityType<?> type, PacketSize size, PacketDump dump)
    {
        return new BlockEntity(pos, type, size, dump);
    }

    public static PacketInfo levelChunkWithLight(ChunkPos pos, PacketSize size, PacketDump dump)
    {
        return new LevelChunkWithLight(pos, size, dump);
    }

    public static PacketInfo entity(int id, UUID uuid, double x, double y, double z, PacketSize size, PacketDump dump)
    {
        return new Entity(id, uuid, x, y, z, size, dump);
    }

    public static PacketInfo datapackData(int count, PacketSize size, PacketDump dump)
    {
        return new DatapackData(count, size, dump);
    }



    public abstract PacketSize getSize();

    public abstract PacketDump getDump();



    public static final class Empty extends PacketInfo
    {
        private static final PacketInfo INSTANCE = new Empty();

        private Empty() { }

        @Override
        public PacketSize getSize()
        {
            return PacketSize.empty();
        }

        @Override
        public PacketDump getDump()
        {
            return PacketDump.EMPTY;
        }
    }

    public static class Single extends PacketInfo
    {
        private final PacketSize size;
        private final PacketDump dump;

        protected Single(PacketSize size, PacketDump dump)
        {
            this.size = size;
            this.dump = dump;
        }

        @Override
        public PacketSize getSize()
        {
            return size;
        }

        @Override
        public PacketDump getDump()
        {
            return dump;
        }
    }

    public static final class Bundle extends PacketInfo
    {
        private final List<Class<? extends Packet<?>>> types;
        private final List<PacketInfo> infos;

        private Bundle(List<Class<? extends Packet<?>>> types, List<PacketInfo> infos)
        {
            this.types = types;
            this.infos = infos;
        }

        public List<Class<? extends Packet<?>>> getTypes()
        {
            return types;
        }

        public List<PacketInfo> getInfos()
        {
            return infos;
        }

        @Override
        public PacketSize getSize()
        {
            int size = 0;
            for (PacketInfo info : infos)
            {
                size += info.getSize().getSize();
            }
            return PacketSize.single(size);
        }

        @Override
        public PacketDump getDump()
        {
            return PacketDump.EMPTY;
        }
    }

    public static final class CustomPayload extends Single
    {
        private final ResourceLocation id;

        private CustomPayload(ResourceLocation id, PacketSize size, PacketDump dump)
        {
            super(size, dump);
            this.id = id;
        }

        public ResourceLocation getId()
        {
            return id;
        }
    }

    public static final class BlockEntity extends Single
    {
        private final BlockPos pos;
        private final BlockEntityType<?> type;

        private BlockEntity(BlockPos pos, BlockEntityType<?> type, PacketSize size, PacketDump dump)
        {
            super(size, dump);
            this.pos = pos;
            this.type = type;
        }

        public BlockPos getPos()
        {
            return pos;
        }

        public BlockEntityType<?> getType()
        {
            return type;
        }
    }

    public static final class LevelChunkWithLight extends Single
    {
        private final ChunkPos pos;

        private LevelChunkWithLight(ChunkPos pos, PacketSize size, PacketDump dump)
        {
            super(size, dump);
            this.pos = pos;
        }

        public ChunkPos getPos()
        {
            return pos;
        }
    }

    public static final class Entity extends Single
    {
        private final int id;
        private final UUID uuid;
        private final Vec3 pos;

        private Entity(int id, UUID uuid, double x, double y, double z, PacketSize size, PacketDump dump)
        {
            super(size, dump);
            this.id = id;
            this.uuid = uuid;
            this.pos = new Vec3(x, y, z);
        }

        public int getId()
        {
            return id;
        }

        public UUID getUUID()
        {
            return uuid;
        }

        public Vec3 getPos()
        {
            return pos;
        }
    }

    public static final class DatapackData extends Single
    {
        private final int count;

        private DatapackData(int count, PacketSize size, PacketDump dump)
        {
            super(size, dump);
            this.count = count;
        }

        public int getCount()
        {
            return count;
        }
    }
}
