package dev.joid.backend.minecraft.bridge.render.texture;

import java.util.function.Supplier;

import dev.joid.lib.bridge.render.texture.BorrowedTexture;
import lombok.NonNull;

import com.mojang.blaze3d.textures.GpuTextureView;

public final class GpuBorrowedTexture extends BorrowedTexture<GpuTextureView> implements IGpuTexture {

	private GpuBorrowedTexture(final Supplier<GpuTextureView> view) {
		super(view);
	}

	public static @NonNull GpuBorrowedTexture create(final @NonNull GpuTextureView view) {
		return new GpuBorrowedTexture(() -> view);
	}

	public static @NonNull GpuBorrowedTexture create(final @NonNull Supplier<GpuTextureView> view) {
		return new GpuBorrowedTexture(view);
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