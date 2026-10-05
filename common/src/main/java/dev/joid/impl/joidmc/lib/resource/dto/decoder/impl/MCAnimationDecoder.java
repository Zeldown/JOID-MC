package dev.joid.impl.joidmc.lib.resource.dto.decoder.impl;

import java.awt.image.BufferedImage;
import java.util.List;

import be.zeldown.joid.lib.bridge.BridgeHandler;
import be.zeldown.joid.lib.bridge.render.texture.ITexture;
import be.zeldown.joid.lib.resource.dto.ResourceData;
import be.zeldown.joid.lib.resource.dto.decoder.IResourceDecoder;
import dev.joid.impl.joidmc.lib.bridge.render.RenderBridge;
import dev.joid.impl.joidmc.lib.bridge.render.texture.Texture;
import lombok.NonNull;

import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;

public final class MCAnimationDecoder implements IResourceDecoder {

	private static final long TICK = 50L;

	private final int        width;
	private final int        height;
	private final ITexture[] frameList;
	private final long[]     endList;
	private final long       duration;

	public MCAnimationDecoder(final @NonNull AnimationMetadataSection section, final @NonNull BufferedImage image) {
		final FrameSize size = section.calculateFrameSize(image.getWidth(), image.getHeight());
		final int columns = Math.max(1, image.getWidth() / size.width());
		final int count = Math.max(1, columns * Math.max(1, image.getHeight() / size.height()));
		final List<AnimationFrame> frameList = section.frames().filter(list -> !list.isEmpty()).orElse(null);
		final int total = frameList == null ? count : frameList.size();
		final RenderBridge render = (RenderBridge) BridgeHandler.RENDER.get();
		this.width     = size.width();
		this.height    = size.height();
		this.frameList = new ITexture[total];
		this.endList   = new long[total];
		long duration = 0L;
		for (int index = 0; index < total; index++) {
			final int frame = Math.floorMod(frameList == null ? index : frameList.get(index).index(), count);
			final int[] pixels = image.getRGB(frame % columns * this.width, frame / columns * this.height, this.width, this.height, null, 0, this.width);
			duration += Math.max(1, frameList == null ? section.defaultFrameTime() : frameList.get(index).timeOr(section.defaultFrameTime())) * MCAnimationDecoder.TICK;
			this.frameList[index] = new Texture(render).allocate(this.width, this.height).upload(pixels, this.width, this.height);
			this.endList[index]   = duration;
		}

		this.duration = duration;
	}

	@Override
	public void init(final ResourceData data) {
		data.textures(this.frameList).width(this.width).height(this.height).loaded(true).uploaded(true);
	}

	@Override
	public void prepare(final ResourceData data) {}

	@Override
	public void decode(final ResourceData data) {}

	@Override
	public void upload(final ResourceData data) {}

	@Override
	public void update(final ResourceData data) {
		final long position = Math.floorMod(System.currentTimeMillis(), this.duration);
		for (int index = 0; index < this.endList.length; index++) {
			if (position < this.endList[index]) {
				data.texture(this.frameList[index]);
				return;
			}
		}
	}

	@Override
	public void clear(final ResourceData data) {
		for (final ITexture texture : this.frameList) {
			texture.delete();
		}
	}

}