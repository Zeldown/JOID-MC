package dev.joid.impl.minecraft.render.texture;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.mojang.blaze3d.textures.GpuTextureView;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BorrowedTexture implements IGpuTexture {

	private final GpuTextureView view;

	public static @NonNull BorrowedTexture of(final @NonNull GpuTextureView view) {
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
	public int getWidth() {
		return this.view.getWidth(0);
	}

	@Override
	public int getHeight() {
		return this.view.getHeight(0);
	}

	@Override
	public boolean isMipmapped() {
		return this.view.mipLevels() > 1;
	}

	@Override
	public void delete() {}

}