package sidben.visiblearmorslots;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sidben.visiblearmorslots.handler.EventHandlerConfig;
import sidben.visiblearmorslots.main.ModConfig;
import sidben.visiblearmorslots.main.Reference;
import sidben.visiblearmorslots.network.NetworkManager;

import static sidben.visiblearmorslots.main.ModConfig.SERVER_SPEC;


@Mod(Reference.MOD_ID)
public class ModVisibleArmorSlots {

	public ModVisibleArmorSlots(){
		ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, SERVER_SPEC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
	}

	private void init(FMLCommonSetupEvent e){
		NetworkManager.registerMessages();
		MinecraftForge.EVENT_BUS.register(EventHandlerConfig.class);
		//ModConfig.updateBlacklistedMods();

	}
}
