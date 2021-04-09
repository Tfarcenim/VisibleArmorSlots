package sidben.visiblearmorslots.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class GuiUtil extends AbstractGui {

	private static final GuiUtil instance = new GuiUtil();

		public static void drawGradientRect(MatrixStack matrices,int left, int top, int right, int bottom, int blitOffset, int startColor, int endColor) {
			int oldBlitOffset = instance.getBlitOffset();
			instance.setBlitOffset(blitOffset);
			instance.fillGradient(matrices,left, top, right, bottom, startColor, endColor);
			instance.setBlitOffset(oldBlitOffset);
		}

}
