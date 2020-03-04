package sidben.visiblearmorslots.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class SlotOffHand extends Slot {

	public SlotOffHand(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	@Override
	public String getSlotTexture() {
		return "minecraft:item/empty_armor_slot_shield";
	}

}