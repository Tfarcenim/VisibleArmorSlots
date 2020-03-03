package sidben.visiblearmorslots.handler;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import sidben.visiblearmorslots.main.ModConfig;
import sidben.visiblearmorslots.main.Reference;


public class EventHandlerConfig
{

    @SubscribeEvent
    public static void onConfigurationChangedEvent(OnConfigChangedEvent event)
    {
        if (event.getModID().equalsIgnoreCase(Reference.MOD_ID)) {
            // Resync config
            ModConfig.refreshConfig();
            ModConfig.updateBlacklistedMods();
        }
    }

}
