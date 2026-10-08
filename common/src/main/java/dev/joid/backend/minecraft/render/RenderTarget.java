package dev.joid.backend.minecraft.render;

import org.joml.Vector4f;

import dev.joid.backend.minecraft.render.texture.Texture;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class RenderTarget {

	private final Texture        texture;
	private final GpuTexture     depth;
	private final GpuTextureView depthView;
	private final GpuTexture     stencil;
	private final GpuTextureView stencilView;
	private final GpuTexture     stencilCopy;
	private final GpuTextureView stencilCopyView;

	public static @NonNull RenderTarget create(final @NonNull RenderBridge bridge, final int width, final int height, final boolean stencil) {
		final GpuDevice device = bridge.getDevice();
		final Texture texture = Texture.create(bridge);
		final GpuTexture depth = device.createTexture("JOID Depth", GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_RENDER_ATTACHMENT,GpuFormat.D32_FLOAT, width, height, 1, 1);
		final GpuTexture stencilTexture = stencil ? RenderTarget.createStencil(device, "JOID Stencil", width, height) : null;
		final GpuTexture stencilCopy = stencil ? RenderTarget.createStencil(device, "JOID Stencil Copy", width, height) : null;
		texture.allocate(width, height);

		final CommandEncoder encoder = bridge.getPassEncoder().encoder();
		encoder.clearColorTexture(texture.getTexture(), new Vector4f(0F));
		encoder.clearDepthTexture(depth, 1D);
		return new RenderTarget(texture, depth, device.createTextureView(depth), stencilTexture, stencil ? device.createTextureView(stencilTexture) : null, stencilCopy, stencil ? device.createTextureView(stencilCopy) : null);
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

	public void delete() {
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

	private static GpuTexture createStencil(final GpuDevice device, final String label, final int width, final int height) {
		return device.createTexture(label, GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_COPY_SRC | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT, GpuFormat.R8_UNORM, width, height, 1, 1);
	}

}