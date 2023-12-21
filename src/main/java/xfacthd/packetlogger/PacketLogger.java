package xfacthd.packetlogger;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import xfacthd.packetlogger.cmd.PacketLogCommand;
import xfacthd.packetlogger.event.ClientEvents;
import xfacthd.packetlogger.event.ServerEvents;
import xfacthd.packetlogger.logger.PacketLogAdapters;

@Mod(PacketLogger.MODID)
@SuppressWarnings("UtilityClassWithPublicConstructor")
public final class PacketLogger
{
    public static final String MODID = "packetlogger";
    public static final Logger LOGGER = LogUtils.getLogger();

	public PacketLogger(IEventBus modBus)
    {
        PacketLogCommand.register();
        PacketLogAdapters.init();
        if (FMLEnvironment.dist.isDedicatedServer())
        {
            ServerEvents.init(modBus);
        }
        else
        {
            ClientEvents.init(modBus);
        }
    }
}
