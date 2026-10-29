package dev.joid.backend.minecraft.bridge.ui.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.joid.backend.minecraft.bridge.ui.screen.BaseScreenUIBridge;
import dev.joid.backend.minecraft.lib.ui.core.container.BaseContainerUI;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.UIDataOverlay;
import dev.joid.lib.ui.core.data.UIDataOverlayObject;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.utils.annotation.AnnotationValues;
import lombok.NonNull;

public abstract class BaseContainerUIBridge<G> extends BaseScreenUIBridge<G> {

	@Override
	public boolean canHandle(final @NonNull UI ui) {
		return !ui.getOverlay().active() && (ui instanceof BaseContainerUI || this.hasContainerScreen());
	}

	@Override
	public boolean canHandle(final @NonNull Class<? extends UI> clazz) {
		return !AnnotationValues.read(clazz.getAnnotation(UIDataOverlay.class), UIDataOverlayObject.create()).active() && (BaseContainerUI.class.isAssignableFrom(clazz) || this.hasContainerScreen());
	}

	@Override
	public boolean canReplace(final @NonNull UI ui) {
		return !(ui instanceof BaseContainerUI);
	}

	public final <N extends Node> N getNodeAt(final UI container, final double x, final double y, final Class<N> type) {
		for (final Node node : this.getNodeList(container, x, y)) {
			if (type.isInstance(node)) {
				return type.cast(node);
			}
		}
		return null;
	}

	protected abstract boolean hasContainerScreen();

	private List<Node> getNodeList(final UI container, final double x, final double y) {
		final List<UI> uiList = new ArrayList<>(super.getUiList());
		for (int index = uiList.size() - 1; index >= 0; index--) {
			final UI ui = uiList.get(index);
			if (ui != container && (!ui.getData().active() || !ui.getData().visible())) {
				continue;
			}

			final double uiX = ui.getView().toUiX(x);
			final double uiY = ui.getView().toUiY(y);
			if (ui == container) {
				return ui.getNodeListAt(uiX, uiY);
			}

			if (ui.getNodeAt(uiX, uiY) != null || ui.getPopup().active()) {
				return Collections.emptyList();
			}
		}
		return Collections.emptyList();
	}

}