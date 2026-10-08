package dev.joid.backend.minecraft.render.texture;

import java.nio.ByteBuffer;
import java.util.Optional;

import dev.joid.backend.minecraft.MinecraftBackend;
import dev.joid.backend.minecraft.render.RenderBridge;
import dev.joid.lib.bridge.render.texture.MipmapChain;
import dev.joid.lib.utils.image.PixelLayout;
import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

@Getter
public final class Texture extends dev.joid.lib.bridge.render.texture.Texture implements IGpuTexture {

	private static final RenderPipeline COPY = RenderPipeline.builder()
			.withLocation(Identifier.fromNamespaceAndPath(MinecraftBackend.MOD_ID, "pipeline/copy"))
			.withVertexShader(Identifier.fromNamespaceAndPath(MinecraftBackend.MOD_ID, "core/copy"))
			.withFragmentShader(Identifier.fromNamespaceAndPath(MinecraftBackend.MOD_ID, "core/copy"))
			.withBindGroupLayout(BindGroupLayouts.SAMPLER0)
			.withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
			.build();

	private final RenderBridge bridge;

	private GpuTexture     texture;
	private GpuTextureView view;

	private Texture(final RenderBridge bridge) {
		this.bridge = bridge;
	}

	public static @NonNull Texture create(final @NonNull RenderBridge bridge) {
		return new Texture(bridge);
	}

	public @NonNull Texture copy(final @NonNull GpuTextureView source, final int x, final int y, final int width, final int height) {
		super.allocate(width, height);
		final GpuTextureView target = this.bridge.getDevice().createTextureView(this.texture, 0, 1);
		try (RenderPass pass = this.bridge.getPassEncoder().encoder().createRenderPass(() -> "JOID Copy", target, Optional.empty())) {
			pass.setPipeline(Texture.COPY);
			pass.bindTexture("Sampler0", source, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
			pass.draw(3, 1, (x << 14 | y) << 2, 0);
		} finally {
			target.close();
		}

		this.generateLevels(MipmapChain.of(width, height, super.isMipmapped()));
		return this;
	}

	@Override
	protected void onAllocate(final @NonNull MipmapChain chain) {
		this.release();
		this.texture = this.createTexture(chain);
		this.view    = this.bridge.getDevice().createTextureView(this.texture);
	}

	@Override
	protected void onUpload(final @NonNull int[] pixels, final @NonNull MipmapChain chain) {
		final ByteBuffer data = PixelLayout.RGBA8.write(pixels, this.bridge.getScratch(chain.getWidth() * chain.getHeight() * 4));
		this.bridge.getPassEncoder().encoder().writeToTexture(this.texture, data, 0, 0, 0, 0, chain.getWidth(), chain.getHeight());
		this.generateLevels(chain);
	}

	@Override
	protected void onGenerateLevels(final @NonNull MipmapChain chain, final int allocatedLevels) {
		if (this.texture.getMipLevels() != Texture.getDeviceLevels(chain)) {
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
	protected void onDelete() {
		this.release();
	}

	private GpuTexture createTexture(final MipmapChain chain) {
		return this.bridge.getDevice().createTexture("JOID Texture", GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_COPY_SRC | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT, GpuFormat.RGBA8_UNORM, chain.getWidth(), chain.getHeight(), 1, Texture.getDeviceLevels(chain));
	}

	private void generateLevels(final MipmapChain chain) {
		final int levels = this.texture.getMipLevels();
		chain.forEachStep((level, _, _, _, _) -> {
			if (level >= levels) {
				return;
			}

			final GpuTextureView source = this.bridge.getDevice().createTextureView(this.texture, level - 1, 1);
			final GpuTextureView target = this.bridge.getDevice().createTextureView(this.texture, level, 1);
			try (RenderPass pass = this.bridge.getPassEncoder().encoder().createRenderPass(() -> "JOID Mipmap", target, Optional.empty())) {
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

	private static int getDeviceLevels(final MipmapChain chain) {
		return Math.min(chain.getLevels(), 32 - Integer.numberOfLeadingZeros(Math.max(1, Math.min(chain.getWidth(), chain.getHeight()))));
	}

}