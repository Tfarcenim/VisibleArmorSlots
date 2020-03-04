package sidben.visiblearmorslots.handler;

import java.util.HashMap;

import net.minecraft.client.MouseHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseReleasedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.PotionShiftEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import sidben.visiblearmorslots.VisibleArmorSlots;
import sidben.visiblearmorslots.client.gui.GuiExtraSlotsOverlay;
import sidben.visiblearmorslots.main.ModConfig;
import sidben.visiblearmorslots.util.LogHelper;


/**
 * Delegates GuiEvents to the {@link sidben.visiblearmorslots.client.gui.GuiExtraSlotsOverlay GuiExtraSlotsOverlay}.
 */
public class EventDelegatorGuiOverlay {

	private static GuiExtraSlotsOverlay _guiOverlay;
	private static HashMap<String, InfoGuiOverlayDisplayParams> _cacheDisplayParams = new HashMap<String, InfoGuiOverlayDisplayParams>();

	GuiExtraSlotsOverlay getGuiOverlay() {
		if (_guiOverlay == null) {
			_guiOverlay = new GuiExtraSlotsOverlay();
		}
		return _guiOverlay;
	}


	/**
	 * Returns if the current GUI should have the extra slots visible.
	 */
	boolean shouldDisplayGuiOverlay(Screen gui) {
		if (gui == null ||
						Minecraft.getInstance().world == null ||
						!(gui instanceof ContainerScreen) ||
						gui instanceof InventoryScreen ||
						gui.getMinecraft().player.isSpectator()) {
			return false;
		}
		final InfoGuiOverlayDisplayParams displayParams = getDisplayParamsForGui(gui);
		return displayParams.getShouldDisplay();
	}


	/**
	 * Returns the display parameters for the given GUI.
	 */
	InfoGuiOverlayDisplayParams getDisplayParamsForGui(Screen gui) {
		if (!(gui instanceof ContainerScreen)) {
			return InfoGuiOverlayDisplayParams.EMPTY;
		}

		final ContainerScreen guiContainer = (ContainerScreen) gui;
		InfoGuiOverlayDisplayParams displayParams;

		// NOTE: inventorySlots should not be null, but we never know for sure...
		int containerSize = 0;
		if (guiContainer.getContainer().inventorySlots != null) {
			containerSize = guiContainer.getContainer().inventorySlots.size();
		}

		// Allows the same GuiContainer to have different parameters, if the container size
		// is different. Example: Chests and Double Chests.
		//
		// If this causes problems in the future I'll append the gui size to the key.
		//
		// Also adds the current screen size, to avoid misplacing the slots when the
		// window resizes.
		String guiClassKey = gui.getClass().getName();
		guiClassKey += "|" + containerSize + "|" + gui.width + "|" + gui.height;

		// TODO: The reference point for the displayparams should be the middle of the screen,
		// not 0,0. This will allow the removal of width/height on the class key.


		if (EventDelegatorGuiOverlay._cacheDisplayParams.containsKey(guiClassKey)) {
			displayParams = _cacheDisplayParams.get(guiClassKey);

		} else {
			displayParams = InfoGuiOverlayDisplayParams.create(guiContainer, guiClassKey);
			_cacheDisplayParams.put(guiClassKey, displayParams);
			LogHelper.trace("EventDelegatorGuiOverlay: Cached display parameters for [%s], key [%s], value [%s]", gui, guiClassKey, displayParams);
		}


		return displayParams;
	}


	// -----------------------------------------------------------
	// Event handlers
	// -----------------------------------------------------------

	/**
	 * Called when the GUI is displayed and when the window resizes.
	 */
	@SubscribeEvent
	public void onInitGuiEvent(InitGuiEvent.Post event) {
		// Only process if the world is loaded
		if (Minecraft.getInstance().world == null) {
			return;
		}
		final Screen gui = event.getGui();

		// NOTE: even if the gui overlay is not visible, it still get the basic config to avoid crashes and leaks
		if (gui != null) {

			this.getGuiOverlay().setWorldAndResolution(gui.width, gui.height);
			this.getGuiOverlay().setExternalGuiPosition(gui);
		}
		if (!this.shouldDisplayGuiOverlay(gui)) {
			return;
		}


		final InfoGuiOverlayDisplayParams displayParams = getDisplayParamsForGui(gui);

		this.getGuiOverlay().guiLeft = displayParams.getGuiLeft();
		this.getGuiOverlay().guiTop = displayParams.getGuiTop();
		this.getGuiOverlay().refreshExtraSlotsInfo(gui.getMinecraft().player.inventory);

		// Reposition the overlay if the potion effects are taking space
		if (this.getGuiOverlay().isPotionShiftActive()) {
			if (ModConfig.extraSlotsSide().equals(ModConfig.POSITION_LEFT)) {
				this.getGuiOverlay().guiLeft += ModConfig.POTION_SHIFT_MARGIN_LEFT;
			} else if (ModConfig.extraSlotsSide().equals(ModConfig.POSITION_RIGHT)) {
				this.getGuiOverlay().guiLeft += ModConfig.POTION_SHIFT_MARGIN_RIGHT;
			}

			// Resets the state since the overlay class is shared among all containers.
			this.getGuiOverlay().setPotionShiftState(false);
		}
	}


	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onBackgroundDrawEvent(DrawScreenEvent event) {
		if (!this.shouldDisplayGuiOverlay(event.getGui())) {
			return;
		}

		this.getGuiOverlay().drawBackground();

		double mouseX = event.getMouseX();
		double mouseY = event.getMouseY();

		this.getGuiOverlay().drawScreen(mouseX, mouseY);
	}


	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onDrawScreenEventPost(DrawScreenEvent.Post event) {
		if (!this.shouldDisplayGuiOverlay(event.getGui())) {
			return;
		}

		this.getGuiOverlay().drawForeground(event.getMouseX(), event.getMouseY());
	}


	@SubscribeEvent
	public void onPotionShiftEvent(PotionShiftEvent event) {
		if (!this.shouldDisplayGuiOverlay(event.getGui())) {
			return;
		}
		this.getGuiOverlay().setPotionShiftState(true);
	}


	@SubscribeEvent
	public void onMouseInputEvent(MouseClickedEvent event) {
		final Minecraft mc = Minecraft.getInstance();
		final MouseHelper mouse = mc.mouseHelper;
		final Screen gui = event.getGui();
		
		
		// Only accepts clicks - 0 = left, 1 = right, 2 = middle
		// if (event..getEventButton() < 0) { return; }
		if (!this.shouldDisplayGuiOverlay(gui)) {
			return;
		}
		
		double mouseX = mouse.getMouseX();
		double mouseY = mouse.getMouseY();

		final boolean shouldCancelEvent = this.getGuiOverlay().handleMouseInput(mouseX, mouseY, event.getButton());
			// Prevents clicks on the gui overlay dropping items on the world
			if(shouldCancelEvent) {
				event.setCanceled(shouldCancelEvent);
				this.getGuiOverlay().handleMouseInput(mouseX, mouseY, event.getButton());
			}
	}
	
	@SubscribeEvent
	public void onMouseReleasedEvent(MouseReleasedEvent event) {
		final Minecraft mc = Minecraft.getInstance();
		final MouseHelper mouse = mc.mouseHelper;
		final Screen gui = event.getGui();
		
		
		// Only accepts clicks - 0 = left, 1 = right, 2 = middle
		// if (event..getEventButton() < 0) { return; }
		if (!this.shouldDisplayGuiOverlay(gui)) {
			return;
		}
		
		double mouseX = mouse.getMouseX();
		double mouseY = mouse.getMouseY();

		final boolean shouldCancelEvent = this.getGuiOverlay().isMouseOverGui(mouseX, mouseY);
			// Prevents clicks on the gui overlay dropping items on the world
			if(shouldCancelEvent) {
				event.setCanceled(shouldCancelEvent);
				this.getGuiOverlay().handleMouseInput(mouseX, mouseY, event.getButton());
			}
	}

	@SubscribeEvent
	public void onKeyboardInputEvent(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
		if (!this.shouldDisplayGuiOverlay(event.getGui())) {
			return;
		}


		final int scaledWidth = Minecraft.getInstance().mainWindow.getScaledWidth();
		final int scaledHeight = Minecraft.getInstance().mainWindow.getScaledHeight();
		double mouseX = Minecraft.getInstance().mouseHelper.getMouseX() * scaledWidth / event.getGui().getMinecraft().mainWindow.getFramebufferWidth();
		double mouseY = scaledHeight - Minecraft.getInstance().mouseHelper.getMouseY() * scaledHeight /
						event.getGui().getMinecraft().mainWindow.getFramebufferHeight() - 1;

		this.getGuiOverlay().keyPressed(mouseX,mouseY,event.getKeyCode(),event.getScanCode());
	}


	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equalsIgnoreCase(VisibleArmorSlots.MOD_ID)) {
			// Refresh the display parameters when the config changes
			_cacheDisplayParams = new HashMap<String, InfoGuiOverlayDisplayParams>();
		}
	}


}
