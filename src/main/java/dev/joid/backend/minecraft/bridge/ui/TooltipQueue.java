package dev.joid.backend.minecraft.bridge.ui;

import java.util.function.Consumer;

public final class TooltipQueue {

	private Object content;

	public void push(final Object content) {
		this.content = content;
	}

	public void flush(final Consumer<Object> drawer) {
		final Object content = this.content;
		this.content = null;
		if (content != null) {
			drawer.accept(content);
		}
	}

}