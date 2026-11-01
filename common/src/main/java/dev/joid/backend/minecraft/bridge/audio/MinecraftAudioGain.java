package dev.joid.backend.minecraft.bridge.audio;

import dev.joid.base.openal.AudioGain;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.settings.GameSettings;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftAudioGain implements AudioGain {

	private static final MinecraftAudioGain INSTANCE = new MinecraftAudioGain();

	public static @NonNull MinecraftAudioGain inst() {
		return MinecraftAudioGain.INSTANCE;
	}

	@Override
	public float apply(final float gain, final Object group) {
		final GameSettings settings = Minecraft.getMinecraft().gameSettings;
		final float master = settings.getSoundLevel(SoundCategory.MASTER);
		if (group instanceof SoundCategory && group != SoundCategory.MASTER) {
			return gain * master * settings.getSoundLevel((SoundCategory) group);
		}
		return gain * master;
	}

}