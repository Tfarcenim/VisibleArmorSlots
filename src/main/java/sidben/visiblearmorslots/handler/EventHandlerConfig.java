package sidben.visiblearmorslots.handler;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import sidben.visiblearmorslots.VisibleArmorSlots;
import sidben.visiblearmorslots.main.ModConfig;


public class EventHandlerConfig {

	@SubscribeEvent
	public static void onConfigurationChangedEvent(ModConfigEvent event) {
		if (event.getConfig().getModId().equals(VisibleArmorSlots.MOD_ID)) {
			// Resync config
			ModConfig.updateBlacklistedMods();
		}
	}
}
