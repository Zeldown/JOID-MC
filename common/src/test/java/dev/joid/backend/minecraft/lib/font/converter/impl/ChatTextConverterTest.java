package dev.joid.backend.minecraft.lib.font.converter.impl;

import org.junit.Assert;
import org.junit.Test;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

public class ChatTextConverterTest {

	@Test
	public void keepsAPlainComponentAsItIs() {
		Assert.assertEquals("Hello", ChatTextConverter.inst().convert(new TextComponent("Hello")));
	}

	@Test
	public void writesTheLegacyCodesOfAStyle() {
		Assert.assertEquals("\u00A7r\u00A7c\u00A7l\u00A7oHello", ChatTextConverter.inst().convert(new TextComponent("Hello").withStyle(ChatFormatting.RED, ChatFormatting.BOLD, ChatFormatting.ITALIC)));
	}

	@Test
	public void writesEveryFormattingInTheMarkupOrder() {
		Assert.assertEquals("\u00A7r\u00A7k\u00A7l\u00A7m\u00A7n\u00A7oA", ChatTextConverter.inst().convert(new TextComponent("A").withStyle(ChatFormatting.ITALIC, ChatFormatting.UNDERLINE, ChatFormatting.STRIKETHROUGH, ChatFormatting.BOLD, ChatFormatting.OBFUSCATED)));
	}

	@Test
	public void writesAHexColor() {
		Assert.assertEquals("\u00A7r\u00A7x\u00A71\u00A72\u00A7a\u00A7b\u00A7c\u00A7dHex", ChatTextConverter.inst().convert(new TextComponent("Hex").withStyle(style -> style.withColor(TextColor.fromRgb(0x12ABCD)))));
	}

	@Test
	public void writesAPaletteColorAsItsLegacyCode() {
		Assert.assertEquals("\u00A7r\u00A76Gold", ChatTextConverter.inst().convert(new TextComponent("Gold").withStyle(style -> style.withColor(TextColor.fromRgb(0xFFAA00)))));
	}

	@Test
	public void resetsBetweenSiblingsWithDifferentStyles() {
		final Component component = new TextComponent("A").withStyle(ChatFormatting.BOLD).append(new TextComponent("B").withStyle(ChatFormatting.GREEN)).append(new TextComponent("C"));
		Assert.assertEquals("\u00A7r\u00A7lA\u00A7r\u00A7a\u00A7lB\u00A7r\u00A7lC", ChatTextConverter.inst().convert(component));
	}

	@Test
	public void mergesConsecutiveSegmentsWithTheSameStyle() {
		final Component component = new TextComponent("A").withStyle(ChatFormatting.RED).append(new TextComponent("B").withStyle(ChatFormatting.RED));
		Assert.assertEquals("\u00A7r\u00A7cAB", ChatTextConverter.inst().convert(component));
	}

}