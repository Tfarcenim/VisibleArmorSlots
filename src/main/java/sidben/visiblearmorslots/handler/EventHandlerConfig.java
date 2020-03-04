package sidben.visiblearmorslots.handler;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import sidben.visiblearmorslots.VisibleArmorSlots;
import sidben.visiblearmorslots.main.ModConfig;


public class EventHandlerConfig
{

    @SubscribeEvent
    public static void onConfigurationChangedEvent(OnConfigChangedEvent event)
    {
        if (event.getModID().equalsIgnoreCase(VisibleArmorSlots.MOD_ID)) {
            // Resync config
            ModConfig.updateBlacklistedMods();
        }
    }

}
