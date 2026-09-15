package fr.augma.joidblaze3d.render.texture;

import be.zeldown.joid.lib.bridge.render.texture.ITexture;
import fr.augma.joidblaze3d.render.RenderBridge;
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
	private boolean        deleted;

	public Texture(final RenderBridge bridge) {
		this.bridge = bridge;
	}

	@Override
	public @NonNull Texture allocate(final int width, final int height) {
		if (this.texture != null && this.width == width && this.height == height) {
			return this;
		}

		this.release();
		this.texture = RenderSystem.getDevice().createTexture("JOID Texture", GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_COPY_SRC | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT, GpuFormat.RGBA8_UNORM, width, height, 1, 1);
		this.view    = RenderSystem.getDevice().createTextureView(this.texture);
		this.width   = width;
		this.height  = height;
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