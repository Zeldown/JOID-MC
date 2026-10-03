package fr.augma.joidblaze3d.command;

import be.zeldown.joid.internal.JOID;
import fr.augma.joidblaze3d.test.TestEffectOverlayUI;
import fr.augma.joidblaze3d.test.TestHotbarOverlayUI;
import fr.augma.joidblaze3d.test.TestMenuScreen;
import fr.augma.joidblaze3d.test.TestOverlayUI;
import fr.augma.joidblaze3d.test.TestUI;
import lombok.NonNull;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.client.Minecraft;

public final class JOIDCommand {

	public static <T> @NonNull LiteralArgumentBuilder<T> create() {
		return LiteralArgumentBuilder.<T>literal("joid").then(LiteralArgumentBuilder.<T>literal("test").executes(context -> {
			Minecraft.getInstance().schedule(() -> JOID.open(new TestUI()));
			return 1;
		})).then(LiteralArgumentBuilder.<T>literal("overlay").executes(context -> {
			Minecraft.getInstance().schedule(() -> {
				if (JOID.isOpen(TestOverlayUI.class)) {
					JOID.close(JOID.getUI(TestOverlayUI.class));
				} else {
					JOID.open(new TestOverlayUI());
				}
			});
			return 1;
		})).then(LiteralArgumentBuilder.<T>literal("hud").executes(context -> {
			Minecraft.getInstance().schedule(() -> {
				if (JOID.isOpen(TestHotbarOverlayUI.class)) {
					JOID.close(JOID.getUI(TestHotbarOverlayUI.class));
					JOID.close(JOID.getUI(TestEffectOverlayUI.class));
				} else {
					JOID.open(new TestHotbarOverlayUI());
					JOID.open(new TestEffectOverlayUI());
				}
			});
			return 1;
		})).then(LiteralArgumentBuilder.<T>literal("menu").executes(context -> {
			Minecraft.getInstance().schedule(() -> Minecraft.getInstance().gui.setScreen(TestMenuScreen.create(Minecraft.getInstance().player)));
			return 1;
		}));
	}

}