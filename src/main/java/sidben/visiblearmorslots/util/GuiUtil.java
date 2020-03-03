package sidben.visiblearmorslots.util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.config.GuiUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class GuiUtil {

	public static void drawHoveringText(@Nonnull final ItemStack stack, List<ITextComponent> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
		GuiUtils.drawHoveringText(stack, strings(textLines), mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
	}

	public static void drawHoveringText(List<ITextComponent> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font){
		GuiUtils.drawHoveringText(strings(textLines), mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
	}
	public static List<String> strings(List<ITextComponent> textLines) {
		return textLines.stream().map(ITextComponent::getFormattedText).collect(Collectors.toList());
	}
}
