package fr.augma.joidmc.screen.data.overlay.interaction;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface UIMCOverlayInteraction {

	public boolean active() default false;

	public boolean cancelClick() default true;

	public boolean cancelKeyboard() default true;

	public boolean cancelScroll() default true;

}