package fr.augma.joidblaze3d.test;

import be.zeldown.joid.internal.JOID;
import be.zeldown.joid.lib.color.Color;
import be.zeldown.joid.lib.ui.core.UI;
import be.zeldown.joid.lib.ui.core.data.UIData;
import be.zeldown.joid.lib.ui.node.impl.design.shape.RectNode;
import fr.augma.joidblaze3d.screen.data.UIMCData;
import fr.augma.joidblaze3d.screen.data.overlay.UIMCOverlay;
import fr.augma.joidblaze3d.screen.data.overlay.interaction.UIMCOverlayInteraction;
import fr.augma.joidblaze3d.screen.data.overlay.render.UIMCOverlayRender;
import fr.augma.joidblaze3d.screen.node.ItemNode;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

@UIMCData
@UIData(background = false)
@UIMCOverlay(interaction = @UIMCOverlayInteraction(active = true), render = @UIMCOverlayRender(post = true, gui = true))
public final class TestOverlayUI extends UI {

	@Override
	public void init() {
		RectNode
		.create(20, 20, 360, 150)
		.color(new Color(18, 18, 24, 200))
		.border(Color.CYAN, 3D)
		.body(panel -> {
			ItemNode
			.create(20, 20, 110)
			.stack(() -> Minecraft.getInstance().player == null ? ItemStack.EMPTY : Minecraft.getInstance().player.getMainHandItem())
			.attach(panel);
			RectNode
			.create(150, 45, 190, 60)
			.color(Color.DARKGRAY)
			.hoveredColor(Color.RED)
			.border(Color.WHITE, 2D)
			.onClick((node, x, y, click) -> JOID.close(this))
			.attach(panel);
		})
		.attach(this);
	}

}