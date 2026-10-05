package dev.joid.impl.joidmc.lib.ui.core.data.overlay;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dev.joid.impl.joidmc.lib.ui.core.data.overlay.interaction.UIMCOverlayInteraction;
import dev.joid.impl.joidmc.lib.ui.core.data.overlay.render.UIMCOverlayRender;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UIMCOverlay {

	public UIMCOverlayInteraction interaction() default @UIMCOverlayInteraction;

	public UIMCOverlayRender render() default @UIMCOverlayRender;

}