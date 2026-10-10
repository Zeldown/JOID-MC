package dev.joid.backend.minecraft.lib.ui.core.data.minecraft;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UIDataMinecraft {

	public String  title()      default "";
	public boolean pause()      default true;
	public boolean background() default true;

}