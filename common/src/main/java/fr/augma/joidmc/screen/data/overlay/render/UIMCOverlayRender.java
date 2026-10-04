package fr.augma.joidmc.screen.data.overlay.render;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface UIMCOverlayRender {

	public ElementType type() default ElementType.ALL;

	public boolean post() default false;

	public boolean cancel() default false;

	public ElementType[] hide() default {};

	public boolean always() default false;

	public boolean gui() default false;

	public int zindex() default 0;

}