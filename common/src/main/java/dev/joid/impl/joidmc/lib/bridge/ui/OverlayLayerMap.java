package dev.joid.impl.joidmc.lib.bridge.ui;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import dev.joid.impl.joidmc.lib.ui.core.data.overlay.render.ElementType;
import lombok.Getter;
import lombok.NonNull;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public final class OverlayLayerMap {

	@Getter private final Map<ElementType, List<Identifier>> layerMap;

	private OverlayLayerMap() {
		this.layerMap = new EnumMap<>(ElementType.class);
	}

	public static @NonNull OverlayLayerMap create() {
		return new OverlayLayerMap();
	}

	public @NonNull OverlayLayerMap map(final @NonNull ElementType type, final @NonNull Identifier... layers) {
		this.layerMap.put(type, List.of(layers));
		return this;
	}

	public void extractPre(final @NonNull GuiGraphicsExtractor graphics, final @NonNull Identifier layer, final boolean cancelled) {
		for (final Map.Entry<ElementType, List<Identifier>> entry : this.layerMap.entrySet()) {
			if (entry.getValue().getFirst().equals(layer)) {
				OverlayBridge.inst().extract(graphics, entry.getKey(), false, cancelled);
			}
		}
	}

	public void extractPost(final @NonNull GuiGraphicsExtractor graphics, final @NonNull Identifier layer) {
		for (final Map.Entry<ElementType, List<Identifier>> entry : this.layerMap.entrySet()) {
			if (entry.getValue().getLast().equals(layer)) {
				OverlayBridge.inst().extract(graphics, entry.getKey(), true, false);
			}
		}
	}

	public boolean isCancelled(final @NonNull Identifier layer) {
		if (OverlayBridge.inst().isCancelled(ElementType.ALL)) {
			return true;
		}

		final boolean shared = this.layerMap.values().stream().filter(layerList -> layerList.contains(layer)).count() > 1L;
		boolean cancelled = false;
		for (final Map.Entry<ElementType, List<Identifier>> entry : this.layerMap.entrySet()) {
			if (!entry.getValue().contains(layer) || shared && !entry.getKey().isActive()) {
				continue;
			}

			if (!OverlayBridge.inst().isCancelled(entry.getKey())) {
				return false;
			}

			cancelled = true;
		}

		return cancelled;
	}

}