package dev.joid.backend.minecraft.lib.resource.resolver.impl;

import dev.joid.backend.minecraft.lib.resource.decoder.impl.SpriteResourceDecoder;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.resource.ResourceBuilder;
import dev.joid.lib.resource.ResourceData;
import dev.joid.lib.resource.resolver.IResourceResolver;
import lombok.NonNull;
import net.minecraft.util.IIcon;

public class SpriteResourceResolver implements IResourceResolver {

	@Override
	public boolean supports(final @NonNull Object input) {
		return input instanceof IIcon;
	}

	@Override
	public @NonNull Resource resolve(final @NonNull ResourceBuilder builder, final @NonNull Object input) {
		final IIcon icon = (IIcon) input;
		final String uniqueId = "sprite:" + icon.getIconName() + "#" + System.identityHashCode(icon);
		return builder.compute(uniqueId, () -> ResourceData.create(uniqueId, SpriteResourceDecoder.create(icon))).nearest();
	}

}