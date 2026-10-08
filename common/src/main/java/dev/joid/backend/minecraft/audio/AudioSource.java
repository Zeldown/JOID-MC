package dev.joid.backend.minecraft.audio;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;

import dev.joid.lib.bridge.audio.AudioDownmix;
import dev.joid.lib.bridge.audio.IAudioSource;
import lombok.NonNull;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;

public final class AudioSource implements IAudioSource {

	private final int format;
	private final int channels;
	private final int sampleRate;

	private final List<Integer>  bufferList;
	private final Deque<Integer> freeBufferQueue;

	private int   source;
	private long  context;
	private float gain;

	private AudioSource(final int sampleRate, final int channels) {
		this.format          = channels > 1 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16;
		this.channels        = channels;
		this.sampleRate      = sampleRate;
		this.bufferList      = new ArrayList<>();
		this.freeBufferQueue = new ArrayDeque<>();
		this.gain            = 1F;
	}

	public static @NonNull AudioSource create(final int sampleRate, final int channels) {
		return new AudioSource(sampleRate, channels);
	}

	@Override
	public void play() {
		if (this.isAvailable()) {
			AL10.alSourcePlay(this.source);
		}
	}

	@Override
	public void stop() {
		if (this.isAvailable()) {
			AL10.alSourceStop(this.source);
		}
	}

	@Override
	public void clear() {
		if (!this.isAvailable()) {
			return;
		}

		AL10.alSourceStop(this.source);
		final int queued = AL10.alGetSourcei(this.source, AL10.AL_BUFFERS_QUEUED);
		for (int i = 0; i < queued; i++) {
			this.freeBufferQueue.push(AL10.alSourceUnqueueBuffers(this.source));
		}
	}

	@Override
	public void pause() {
		if (this.isAvailable()) {
			AL10.alSourcePause(this.source);
		}
	}

	@Override
	public void gain(final float gain) {
		this.gain = gain;
		if (this.isAvailable()) {
			this.applyGain();
		}
	}

	@Override
	public void queue(final @NonNull short[] samples) {
		if (!this.isAvailable()) {
			return;
		}

		final int buffer = this.nextBuffer();
		AL10.alBufferData(buffer, this.format, AudioDownmix.stereo(samples, this.channels), this.sampleRate);
		AL10.alSourceQueueBuffers(this.source, buffer);
	}

	@Override
	public boolean isPlaying() {
		return this.isAvailable() && AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}

	@Override
	public int getQueuedBuffers() {
		return this.isAvailable() ? AL10.alGetSourcei(this.source, AL10.AL_BUFFERS_QUEUED) : 0;
	}

	@Override
	public int getProcessedBuffers() {
		return this.isAvailable() ? AL10.alGetSourcei(this.source, AL10.AL_BUFFERS_PROCESSED) : 0;
	}

	@Override
	public void delete() {
		if (this.context != 0L && this.context == ALC10.alcGetCurrentContext()) {
			AL10.alSourceStop(this.source);
			AL10.alDeleteSources(this.source);
			for (final int buffer : this.bufferList) {
				AL10.alDeleteBuffers(buffer);
			}
		}

		this.context = 0L;
		this.bufferList.clear();
		this.freeBufferQueue.clear();
	}

	private boolean isAvailable() {
		final long current = ALC10.alcGetCurrentContext();
		if (current == 0L) {
			return false;
		}

		if (current != this.context) {
			this.context = current;
			this.bufferList.clear();
			this.freeBufferQueue.clear();
			this.source = AL10.alGenSources();
			AL10.alSourcei(this.source, AL10.AL_SOURCE_RELATIVE, AL10.AL_TRUE);
			AL10.alSource3f(this.source, AL10.AL_POSITION, 0F, 0F, 0F);
			this.applyGain();
		}
		return true;
	}

	private void applyGain() {
		AL10.alSourcef(this.source, AL10.AL_GAIN, this.gain * Minecraft.getInstance().options.getFinalSoundSourceVolume(SoundSource.UI));
	}

	private int nextBuffer() {
		if (AL10.alGetSourcei(this.source, AL10.AL_BUFFERS_PROCESSED) > 0) {
			return AL10.alSourceUnqueueBuffers(this.source);
		}

		if (!this.freeBufferQueue.isEmpty()) {
			return this.freeBufferQueue.pop();
		}

		final int buffer = AL10.alGenBuffers();
		this.bufferList.add(buffer);
		return buffer;
	}

}