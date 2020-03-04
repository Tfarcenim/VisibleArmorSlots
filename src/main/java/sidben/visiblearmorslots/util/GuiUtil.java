package sidben.visiblearmorslots.util;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.config.GuiUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class GuiUtil extends AbstractGui {

	public static void drawHoveringText(@Nonnull final ItemStack stack, List<ITextComponent> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
		GuiUtils.drawHoveringText(stack, strings(textLines), mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
	}

	public static void drawHoveringText(List<ITextComponent> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font){
		GuiUtils.drawHoveringText(strings(textLines), mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
	}
	public static List<String> strings(List<ITextComponent> textLines) {
		return textLines.stream().map(ITextComponent::getFormattedText).collect(Collectors.toList());
	}

		private static final GuiUtil instance = new GuiUtil();

		public static void drawGradientRect(int left, int top, int right, int bottom, int blitOffset, int startColor, int endColor) {
			int oldBlitOffset = instance.blitOffset;
			instance.blitOffset = blitOffset;
			instance.fillGradient(left, top, right, bottom, startColor, endColor);
			instance.blitOffset = oldBlitOffset;
		}

		public static void renderTooltip(Screen screen, ItemStack itemStack, int x, int y) {
			FontRenderer font = itemStack.getItem().getFontRenderer(itemStack);
			net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(itemStack);
			screen.renderTooltip(screen.getTooltipFromItem(itemStack), x, y, (font == null ? screen.getMinecraft().fontRenderer : font));
			net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
		}
	}
