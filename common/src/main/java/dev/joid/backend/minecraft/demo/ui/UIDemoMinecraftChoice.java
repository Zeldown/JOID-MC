package dev.joid.backend.minecraft.demo.ui;

import java.util.Arrays;
import java.util.List;

import dev.joid.backend.minecraft.demo.network.OpenDemoContainerPayload;
import dev.joid.demo.DemoFont;
import dev.joid.demo.ui.DemoEntry;
import dev.joid.demo.ui.UIDemoChoice;
import dev.joid.demo.ui.core.transition.DemoPushTransition;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.Text;
import dev.joid.lib.font.TextInfo;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.UIData;
import dev.joid.lib.ui.node.impl.layout.GridNode;
import dev.joid.lib.ui.node.impl.shape.RectNode;
import dev.joid.lib.ui.node.property.OverflowProperty;

@UIData(background = true)
public class UIDemoMinecraftChoice extends UI {

	private static final Color INK     = new Color(153, 153, 153).lock();
	private static final Color ACTIVE  = new Color(102, 102, 102).lock();
	private static final Color HOVERED = new Color(128, 128, 128).lock();

	private static final List<DemoEntry> LIST = Arrays.asList(
		DemoEntry.create(UIDemoChoice.class),
		DemoEntry.create(UIDemoMinecraft.class),
		DemoEntry.create("UIDemoOverlayLayer", UIDemoOverlayLayer::toggle).state(UIDemoOverlayLayer::isOpen).hover(UIDemoOverlayLayer.class.getName()),
		DemoEntry.create("UIDemoContainer", OpenDemoContainerPayload.INSTANCE::sendToServer).hover(UIDemoContainer.class.getName())
	);

	@Override
	public void init() {
		final TextInfo label = TextInfo.create(DemoFont.MONTSERRAT, 26).color(Color.WHITE);

		super.transition(DemoPushTransition.create());

		GridNode
		.create(90, 40, 1740, 1000)
		.margin(20D)
		.overflow(OverflowProperty.SCROLL)
		.body(grid -> {
			for (final DemoEntry entry : UIDemoMinecraftChoice.LIST) {
				RectNode
				.create(0, 0, 420, 70)
				.color(() -> entry.isActive() ? UIDemoMinecraftChoice.ACTIVE : UIDemoMinecraftChoice.INK)
				.hoveredColor(UIDemoMinecraftChoice.HOVERED)
				.label(Text.create(entry::getText, label))
				.onClick(_ -> entry.getAction().run())
				.hover(entry::getHover)
				.attach(grid);
			}
		})
		.attach(this);
	}

}