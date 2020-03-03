package sidben.visiblearmorslots;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import sidben.visiblearmorslots.handler.EventDelegatorGuiOverlay;


@Mod.EventBusSubscriber(value = Dist.CLIENT,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ProxyClient {

    public static void initialize(FMLClientSetupEvent e) {
        // Event Handlers
        MinecraftForge.EVENT_BUS.register(new EventDelegatorGuiOverlay());
    }
}
