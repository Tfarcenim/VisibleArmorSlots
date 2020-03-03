package sidben.visiblearmorslots.main;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import sidben.visiblearmorslots.util.LogHelper;

import java.util.ArrayList;
import java.util.List;


public class ModConfig {
	public static final String POSITION_LEFT = "LEFT";
	public static final String POSITION_RIGHT = "RIGHT";
	public static final int POTION_SHIFT_MARGIN_LEFT = -62;
	public static final int POTION_SHIFT_MARGIN_RIGHT = 60;
	public static final String CATEGORY_DEBUG = "debug";

	private static ForgeConfigSpec.BooleanValue onDebug;
	private static ForgeConfigSpec.BooleanValue debugAsInfo;
	private static ForgeConfigSpec.ConfigValue<String> _extraSlotsSide;
	private static ForgeConfigSpec.IntValue _extraSlotsMargin;
	private static ForgeConfigSpec.BooleanValue _swapKeyEnabled;
	private static String[] _blacklistedModIds;
	private static String[] _blacklistedModPackages;

	public ModConfig(ForgeConfigSpec.Builder t) {
		final String[] slotSidesValidEntries = new String[]{POSITION_LEFT, POSITION_RIGHT};

		// Load properties
		t.push("general");
		onDebug = t.define("on_debug", false);
		debugAsInfo = t.define("debug_as_level_info",false);
		_extraSlotsSide = t.define("slots_side",  POSITION_LEFT, o -> o ==slotSidesValidEntries);
		_extraSlotsMargin = t.defineInRange("slots_margin", 2, 0, 128);
		_swapKeyEnabled = t.define("swap_hands",  true);
		//_blacklistedModIds = _config.getStringList("blacklisted_mod_ids",L, new String[0], "");
	}

	public static final ModConfig SERVER;
	public static final ForgeConfigSpec SERVER_SPEC;

	static {
		final Pair<ModConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ModConfig::new);
		SERVER_SPEC = specPair.getRight();
		SERVER = specPair.getLeft();
	}

	/**
	 * Check each mod id that is blacklisted and find out which package they belong to,
	 * so I can ignore them.
	 */
	public static void updateBlacklistedMods() {
		_blacklistedModPackages = new String[0];
		if (_blacklistedModIds.length == 0) {
			return;
		}


		final List<String> modPackages = new ArrayList<String>();

		LogHelper.info("The following mods are blacklisted and will be ignored by the Visible Armor Slots mod:");
		LogHelper.info("[START]");

		for (final String blacklistedModId : _blacklistedModIds) {
		}
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


	public static String extraSlotsSide() {
		return _extraSlotsSide.get();
	}


	public static int extraSlotsMargin() {
		return _extraSlotsMargin.get();
	}


	/**
	 * Enabled swapping items of the off-hand slot by pressing the swap hands key when hovering over any slot.
	 */
	public static boolean swapKeyEnabled() {
		return _swapKeyEnabled.get();
	}


	public static List<String> blacklistedModPackages() {
		return new ArrayList<>();//_blacklistedModPackages;
	}
}
