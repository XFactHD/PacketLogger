package xfacthd.packetlogger.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import xfacthd.packetlogger.PacketLogger;
import xfacthd.packetlogger.datagen.providers.PacketLogLanguageProvider;

@Mod.EventBusSubscriber(modid = PacketLogger.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GatherDataHandler
{
    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();

        gen.addProvider(event.includeClient(), new PacketLogLanguageProvider(output));
    }



    private GatherDataHandler() { }
}
