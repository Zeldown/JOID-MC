package fr.augma.joidmc.lib.bridge.audio;

import be.zeldown.joid.lib.bridge.audio.IAudioBridge;
import be.zeldown.joid.lib.bridge.audio.IAudioSource;
import lombok.NonNull;

public final class AudioBridge implements IAudioBridge {

	@Override
	public @NonNull IAudioSource createSource(final int sampleRate, final int channels) {
		return new AudioSource(sampleRate, channels);
	}

}