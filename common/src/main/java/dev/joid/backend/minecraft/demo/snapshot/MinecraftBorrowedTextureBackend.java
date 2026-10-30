package dev.joid.backend.minecraft.demo.snapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

import dev.joid.backend.minecraft.bridge.render.texture.MinecraftBorrowedTexture;
import dev.joid.backend.minecraft.bridge.snapshot.MinecraftSnapshotBackend;
import dev.joid.lib.bridge.render.texture.ITexture;
import dev.joid.test.contract.IBorrowedTextureBackend;
import dev.joid.test.snapshot.SnapshotImage;
import lombok.NonNull;

public final class MinecraftBorrowedTextureBackend implements IBorrowedTextureBackend {

	private final MinecraftSnapshotBackend backend  = new MinecraftSnapshotBackend();
	private final List<GpuTexture>         textures = new ArrayList<>();

	@Override
	public void create(final int width, final int height) {
		this.backend.create(width, height);
	}

	@Override
	public void destroy() {
		for (final GpuTexture texture : this.textures) {
			texture.close();
		}
		this.textures.clear();
		this.backend.destroy();
	}

	@Override
	public void present() {
		this.backend.present();
	}

	@Override
	public @NonNull SnapshotImage capture(final int width, final int height) {
		return this.backend.capture(width, height);
	}

	@Override
	public @NonNull String getRenderer() {
		return this.backend.getRenderer();
	}

	@Override
	public @NonNull ITexture borrow(final @NonNull Supplier<Object> texture) {
		return MinecraftBorrowedTexture.of(() -> (GpuTextureView) texture.get());
	}

	@Override
	public boolean isBorrowableTexture(final @NonNull Object texture) {
		return !((GpuTextureView) texture).isClosed();
	}

	@Override
	public @NonNull Object createBorrowableTexture(final int width, final int height, final int color, final boolean mipmapped) {
		final int levels = mipmapped ? 32 - Integer.numberOfLeadingZeros(Math.max(width, height)) : 1;
		final GpuTexture texture = RenderSystem.getDevice().createTexture("JOID Borrowable Texture", GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_TEXTURE_BINDING, GpuFormat.RGBA8_UNORM, width, height, 1, levels);
		this.textures.add(texture);
		for (int level = 0; level < levels; level++) {
			final int levelWidth = Math.max(1, width >> level);
			final int levelHeight = Math.max(1, height >> level);
			try (final NativeImage image = new NativeImage(levelWidth, levelHeight, false)) {
				image.fillRect(0, 0, levelWidth, levelHeight, color);
				RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, image, level, 0, 0, 0);
			}
		}
		return RenderSystem.getDevice().createTextureView(texture);
	}

	@Override
	public @NonNull Map<@NonNull String, @NonNull String> readBorrowableParameters(final @NonNull Object texture) {
		final GpuTextureView view = (GpuTextureView) texture;
		final Map<String, String> parameters = new TreeMap<>();
		parameters.put("FORMAT", view.texture().getFormat().toString());
		parameters.put("MIP_LEVELS", Integer.toString(view.mipLevels()));
		parameters.put("SIZE", view.getWidth(0) + "x" + view.getHeight(0));
		return parameters;
	}

}