package dev.joid.impl.joidmc.demo.ui.font;

import dev.joid.demo.ui.UIDemo;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.builder.Text;
import dev.joid.lib.font.dto.TextInfo;
import dev.joid.lib.ui.node.impl.design.text.TextNode;
import dev.joid.lib.utils.align.Align;
import dev.joid.impl.joidmc.lib.font.impl.minecraft.MinecraftFont;

public final class UIDemoFont extends UIDemo {

	private static final String[] SAMPLE_LIST = {
			"&c&lBold &a&oItalic &b&nUnderline &e&mStrike &d&kObfus",
			"&00&11&22&33&44&55&66&77&88&99&aa&bb&cc&dd&ee&ff",
			"&9&l&nBold underlined &rback to normal",
			"&6Shadowed &l&6bold shadowed"};

	@Override
	public void init() {
		for (int index = 0; index < UIDemoFont.SAMPLE_LIST.length; index++) {
			final TextInfo info = TextInfo.create(MinecraftFont.MINECRAFT, 40F, Color.WHITE);
			TextNode
			.create(960, 260 + index * 110)
			.text(Text.create(UIDemoFont.SAMPLE_LIST[index], index == 3 ? info.shadow(Color.BLACK) : info, Align.CENTER))
			.anchorX(Align.CENTER)
			.attach(this);
		}
	}

}