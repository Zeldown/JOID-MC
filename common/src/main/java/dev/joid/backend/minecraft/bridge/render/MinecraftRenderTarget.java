package dev.joid.backend.minecraft.bridge.render;

import org.joml.Vector4f;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

import dev.joid.backend.minecraft.bridge.render.texture.MinecraftTexture;
import lombok.Getter;
import lombok.NonNull;

@Getter
public final class MinecraftRenderTarget {

	private final MinecraftTexture texture;

	private final GpuTexture     depth;
	private final GpuTextureView depthView;

	private final GpuTexture     stencil;
	private final GpuTextureView stencilView;

	private final GpuTexture     stencilCopy;
	private final GpuTextureView stencilCopyView;

	private boolean               pending;
	private MinecraftRenderTarget viewport;

	public MinecraftRenderTarget(final MinecraftRenderBridge bridge, final int width, final int height, final boolean stencil) {
		final GpuDevice device = bridge.getDevice();
		this.texture     = MinecraftTexture.create(bridge);
		this.depth       = device.createTexture("JOID Depth", GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_RENDER_ATTACHMENT, GpuFormat.D32_FLOAT, width, height, 1, 1);
		this.stencil     = stencil ? MinecraftRenderTarget.createStencil(device, "JOID Stencil", width, height) : null;
		this.stencilCopy = stencil ? MinecraftRenderTarget.createStencil(device, "JOID Stencil Copy", width, height) : null;
		this.texture.allocate(width, height);

		final CommandEncoder encoder = bridge.getPassEncoder().encoder();
		encoder.clearColorTexture(this.texture.getTexture(), new Vector4f(0F));
		encoder.clearDepthTexture(this.depth, 1D);
		this.depthView       = device.createTextureView(this.depth);
		this.stencilView     = stencil ? device.createTextureView(this.stencil) : null;
		this.stencilCopyView = stencil ? device.createTextureView(this.stencilCopy) : null;
	}

	public void delete() {
		if (this.viewport != null) {
			this.viewport.delete();
		}
		this.texture.delete();
		this.depthView.close();
		this.depth.close();
		if (this.stencil != null) {
			this.stencilView.close();
			this.stencil.close();
			this.stencilCopyView.close();
			this.stencilCopy.close();
		}
	}

	public @NonNull MinecraftRenderTarget viewport(final MinecraftRenderBridge bridge, final int width, final int height) {
		if (this.viewport == null || this.viewport.getWidth() != width || this.viewport.getHeight() != height) {
			if (this.viewport != null) {
				this.viewport.delete();
			}
			this.viewport = new MinecraftRenderTarget(bridge, width, height, this.stencil != null);
		}
		return this.viewport;
	}

	public void markPending() {
		this.pending = true;
	}

	public void clearPending() {
		this.pending = false;
	}

	public void resolve(final CommandEncoder encoder) {
		if (this.viewport != null && this.viewport.pending) {
			encoder.copyTextureToTexture(this.viewport.getTexture().getTexture(), this.texture.getTexture(), 0, 0, 0, 0, 0, this.viewport.getWidth(), this.viewport.getHeight());
			this.viewport.pending = false;
		}
	}

	public int getWidth() {
		return this.texture.getWidth();
	}

	public int getHeight() {
		return this.texture.getHeight();
	}

	public @NonNull GpuTextureView getView() {
		return this.texture.getView();
	}

	private static GpuTexture createStencil(final GpuDevice device, final String label, final int width, final int height) {
		return device.createTexture(label, GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_COPY_SRC | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT, GpuFormat.R8_UNORM, width, height, 1, 1);
	}

}