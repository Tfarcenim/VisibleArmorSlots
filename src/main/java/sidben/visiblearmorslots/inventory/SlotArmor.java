package sidben.visiblearmorslots.inventory;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class SlotArmor extends Slot {

	private static final String[] ARMOR_SLOT_TEXTURES = new String[]{"item/empty_armor_slot_boots", "item/empty_armor_slot_leggings", "item/empty_armor_slot_chestplate", "item/empty_armor_slot_helmet"};


	private static final EquipmentSlotType[] armorSloyArray = new EquipmentSlotType[]{EquipmentSlotType.FEET, EquipmentSlotType.LEGS, EquipmentSlotType.CHEST, EquipmentSlotType.HEAD};

	private final PlayerEntity thePlayer;
	private int slotTypeIndex = 0;


	public SlotArmor(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);

		thePlayer = ((PlayerInventory) inventoryIn).player;
		slotTypeIndex = index - 36;
		slotTypeIndex = Math.min(Math.max(slotTypeIndex, 0), armorSloyArray.length - 1);
	}


	/**
	 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1
	 * in the case of armor slots)
	 */
	@Override
	public int getSlotStackLimit() {
		return 1;
	}


	/**
	 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
	 */
	@Override
	public boolean isItemValid(ItemStack stack) {
		if (stack == null) {
			return false;
		} else {
			return stack.getItem().canEquip(stack, armorSloyArray[slotTypeIndex], thePlayer);
		}
	}


	@Override
	@OnlyIn(Dist.CLIENT)
	public String getSlotTexture() {
		return ARMOR_SLOT_TEXTURES[slotTypeIndex];
	}


	@Override
	public boolean canTakeStack(PlayerEntity player) {
		final ItemStack itemstack = this.getStack();
		return (itemstack.isEmpty() || player.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.canTakeStack(player);
	}


}
