package dev.joid.backend.minecraft.lib.font.converter.impl;

import org.junit.Assert;
import org.junit.Test;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

public class ChatTextConverterTest {

	@Test
	public void keepsAPlainComponentAsItIs() {
		Assert.assertEquals("Hello", ChatTextConverter.inst().convert(Component.literal("Hello")));
	}

	@Test
	public void writesTheLegacyCodesOfAStyle() {
		Assert.assertEquals("\u00A7r\u00A7c\u00A7l\u00A7oHello", ChatTextConverter.inst().convert(Component.literal("Hello").withStyle(ChatFormatting.RED, ChatFormatting.BOLD, ChatFormatting.ITALIC)));
	}

	@Test
	public void writesEveryFormattingInTheMarkupOrder() {
		Assert.assertEquals("\u00A7r\u00A7k\u00A7l\u00A7m\u00A7n\u00A7oA", ChatTextConverter.inst().convert(Component.literal("A").withStyle(ChatFormatting.ITALIC, ChatFormatting.UNDERLINE, ChatFormatting.STRIKETHROUGH, ChatFormatting.BOLD, ChatFormatting.OBFUSCATED)));
	}

	@Test
	public void writesAHexColor() {
		Assert.assertEquals("\u00A7r\u00A7x\u00A71\u00A72\u00A7a\u00A7b\u00A7c\u00A7dHex", ChatTextConverter.inst().convert(Component.literal("Hex").withStyle(style -> style.withColor(TextColor.fromRgb(0x12ABCD)))));
	}

	@Test
	public void writesAPaletteColorAsItsLegacyCode() {
		Assert.assertEquals("\u00A7r\u00A76Gold", ChatTextConverter.inst().convert(Component.literal("Gold").withStyle(style -> style.withColor(TextColor.fromRgb(0xFFAA00)))));
	}

	@Test
	public void resetsBetweenSiblingsWithDifferentStyles() {
		final Component component = Component.literal("A").withStyle(ChatFormatting.BOLD).append(Component.literal("B").withStyle(ChatFormatting.GREEN)).append(Component.literal("C"));
		Assert.assertEquals("\u00A7r\u00A7lA\u00A7r\u00A7a\u00A7lB\u00A7r\u00A7lC", ChatTextConverter.inst().convert(component));
	}

	@Test
	public void mergesConsecutiveSegmentsWithTheSameStyle() {
		final Component component = Component.literal("A").withStyle(ChatFormatting.RED).append(Component.literal("B").withStyle(ChatFormatting.RED));
		Assert.assertEquals("\u00A7r\u00A7cAB", ChatTextConverter.inst().convert(component));
	}

}