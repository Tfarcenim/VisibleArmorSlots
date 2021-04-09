package sidben.visiblearmorslots;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sidben.visiblearmorslots.handler.EventDelegatorGuiOverlay;
import sidben.visiblearmorslots.network.NetworkManager;


@Mod(VisibleArmorSlots.MOD_ID)
public class VisibleArmorSlots {

	public static final String MOD_ID = "visiblearmorslots";

	public VisibleArmorSlots(){
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, sidben.visiblearmorslots.config.ModConfig.SERVER_SPEC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initialize);
	}


	private void initialize(FMLClientSetupEvent e) {
		// Event Handlers
		MinecraftForge.EVENT_BUS.register(new EventDelegatorGuiOverlay());
	}
	
	private void init(FMLCommonSetupEvent e){
		NetworkManager.registerMessages();
	}
}
