package dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UIDataOverlayLayer {

	public OverlayLayer layer();
	public boolean      post()   default false;
	public boolean      cancel() default false;

}