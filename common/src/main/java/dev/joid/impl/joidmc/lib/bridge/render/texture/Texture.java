package dev.joid.impl.joidmc.lib.bridge.render.texture;

import dev.joid.lib.bridge.render.texture.ITexture;
import dev.joid.impl.joidmc.lib.bridge.render.RenderBridge;
import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

@Getter
public final class Texture implements ITexture {

	private final RenderBridge bridge;

	private GpuTexture     texture;
	private GpuTextureView view;
	private int            width;
	private int            height;
	private boolean        mipmapped = true;
	private boolean        deleted;

	public Texture(final RenderBridge bridge) {
		this.bridge = bridge;
	}

	@Override
	public @NonNull Texture allocate(final int width, final int height) {
		final int levels = this.levels(width, height);
		if (this.texture != null && this.width == width && this.height == height && this.texture.getMipLevels() == levels) {
			return this;
		}

		this.release();
		this.texture = RenderSystem.getDevice().createTexture("JOID Texture", GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_COPY_SRC | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT, GpuFormat.RGBA8_UNORM, width, height, 1, levels);
		this.view    = RenderSystem.getDevice().createTextureView(this.texture);
		this.width   = width;
		this.height  = height;
		return this;
	}

	@Override
	public @NonNull Texture mipmap(final boolean mipmap) {
		if (this.mipmapped != mipmap) {
			this.mipmapped = mipmap;
			this.release();
		}

		return this;
	}

	@Override
	public boolean isMipmapped() {
		return this.texture != null && this.texture.getMipLevels() > 1;
	}

	public @NonNull Texture borrow(final @NonNull GpuTextureView view) {
		this.release();
		this.view   = view;
		this.width  = view.getWidth(0);
		this.height = view.getHeight(0);
		return this;
	}

	@Override
	public @NonNull Texture upload(final @NonNull int[] pixels, final int width, final int height) {
		this.bridge.upload(this.texture, pixels, width, height);
		return this;
	}

	@Override
	public void delete() {
		if (this.deleted) {
			return;
		}

		this.release();
		this.deleted = true;
	}

	private int levels(final int width, final int height) {
		if (!this.mipmapped) {
			return 1;
		}

		int levels = 1;
		for (int size = Math.max(width, height); size > 1; size >>= 1) {
			levels++;
		}

		return levels;
	}

	private void release() {
		if (this.texture == null) {
			return;
		}

		this.bridge.flush();
		this.view.close();
		this.texture.close();
		this.view    = null;
		this.texture = null;
	}

}