package dev.joid.backend.minecraft.bridge.render.texture;

import java.nio.ByteBuffer;
import java.util.Optional;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.systems.DeviceInfo;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.lib.bridge.render.texture.MipmapChain;
import dev.joid.lib.bridge.render.texture.Texture;
import dev.joid.lib.utils.image.PixelLayout;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.RenderPipelines;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftTexture extends Texture implements IMinecraftTexture {

	private final MinecraftRenderBridge bridge;

	private GpuTexture     texture;
	private GpuTextureView view;

	public static @NonNull MinecraftTexture create(final MinecraftRenderBridge bridge) {
		return new MinecraftTexture(bridge);
	}

	@Override
	protected int getMaxLevels(final int width, final int height) {
		return 32 - Integer.numberOfLeadingZeros(Math.max(1, Math.min(width, height)));
	}

	@Override
	protected void allocateStorage(final @NonNull MipmapChain chain) {
		final DeviceInfo info = this.bridge.getDevice().getDeviceInfo();
		final int maxSize = info.limits().maxTextureSizeForFormat(GpuFormat.RGBA8_UNORM);
		if (chain.getWidth() > maxSize || chain.getHeight() > maxSize) {
			throw new IllegalArgumentException("A texture of " + chain.getWidth() + "x" + chain.getHeight() + " exceeds the maximum size " + maxSize + " of " + info.name() + ", downscale the image");
		}

		this.release();
		this.texture = this.createTexture(chain);
		this.view    = this.bridge.getDevice().createTextureView(this.texture);
	}

	@Override
	protected void uploadPixels(final @NonNull int[] pixels, final @NonNull MipmapChain chain) {
		final ByteBuffer data = PixelLayout.RGBA8.write(pixels, this.bridge.getScratch(chain.getWidth() * chain.getHeight() * 4));
		this.bridge.getPassEncoder().encoder().writeToTexture(this.texture, data, 0, 0, 0, 0, chain.getWidth(), chain.getHeight());
		this.generateLevels(chain);
	}

	@Override
	protected void generateMipmapLevels(final @NonNull MipmapChain chain, final int allocatedLevels) {
		if (allocatedLevels != chain.getLevels()) {
			final GpuTexture source = this.texture;
			final GpuTextureView view = this.view;
			this.texture = this.createTexture(chain);
			this.view    = this.bridge.getDevice().createTextureView(this.texture);
			this.bridge.getPassEncoder().encoder().copyTextureToTexture(source, this.texture, 0, 0, 0, 0, 0, chain.getWidth(), chain.getHeight());
			view.close();
			source.close();
		}

		this.generateLevels(chain);
	}

	@Override
	protected void deleteStorage() {
		this.release();
	}

	private GpuTexture createTexture(final MipmapChain chain) {
		return this.bridge.getDevice().createTexture("JOID Texture", GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_COPY_SRC | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT, GpuFormat.RGBA8_UNORM, chain.getWidth(), chain.getHeight(), 1, chain.getLevels());
	}

	private void generateLevels(final MipmapChain chain) {
		chain.forEachStep((level, _, _, _, _) -> {
			final GpuTextureView source = this.bridge.getDevice().createTextureView(this.texture, level - 1, 1);
			final GpuTextureView target = this.bridge.getDevice().createTextureView(this.texture, level, 1);
			try (final RenderPass pass = this.bridge.getPassEncoder().encoder().createRenderPass(() -> "JOID Mipmap", target, Optional.empty())) {
				RenderSystem.bindDefaultUniforms(pass);
				pass.setPipeline(RenderPipelines.TRACY_BLIT);
				pass.bindTexture("InSampler", source, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
				pass.draw(3, 1, 0, 0);
			} finally {
				source.close();
				target.close();
			}
		});
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
	}

}