package fr.augma.joidblaze3d.command;

import be.zeldown.joid.internal.JOID;
import fr.augma.joidblaze3d.test.TestMenuScreen;
import fr.augma.joidblaze3d.test.TestUI;
import lombok.NonNull;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.client.Minecraft;

public final class JOIDCommand {

	public static <T> @NonNull LiteralArgumentBuilder<T> create() {
		return LiteralArgumentBuilder.<T>literal("joid").then(LiteralArgumentBuilder.<T>literal("test").executes(context -> {
			Minecraft.getInstance().schedule(() -> JOID.open(new TestUI()));
			return 1;
		})).then(LiteralArgumentBuilder.<T>literal("menu").executes(context -> {
			Minecraft.getInstance().schedule(() -> Minecraft.getInstance().gui.setScreen(TestMenuScreen.create(Minecraft.getInstance().player)));
			return 1;
		}));
	}

}