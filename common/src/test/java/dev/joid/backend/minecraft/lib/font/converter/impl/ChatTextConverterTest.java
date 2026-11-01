package dev.joid.backend.minecraft.lib.font.converter.impl;

import org.junit.Assert;
import org.junit.Test;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class ChatTextConverterTest {

	@Test
	public void keepsAPlainComponentAsItIs() {
		Assert.assertEquals("Hello", ChatTextConverter.inst().convert(new TextComponentString("Hello")));
	}

	@Test
	public void writesTheLegacyCodesOfAStyle() {
		Assert.assertEquals("§r§c§l§oHello", ChatTextConverter.inst().convert(new TextComponentString("Hello").setStyle(new Style().setColor(TextFormatting.RED).setBold(true).setItalic(true))));
	}

	@Test
	public void writesEveryFormattingInTheMarkupOrder() {
		Assert.assertEquals("§r§k§l§m§n§oA", ChatTextConverter.inst().convert(new TextComponentString("A").setStyle(new Style().setItalic(true).setUnderlined(true).setStrikethrough(true).setBold(true).setObfuscated(true))));
	}

	@Test
	public void resetsBetweenSiblingsWithDifferentStyles() {
		final ITextComponent component = new TextComponentString("A").setStyle(new Style().setBold(true)).appendSibling(new TextComponentString("B").setStyle(new Style().setColor(TextFormatting.GREEN))).appendSibling(new TextComponentString("C"));
		Assert.assertEquals("§r§lA§r§a§lB§r§lC", ChatTextConverter.inst().convert(component));
	}

	@Test
	public void mergesConsecutiveSegmentsWithTheSameStyle() {
		final ITextComponent component = new TextComponentString("A").setStyle(new Style().setColor(TextFormatting.RED)).appendSibling(new TextComponentString("B").setStyle(new Style().setColor(TextFormatting.RED)));
		Assert.assertEquals("§r§cAB", ChatTextConverter.inst().convert(component));
	}

}