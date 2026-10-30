package dev.joid.backend.minecraft.bridge.render.pass;

import java.util.Optional;
import java.util.OptionalDouble;

import com.mojang.renderpearl.api.commands.CommandEncoder;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.textures.GpuTextureView;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class PassEncoder {

	private final GpuDevice device;

	private RenderPass     pass;
	private GpuTextureView color;
	private GpuTextureView depth;

	public @NonNull RenderPass begin(final GpuTextureView color, final GpuTextureView depth) {
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