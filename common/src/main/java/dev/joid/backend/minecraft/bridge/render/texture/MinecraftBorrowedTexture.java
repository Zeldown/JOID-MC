package dev.joid.backend.minecraft.bridge.render.texture;

import java.util.function.Supplier;

import com.mojang.renderpearl.api.textures.GpuTextureView;

import dev.joid.lib.bridge.render.texture.BorrowedTexture;
import lombok.NonNull;

public final class MinecraftBorrowedTexture extends BorrowedTexture<GpuTextureView> implements IMinecraftTexture {

	private MinecraftBorrowedTexture(final Supplier<GpuTextureView> view) {
		super(view);
	}

	public static @NonNull MinecraftBorrowedTexture of(final @NonNull GpuTextureView view) {
		return new MinecraftBorrowedTexture(() -> view);
	}

	public static @NonNull MinecraftBorrowedTexture of(final @NonNull Supplier<GpuTextureView> view) {
		return new MinecraftBorrowedTexture(view);
	}

	@Override
	public GpuTextureView getView() {
		return super.getHandle();
	}

	@Override
	protected int getWidth(final @NonNull GpuTextureView view) {
		return view.getWidth(0);
	}

	@Override
	protected int getHeight(final @NonNull GpuTextureView view) {
		return view.getHeight(0);
	}

	@Override
	protected boolean isValid(final @NonNull GpuTextureView view) {
		return !view.isClosed();
	}

	@Override
	protected boolean isMipmapped(final @NonNull GpuTextureView view) {
		return view.mipLevels() > 1;
	}

}