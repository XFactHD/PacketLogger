package xfacthd.packetlogger;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import xfacthd.packetlogger.cmd.PacketLogCommand;
import xfacthd.packetlogger.event.ClientEvents;
import xfacthd.packetlogger.event.ServerEvents;
import xfacthd.packetlogger.logger.PacketLogAdapters;

@Mod(PacketLogger.MODID)
public final class PacketLogger
{
    public static final String MODID = "packetlogger";
    public static final Logger LOGGER = LogUtils.getLogger();

	public PacketLogger()
    {
        PacketLogCommand.register();
        PacketLogAdapters.init();
        if (FMLEnvironment.dist.isDedicatedServer())
        {
            ServerEvents.init();
        }
        else
        {
            ClientEvents.init();
        }
    }
}
