package dev.joid.impl.minecraft.snapshot;

import dev.joid.impl.minecraft.render.RenderBridge;
import dev.joid.impl.minecraft.render.RenderTarget;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.test.snapshot.ISnapshotBackend;
import dev.joid.test.snapshot.SnapshotImage;
import lombok.NonNull;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;

public final class SnapshotBackend implements ISnapshotBackend {

	private RenderTarget target;

	@Override
	public void destroy() {
		this.target.delete();
		this.target = null;
	}

	@Override
	public void create(final int width, final int height) {
		this.target = RenderTarget.create((RenderBridge) BridgeHandler.RENDER.get(), width, height, true);
	}

	@Override
	public void present() {
		RenderSystem.getDevice().createCommandEncoder().submit();
	}

	@Override
	public void frame(final @NonNull Runnable draw) {
		final RenderBridge render = (RenderBridge) BridgeHandler.RENDER.get();
		render.beginFrame(this.target);
		try {
			draw.run();
		} finally {
			render.endFrame();
		}
	}

	@Override
	public @NonNull SnapshotImage capture(final int width, final int height) {
		final GpuBuffer buffer = RenderSystem.getDevice().createBuffer(() -> "JOID Snapshot", GpuBuffer.USAGE_MAP_READ | GpuBuffer.USAGE_COPY_DST, (long) width * height * 4L);
		try {
			final CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
			encoder.copyTextureToBuffer(this.target.getTexture().getTexture(), buffer, 0L, () -> {}, 0, 0, 0, width, height);
			try (GpuFence fence = encoder.createFence()) {
				encoder.submit();
				fence.awaitCompletion(Long.MAX_VALUE);
			}

			try (GpuBufferSlice.MappedView view = buffer.map(true, false)) {
				return SnapshotImage.fromBytes(view.data(), width, height, true, false);
			}
		} finally {
			buffer.close();
		}
	}

	@Override
	public @NonNull String getRenderer() {
		return RenderSystem.getDevice().getDeviceInfo().name();
	}

}