package dev.joid.backend.minecraft.bridge.audio;

import dev.joid.base.openal.AudioGain;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.sounds.SoundSource;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftAudioGain implements AudioGain {

	private static final MinecraftAudioGain INSTANCE = new MinecraftAudioGain();

	public static @NonNull MinecraftAudioGain inst() {
		return MinecraftAudioGain.INSTANCE;
	}

	@Override
	public float apply(final float gain, final Object group) {
		final Options options = Minecraft.getInstance().options;
		final float master = options.getSoundSourceVolume(SoundSource.MASTER);
		if (group instanceof SoundSource && group != SoundSource.MASTER) {
			return gain * master * options.getSoundSourceVolume((SoundSource) group);
		}
		return gain * master;
	}

}