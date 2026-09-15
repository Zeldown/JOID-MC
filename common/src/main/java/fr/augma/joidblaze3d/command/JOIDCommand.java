package fr.augma.joidblaze3d.command;

import be.zeldown.joid.demo.ui.UIDemoChoice;
import be.zeldown.joid.internal.JOID;
import lombok.NonNull;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.client.Minecraft;

public final class JOIDCommand {

	public static <T> @NonNull LiteralArgumentBuilder<T> create() {
		return LiteralArgumentBuilder.<T>literal("joid").executes(context -> {
			Minecraft.getInstance().schedule(() -> JOID.open(new UIDemoChoice()));
			return 1;
		});
	}

}