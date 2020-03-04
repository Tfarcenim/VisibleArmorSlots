package sidben.visiblearmorslots;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sidben.visiblearmorslots.handler.EventDelegatorGuiOverlay;
import sidben.visiblearmorslots.handler.EventHandlerConfig;
import sidben.visiblearmorslots.network.NetworkManager;

import static sidben.visiblearmorslots.main.ModConfig.SERVER_SPEC;


@Mod(VisibleArmorSlots.MOD_ID)
public class VisibleArmorSlots {

	public static final String MOD_ID = "visiblearmorslots";

	public VisibleArmorSlots(){
		ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, SERVER_SPEC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initialize);
	}


	private void initialize(FMLClientSetupEvent e) {
		// Event Handlers
		MinecraftForge.EVENT_BUS.register(new EventDelegatorGuiOverlay());
	}
	
	private void init(FMLCommonSetupEvent e){
		NetworkManager.registerMessages();
		MinecraftForge.EVENT_BUS.register(EventHandlerConfig.class);
		//ModConfig.updateBlacklistedMods();
	}
}
