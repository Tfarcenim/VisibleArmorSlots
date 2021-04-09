package sidben.visiblearmorslots.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import sidben.visiblearmorslots.util.LogHelper;

import java.util.ArrayList;
import java.util.List;


public class ModConfig {

	private static ForgeConfigSpec.BooleanValue onDebug;
	private static ForgeConfigSpec.BooleanValue debugAsInfo;
	private static ForgeConfigSpec.EnumValue<SlotPosition> extraSlotsSide;
	private static ForgeConfigSpec.IntValue extraSlotsMargin;
	private static ForgeConfigSpec.BooleanValue swapKeyEnabled;
	private static ForgeConfigSpec.ConfigValue<List<? extends String>> blacklistedScreens;

	public ModConfig(ForgeConfigSpec.Builder t) {

		// Load properties
		t.push("general");
		onDebug = t.define("on_debug", false);
		debugAsInfo = t.define("debug_as_level_info",false);
		extraSlotsSide = t.defineEnum("slots_side",SlotPosition.LEFT);
		extraSlotsMargin = t.defineInRange("slots_margin", 2, 0, 128);
		swapKeyEnabled = t.define("swap_hands",  true);
		blacklistedScreens = t.defineList("blacklisted_screens", Lists.newArrayList("curios"),String.class::isInstance);
	}

	public static final ModConfig SERVER;
	public static final ForgeConfigSpec SERVER_SPEC;

	static {
		final Pair<ModConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ModConfig::new);
		SERVER_SPEC = specPair.getRight();
		SERVER = specPair.getLeft();
	}

	// --------------------------------------------
	// Public config values
	// --------------------------------------------

	/**
	 * When the mod is on 'debug mode', messages with the level Trace and Debug will be added to the logs.
	 */
	public static boolean onDebug() {
		return onDebug.get();
	}


	/**
	 * DEBUG and TRACE messages are logged with the level INFO. Requires onDebug set to true.
	 */
	public static boolean debugAsInfo() {
		return debugAsInfo.get();
	}


	public static SlotPosition extraSlotsSide() {
		return extraSlotsSide.get();
	}


	public static int extraSlotsMargin() {
		return extraSlotsMargin.get();
	}


	/**
	 * Enabled swapping items of the off-hand slot by pressing the swap hands key when hovering over any slot.
	 */
	public static boolean swapKeyEnabled() {
		return swapKeyEnabled.get();
	}


	public static List<? extends String> blacklistedScreens() {
		return blacklistedScreens.get();
	}
}
