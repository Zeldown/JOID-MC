package dev.joid.backend.minecraft.bridge.audio;

import dev.joid.base.openal.IAudioGain;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftAudioGain implements IAudioGain {

	private static final MinecraftAudioGain INSTANCE = new MinecraftAudioGain();

	public static @NonNull MinecraftAudioGain inst() {
		return MinecraftAudioGain.INSTANCE;
	}

	@Override
	public float apply(final float gain, final Object group) {
		return gain * Minecraft.getInstance().options.getFinalSoundSourceVolume(group instanceof final SoundSource source ? source : SoundSource.UI);
	}

}