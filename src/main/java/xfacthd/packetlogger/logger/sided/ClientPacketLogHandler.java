package xfacthd.packetlogger.logger.sided;

import io.netty.channel.ChannelPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import xfacthd.packetlogger.PacketLogger;
import xfacthd.packetlogger.logger.PacketLogContext;
import xfacthd.packetlogger.logger.PacketLogHandler;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = PacketLogger.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientPacketLogHandler extends PacketLogHandler
{
    private final Connection connection;
    private final ChannelPipeline pipeline;

    ClientPacketLogHandler(PacketLogContext logCtx, Connection connection, ChannelPipeline pipeline)
    {
        super(logCtx);
        this.connection = Objects.requireNonNullElseGet(
                connection,
                () -> Objects.requireNonNull(Minecraft.getInstance().player).connection.getConnection()
        );
        this.pipeline = Objects.requireNonNullElseGet(
                pipeline,
                () -> this.connection.channel().pipeline()
        );
    }

    @Override
    protected void installInterceptors()
    {
        installInterceptor(connection, pipeline);
    }

    @Override
    protected void removeInterceptors()
    {
        removeInterceptor(pipeline);
    }

    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event)
    {
        if (getInstance() instanceof ClientPacketLogHandler handler)
        {
            handler.tick();
        }
    }
}
