package dev.joid.impl.minecraft.render.pass;

import java.util.Optional;
import java.util.OptionalDouble;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuTextureView;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PassEncoder {

	private final GpuDevice device;

	private RenderPass     pass;
	private GpuTextureView color;
	private GpuTextureView depth;

	public static @NonNull PassEncoder create(final @NonNull GpuDevice device) {
		return new PassEncoder(device);
	}

	public @NonNull RenderPass begin(final @NonNull GpuTextureView color, final GpuTextureView depth) {
		if (this.pass != null && this.color == color && this.depth == depth) {
			return this.pass;
		}

		this.end();
		this.pass  = this.device.createCommandEncoder().createRenderPass(() -> "JOID", color, Optional.empty(), depth, OptionalDouble.empty());
		this.color = color;
		this.depth = depth;
		return this.pass;
	}

	public void end() {
		if (this.pass == null) {
			return;
		}

		this.pass.close();
		this.pass  = null;
		this.color = null;
		this.depth = null;
	}

	public @NonNull CommandEncoder encoder() {
		this.end();
		return this.device.createCommandEncoder();
	}

}