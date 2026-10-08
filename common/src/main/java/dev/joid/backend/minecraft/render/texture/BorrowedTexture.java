package dev.joid.backend.minecraft.render.texture;

import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.mojang.blaze3d.textures.GpuTextureView;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BorrowedTexture implements IGpuTexture {

	private final Supplier<GpuTextureView> view;

	public static @NonNull BorrowedTexture of(final @NonNull GpuTextureView view) {
		return new BorrowedTexture(() -> view);
	}

	public static @NonNull BorrowedTexture of(final @NonNull Supplier<@NonNull GpuTextureView> view) {
		return new BorrowedTexture(view);
	}

	@Override
	public @NonNull BorrowedTexture mipmap(final boolean mipmap) {
		return this;
	}

	@Override
	public @NonNull BorrowedTexture allocate(final int width, final int height) {
		throw new UnsupportedOperationException("A borrowed Minecraft texture is read-only");
	}

	@Override
	public @NonNull BorrowedTexture upload(final @NonNull int[] pixels, final int width, final int height) {
		throw new UnsupportedOperationException("A borrowed Minecraft texture is read-only");
	}

	@Override
	public @NonNull GpuTextureView getView() {
		return this.view.get();
	}

	@Override
	public int getWidth() {
		return this.getView().getWidth(0);
	}

	@Override
	public int getHeight() {
		return this.getView().getHeight(0);
	}

	@Override
	public boolean isMipmapped() {
		return this.getView().mipLevels() > 1;
	}

	@Override
	public void delete() {}

}