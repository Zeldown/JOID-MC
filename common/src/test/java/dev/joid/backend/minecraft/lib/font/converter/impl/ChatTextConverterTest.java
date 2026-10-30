package dev.joid.backend.minecraft.lib.font.converter.impl;

import org.junit.Assert;
import org.junit.Test;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class ChatTextConverterTest {

	@Test
	public void keepsAPlainComponentAsItIs() {
		Assert.assertEquals("Hello", ChatTextConverter.inst().convert(new ChatComponentText("Hello")));
	}

	@Test
	public void writesTheLegacyCodesOfAStyle() {
		Assert.assertEquals("§r§c§l§oHello", ChatTextConverter.inst().convert(new ChatComponentText("Hello").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED).setBold(true).setItalic(true))));
	}

	@Test
	public void writesEveryFormattingInTheMarkupOrder() {
		Assert.assertEquals("§r§k§l§m§n§oA", ChatTextConverter.inst().convert(new ChatComponentText("A").setChatStyle(new ChatStyle().setItalic(true).setUnderlined(true).setStrikethrough(true).setBold(true).setObfuscated(true))));
	}

	@Test
	public void resetsBetweenSiblingsWithDifferentStyles() {
		final IChatComponent component = new ChatComponentText("A").setChatStyle(new ChatStyle().setBold(true)).appendSibling(new ChatComponentText("B").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))).appendSibling(new ChatComponentText("C"));
		Assert.assertEquals("§r§lA§r§a§lB§r§lC", ChatTextConverter.inst().convert(component));
	}

	@Test
	public void mergesConsecutiveSegmentsWithTheSameStyle() {
		final IChatComponent component = new ChatComponentText("A").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)).appendSibling(new ChatComponentText("B").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
		Assert.assertEquals("§r§cAB", ChatTextConverter.inst().convert(component));
	}

}