package sidben.visiblearmorslots.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sidben.visiblearmorslots.helper.ExtraSlotsHelper;
import sidben.visiblearmorslots.inventory.ContainerChestCustom;
import sidben.visiblearmorslots.inventory.ContainerRepairCustom;



@SideOnly(Side.CLIENT)
public class GuiChestCustom extends GuiChest
{


    public GuiChestCustom(IInventory upperInv, IInventory lowerInv) {
        super(upperInv, lowerInv);

        final ContainerChestCustom customContainer = new ContainerChestCustom(upperInv, lowerInv, Minecraft.getMinecraft().thePlayer);

        this.inventorySlots = customContainer;
        // ObfuscationReflectionHelper.setPrivateValue(GuiChest.class, this, customContainer, "anvil", "field_147092_v");
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        // Draws the extra slots
        ExtraSlotsHelper.drawExtraSlotsOnGui(this, this.xSize, this.ySize);

        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }


}