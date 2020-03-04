package sidben.visiblearmorslots.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import sidben.visiblearmorslots.VisibleArmorSlots;
import sidben.visiblearmorslots.client.gui.InfoExtraSlots.EnumSlotType;
import sidben.visiblearmorslots.handler.action.SlotActionManager;
import sidben.visiblearmorslots.handler.action.SlotActionType;
import sidben.visiblearmorslots.inventory.SlotArmor;
import sidben.visiblearmorslots.inventory.SlotOffHand;
import sidben.visiblearmorslots.util.GuiUtil;
import sidben.visiblearmorslots.util.LogHelper;

import java.util.List;


/**
 * This class simulates a simplified GuiContainer that runs only on the client,
 * since it uses the player inventory.<br/>
 * <br/>
 * <p>
 * Using Forge hooks on {@link sidben.visiblearmorslots.handler.EventDelegatorGuiOverlay EventDelegatorGuiOverlay},
 * this class simulates the regular flow of a GUI.
 */
public class GuiExtraSlotsOverlay extends AbstractGui {

	private static final ResourceLocation GUI_EXTRA_SLOTS = new ResourceLocation(VisibleArmorSlots.MOD_ID + ":textures/gui/extra-slots.png");
	public static final int GUI_WIDTH = 24;
	public static final int GUI_HEIGHT = 100;

	/**
	 * holds the slot currently hovered
	 */
	private Slot theSlot;
	private int eventButton;
	private long lastMouseEvent;
	private boolean _potionShiftActive;
	private int _externalGuiLeft;
	private int _externalGuiTop;

	protected List<InfoExtraSlots> supportedSlotsInfo;
	protected List<Slot> extraSlots;
	protected Minecraft mc;
	protected ItemRenderer itemRender;
	protected FontRenderer fontRenderer;

	public int screenWidth;
	public int screenHeight;
	public int guiLeft;
	public int guiTop;


	public GuiExtraSlotsOverlay() {
		supportedSlotsInfo = Lists.newArrayList();
		loadSupportedSlotsInfo(supportedSlotsInfo);

		extraSlots = Lists.newArrayList();

		this.mc = Minecraft.getInstance();
		this.itemRender = mc.getItemRenderer();
		this.fontRenderer = mc.fontRenderer;
	}


	// -----------------------------------------------------------
	// Slots info
	// -----------------------------------------------------------

	protected void loadSupportedSlotsInfo(List<InfoExtraSlots> list) {
		final int xStart = 4;       // Gui margin
		final int yStart = 4;       // Gui margin

		list.add(new InfoExtraSlots(EnumSlotType.ARMOR, xStart, (18 * 0) + yStart, 39, 5));     // Helmet
		list.add(new InfoExtraSlots(EnumSlotType.ARMOR, xStart, (18 * 1) + yStart, 38, 6));     // Chest
		list.add(new InfoExtraSlots(EnumSlotType.ARMOR, xStart, (18 * 2) + yStart, 37, 7));     // Legs
		list.add(new InfoExtraSlots(EnumSlotType.ARMOR, xStart, (18 * 3) + yStart, 36, 8));     // Boots
		list.add(new InfoExtraSlots(EnumSlotType.OFF_HAND, xStart, 76 + yStart, 40, 45));       // Off-hand
	}


	public void refreshExtraSlotsInfo(PlayerInventory inventoryplayer) {
		extraSlots = Lists.newArrayList();

		for (final InfoExtraSlots slotInfo : supportedSlotsInfo) {
			Slot extraSlot = null;

			if (slotInfo.getSlotType() == EnumSlotType.ARMOR) {
				extraSlot = new SlotArmor(inventoryplayer, slotInfo.getInventorySlotIndex(), slotInfo.getX(), slotInfo.getY());
			} else if (slotInfo.getSlotType() == EnumSlotType.OFF_HAND) {
				extraSlot = new SlotOffHand(inventoryplayer, slotInfo.getInventorySlotIndex(), slotInfo.getX(), slotInfo.getY());
			}

			if (extraSlot != null) {
				extraSlot.slotNumber = slotInfo.getContainerSlotIndex();
				extraSlots.add(extraSlot);
			}
		}
	}


	// -----------------------------------------------------------
	// Gui parameters
	// -----------------------------------------------------------

	public void setWorldAndResolution(int width, int height) {
		this.screenWidth = width;
		this.screenHeight = height;

		// Reset values that may leak from other gui's
		this.theSlot = null;
	}


	public void setExternalGuiPosition(Screen gui) {
		if (gui instanceof ContainerScreen) {
			final int candidateGuiLeft = ((ContainerScreen) gui).getGuiLeft();
			final int candidateGuiTop = ((ContainerScreen) gui).getGuiTop();

			// -- NOTE --
			// The creative inventory would cause this method to be fired twice, once for
			// net.minecraft.client.gui.inventory.GuiContainerCreative (with correct values)
			// and once for net.minecraft.client.gui.inventory.GuiInventory (with wrong values).
			//
			// I ignore the second call so the gui overlay preserves the correct values.
			if (gui instanceof InventoryScreen && candidateGuiLeft == 0 && candidateGuiTop == 0) {
				return;
			}

			this._externalGuiLeft = candidateGuiLeft;
			this._externalGuiTop = candidateGuiTop;

		} else {
			this._externalGuiLeft = -1;
			this._externalGuiTop = -1;

		}

		LogHelper.trace("GuiExtraSlotsOverlay.setExternalGuiPosition() - left: %d, top: %d", this._externalGuiLeft, this._externalGuiTop);
	}

	/*
	 * Informs if the potion effects pushed the original gui to the side.
	 */
	public boolean isPotionShiftActive() {
		return this._potionShiftActive;
	}

	/*
	 * Holds the information if the potion effects pushed the original gui to the side.
	 */
	public void setPotionShiftState(boolean active) {
		this._potionShiftActive = active;
	}


	// -----------------------------------------------------------
	// Gui drawing
	// -----------------------------------------------------------

	/**
	 * Draws the extra slots overlay slots and their contents.
	 */
	public void render(double mouseX, double mouseY) {
		this.theSlot = null;

		RenderHelper.enableGUIStandardItemLighting();


		// Draw the slots
		GlStateManager.pushMatrix();
		GlStateManager.translated(this.guiLeft, this.guiTop, 0);
		for (final Slot slot : extraSlots) {
			// Slot items
			if (slot.isEnabled()) {
				this.drawSlot(slot);
			}

			// Hover box
			if (this.isMouseOverSlot(slot, mouseX, mouseY) && slot.isEnabled()) {
				final int hoverX = slot.xPos;
				final int hoverY = slot.yPos;
				this.theSlot = slot;

				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				GlStateManager.colorMask(true, true, true, false);
				GuiUtil.drawGradientRect(hoverX, hoverY, hoverX + 16, hoverY + 16, -600, -2130706433, -2130706433);
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}
		}
		GlStateManager.popMatrix();
		renderHoveredTooltip(mouseX,mouseY);

		RenderHelper.disableStandardItemLighting();
	}


	/**
	 * Draws the extra slots overlay background.
	 */
	public void drawBackground(double mouseX, double mouseY) {
		final int textureStartX = 0;
		final int textureStartY = 62;
		final int textureWidth = 24;
		final int textureHeight = 100;
		final int startX = guiLeft;
		final int startY = guiTop;

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_EXTRA_SLOTS);
		this.blit(startX, startY, textureStartX, textureStartY, textureWidth, textureHeight);

	}

	/**
	 * Draws the extra slots overlay tooltips.
	 */
	public void renderHoveredTooltip(double mouseX, double mouseY) {
		final PlayerInventory inventoryplayer = this.mc.player.inventory;
		final ItemStack playerItemStack = inventoryplayer.getItemStack();

		// Tooltip
		if (playerItemStack.isEmpty() && this.theSlot != null && this.theSlot.getHasStack()) {
			final ItemStack slotStack = this.theSlot.getStack();
			this.renderToolTip(slotStack, mouseX, mouseY);
		}
	}


	private void drawSlot(Slot slot) {
		final int x = slot.xPos;
		final int y = slot.yPos;
		final ItemStack itemstack = slot.getStack();

		// Slot background
		if (itemstack.isEmpty()) {
			final TextureAtlasSprite textureatlassprite = slot.getBackgroundSprite();

			if (textureatlassprite != null) {
				GlStateManager.disableLighting();
				this.mc.getTextureManager().bindTexture(slot.getBackgroundLocation());
				blit(x, y,blitOffset, 16, 16,textureatlassprite);
				GlStateManager.enableLighting();
			}
		}
		// Slot item
		else {
			itemRender.renderItemAndEffectIntoGUI(mc.player, itemstack, x, y);
			itemRender.renderItemOverlayIntoGUI(fontRenderer, itemstack, x, y, null);
		}
	}


	protected void renderToolTip(ItemStack stack, double x, double y) {
		final List<ITextComponent> list = stack.getTooltip(this.mc.player,this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);

		for (int i = 0; i < list.size(); ++i) {
			if (i == 0) {
				list.set(i,list.get(i).applyTextStyle(stack.getRarity().color));
			} else {
				list.set(i, list.get(i).applyTextStyle(TextFormatting.GRAY));
			}
		}

		FontRenderer font = stack.getItem().getFontRenderer(stack);
		font = font == null ? fontRenderer : font;
		net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
		GuiUtil.drawHoveringText(list,(int) x,(int) y, this.screenWidth, this.screenHeight, -1, font);
		net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
	}


	// -----------------------------------------------------------
	// Mouse interaction
	// -----------------------------------------------------------

	/**
	 * @return Should cancel the mouse event (since it was handled by this gui).
	 * @param mouseX
	 * @param mouseY
	 */

	public boolean handleMouseInput(double mouseX, double mouseY,int clickedButton) {
		InputMappings.Input mouseKey = InputMappings.Type.MOUSE.getOrMakeInput(clickedButton);

		if (!this.isMouseOverGui(mouseX, mouseY)) {
			return false;
		}


		if (true) {
			this.eventButton = clickedButton;
			this.lastMouseEvent = System.currentTimeMillis();
			this.mouseClicked(mouseX, mouseY, this.eventButton);
		} else if (clickedButton != -1) {
			this.eventButton = -1;
		} else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
			final long l = System.currentTimeMillis() - this.lastMouseEvent;
			this.mouseClickMove(mouseX, mouseY, this.eventButton, l);
		}

		// Needed to avoid clicks on the gui overlay being interpreted as clicks outside the open gui,
		// causing the items on the mouse to drop.
		return true;
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	protected void mouseClicked(double mouseX, double mouseY, int clickedButton) {
		/*
		 * About {@link net.minecraft.inventory.ClickTypes ClickTypes}:
		 *
		 * - PICKUP: Mouse clicked on the slot (left or right button)
		 * - PICKUP_ALL: Double-click on the slot, pick all items of the type
		 * - CLONE: Middle click, creative only
		 * - QUICK_MOVE: Shift + click
		 * - SWAP: Swap with hotbar when a number is pressed (can use this.slotUnderTheMouse)
		 * - THROW: Click outside the gui
		 *
		 */

		// LogHelper.trace(" mouseClicked(%d, %d, %d)", mouseX, mouseY, clickedButton);

		InputMappings.Input mouseKey = InputMappings.Type.MOUSE.getOrMakeInput(clickedButton);


		final boolean isButtonPickBlock = this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey);
		final Slot slot = this.getSlotAtPosition(mouseX, mouseY, true, false);

		// NOTE: I don't need to handle the ClickType.THROW, the parent gui will take care of it.
		if (slot == null) {
			return;
		}


		// TODO: handle dragging (?)
		// TODO: handle ClickType.PICKUP_ALL (?)

		final PlayerEntity player = this.mc.player;
		final SlotActionType.EnumMouseAction slotMouseButton = SlotActionType.EnumMouseAction.create(clickedButton, isButtonPickBlock);
		final SlotActionType slotAction = SlotActionType.create(player, slot, Screen.hasShiftDown(), slotMouseButton);

		if (slotAction.isValid()) {
			SlotActionManager.instance.processActionOnClient(slotAction, slot, this.mc.player);
		}
	}


	/**
	 * Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX, mouseY,
	 * lastButtonClicked & timeSinceMouseClick.
	 */
	protected void mouseClickMove(double mouseX, double mouseY, int clickedMouseButton, long timeSinceLastClick) {
		// LogHelper.trace(" mouseClickMove(%d, %d, %d, %d)", mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}


	/**
	 * Called when a mouse button is released.
	 */
	public boolean mouseReleased(double mouseX, double mouseY, int clickedButton) {
		// Needed to avoid clicks on the gui overlay being interpreted as clicks outside the open gui,
		// causing the items on the mouse to drop.
			if (!this.isMouseOverGui(mouseX, mouseY)) {
				return false;
			}

			return true;
		}


	// -----------------------------------------------------------
	// Keyboard interaction
	// -----------------------------------------------------------

	public boolean keyPressed(double mouseX, double mouseY, int keyChar, int scanCode) {

		// TODO: Ignore this event on the anvil renaming (need to make nameField visible)

		if (keyChar == 0 || keyChar >= 32) {
			this.keyTyped(mouseX,mouseY,keyChar,scanCode);
			return true;
		}
		return false;
	}

	protected void keyTyped(double mouseX, double mouseY, int keyCode, int scanCode) {
		final PlayerEntity player = this.mc.player;
		Slot slot = this.theSlot;       // Slot under the mouse (if the mouse is inside the gui overlay)
		SlotActionType.EnumKeyboardAction keyboardAction = SlotActionType.EnumKeyboardAction.INVALID;

		// NOTE: KeyBinding.isActiveAndMatches() will check if the gui is active, so it would always return false here.
		if (this.mc.gameSettings.keyBindSwapHands.isKeyDown()) {
			if (this.theSlot == null) {
				// The mouse is outside the overlay gui, try to find the slot of the open container

				slot = this.getSlotAtPosition(mouseX, mouseY, false, true);
			}
			keyboardAction = SlotActionType.EnumKeyboardAction.SWAP_HANDS;

		} else {
			// test hotbar swap key
			for (int i = 0; i < 9; i++) {
				if (this.mc.gameSettings.keyBindsHotbar[i].isActiveAndMatches(InputMappings.getInputByCode(keyCode,scanCode))) {
					keyboardAction = SlotActionType.EnumKeyboardAction.createHotbar(i);
					break;
				}
			}
		}

		final SlotActionType slotAction = SlotActionType.create(player, slot, keyboardAction);

		if (slotAction.isValid()) {
			SlotActionManager.instance.processActionOnClient(slotAction, slot, this.mc.player);
		}
	}


	// -----------------------------------------------------------
	// Utility
	// -----------------------------------------------------------

	/**
	 * Returns if the mouse is over this GUI.
	 */
	private boolean isMouseOverGui(double mouseX, double mouseY) {
		return this.isPointInRegion(0, 0, GUI_WIDTH, GUI_HEIGHT, mouseX, mouseY, false);
	}


	/**
	 * Returns whether the mouse is over the given slot (only check the gui overlay).
	 */
	private boolean isMouseOverSlot(Slot slot, double mouseX, double mouseY) {
		return this.isPointInRegion(slot.xPos, slot.yPos, 16, 16, mouseX, mouseY, false);
	}


	/**
	 * Returns whether the mouse is over the given slot (only check the external gui).
	 */
	private boolean isMouseOverExternalSlot(Slot slot, double mouseX, double mouseY) {
		return this.isPointInRegion(slot.xPos, slot.yPos, 16, 16, mouseX, mouseY, true);
	}


	/**
	 * Test if the 2D point is in a rectangle (relative to the GUI). Args : rectX, rectY, rectWidth, rectHeight, pointX,
	 * pointY
	 */
	protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, double pointX, double pointY, boolean lookAtExternalGui) {
		if (lookAtExternalGui && this._externalGuiLeft < 0 && this._externalGuiTop < 0) {
			return false;
		}
		final int i = lookAtExternalGui ? this._externalGuiLeft : this.guiLeft;
		final int j = lookAtExternalGui ? this._externalGuiTop : this.guiTop;
		pointX = pointX - i;
		pointY = pointY - j;
		return pointX >= rectX - 1 && pointX < rectX + rectWidth + 1 && pointY >= rectY - 1 && pointY < rectY + rectHeight + 1;
	}


	/**
	 * Returns the slot at the given coordinates or null if there is none.
	 */
	private Slot getSlotAtPosition(double x,double y, boolean checkGuiOverlay, boolean checkExternalGui) {
		if (checkGuiOverlay) {
			for (final Slot internalSlot : extraSlots) {
				if (this.isMouseOverSlot(internalSlot, x, y) && internalSlot.isEnabled()) {
					return internalSlot;
				}
			}
		}

		// TODO: try refactor to use getSlotUnderMouse() of the external gui
		if (checkExternalGui && this.mc.player.openContainer != null && this.mc.player.openContainer.inventorySlots.size() > 0) {
			for (final Slot externalSlot : this.mc.player.openContainer.inventorySlots) {
				if (this.isMouseOverExternalSlot(externalSlot, x, y) && externalSlot.isEnabled()) {
					return externalSlot;
				}
			}
		}

		return null;
	}

}
