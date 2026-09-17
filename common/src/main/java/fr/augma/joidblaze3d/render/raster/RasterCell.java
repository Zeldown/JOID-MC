package fr.augma.joidblaze3d.render.raster;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.textures.GpuTextureView;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class RasterCell {

	private final GpuTextureView view;
	private final float          u0;
	private final float          v0;
	private final float          u1;
	private final float          v1;

	public static @NonNull RasterCell create(final @NonNull GpuTextureView view, final float u0, final float v0, final float u1, final float v1) {
		return new RasterCell(view, u0, v0, u1, v1);
	}

}