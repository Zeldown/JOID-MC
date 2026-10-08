package dev.joid.impl.minecraft.render.texture;

import java.nio.ByteBuffer;
import java.util.Optional;

import dev.joid.impl.minecraft.render.RenderBridge;
import dev.joid.lib.bridge.render.texture.ITexture;
import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.client.renderer.RenderPipelines;

@Getter
public final class Texture implements ITexture {

	private final RenderBridge bridge;

	private GpuTexture     texture;
	private GpuTextureView view;
	private int            width;
	private int            height;
	private int            levels;
	private boolean        deleted;
	private boolean        mipmapped;

	private Texture(final RenderBridge bridge) {
		this.bridge = bridge;
		this.levels = 1;
	}

	public static @NonNull Texture create(final @NonNull RenderBridge bridge) {
		return new Texture(bridge);
	}

	@Override
	public @NonNull Texture mipmap(final boolean mipmap) {
		if (this.mipmapped == mipmap) {
			return this;
		}

		this.mipmapped = mipmap;
		if (mipmap && this.texture != null && this.levels != Texture.levels(true, this.width, this.height)) {
			final GpuTexture source = this.texture;
			final GpuTextureView view = this.view;
			this.texture = this.createTexture(this.width, this.height);
			this.view    = this.bridge.getDevice().createTextureView(this.texture);
			this.bridge.getPassEncoder().encoder().copyTextureToTexture(source, this.texture, 0, 0, 0, 0, 0, this.width, this.height);
			view.close();
			source.close();
			this.generateLevels();
		}

		return this;
	}

	@Override
	public @NonNull Texture allocate(final int width, final int height) {
		if (this.texture != null && this.width == width && this.height == height && this.levels == Texture.levels(this.mipmapped, width, height)) {
			return this;
		}

		this.release();
		this.width   = width;
		this.height  = height;
		this.texture = this.createTexture(width, height);
		this.view    = this.bridge.getDevice().createTextureView(this.texture);
		return this;
	}

	@Override
	public @NonNull Texture upload(final @NonNull int[] pixels, final int width, final int height) {
		final ByteBuffer data = this.bridge.getScratch(width * height * 4);
		for (int i = 0; i < width * height; i++) {
			final int pixel = pixels[i];
			data.putInt(i * 4, pixel & 0xFF00FF00 | pixel >> 16 & 0xFF | (pixel & 0xFF) << 16);
		}

		this.bridge.getPassEncoder().encoder().writeToTexture(this.texture, data, 0, 0, 0, 0, width, height);
		if (this.mipmapped) {
			this.generateLevels();
		}

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

	private GpuTexture createTexture(final int width, final int height) {
		this.levels = Texture.levels(this.mipmapped, width, height);
		return this.bridge.getDevice().createTexture("JOID Texture", GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_COPY_SRC | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT, GpuFormat.RGBA8_UNORM, width, height, 1, this.levels);
	}

	private void generateLevels() {
		final CommandEncoder encoder = this.bridge.getPassEncoder().encoder();
		for (int level = 1; level < this.levels; level++) {
			final GpuTextureView source = this.bridge.getDevice().createTextureView(this.texture, level - 1, 1);
			final GpuTextureView target = this.bridge.getDevice().createTextureView(this.texture, level, 1);
			try (RenderPass pass = encoder.createRenderPass(() -> "JOID Mipmap", target, Optional.empty())) {
				RenderSystem.bindDefaultUniforms(pass);
				pass.setPipeline(RenderPipelines.TRACY_BLIT);
				pass.bindTexture("InSampler", source, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
				pass.draw(3, 1, 0, 0);
			} finally {
				source.close();
				target.close();
			}
		}
	}

	private void release() {
		if (this.texture == null) {
			return;
		}

		this.bridge.getPassEncoder().end();
		this.view.close();
		this.texture.close();
		this.view    = null;
		this.texture = null;
		this.levels  = 1;
	}

	private static int levels(final boolean mipmapped, final int width, final int height) {
		if (!mipmapped) {
			return 1;
		}

		return 32 - Integer.numberOfLeadingZeros(Math.max(1, Math.min(width, height)));
	}

}