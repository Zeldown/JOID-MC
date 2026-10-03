package fr.augma.joidblaze3d.test;

import be.zeldown.joid.lib.color.Color;
import be.zeldown.joid.lib.resource.Resource;
import be.zeldown.joid.lib.ui.core.UI;
import be.zeldown.joid.lib.ui.core.data.debug.UIDataDebug;
import be.zeldown.joid.lib.ui.node.Node;
import be.zeldown.joid.lib.ui.node.impl.design.shape.RectNode;
import be.zeldown.joid.lib.ui.node.impl.structure.flex.FlexNode;
import be.zeldown.joid.lib.ui.node.impl.structure.grid.GridNode;
import be.zeldown.joid.lib.ui.node.property.overflow.OverflowProperty;
import be.zeldown.joid.lib.utils.align.Align;
import lombok.Getter;
import fr.augma.joidblaze3d.screen.data.UIMCData;
import fr.augma.joidblaze3d.screen.node.EntityNode;
import fr.augma.joidblaze3d.screen.node.SlotNode;

import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.InventoryMenu;

@UIMCData
@UIDataDebug(hotreload = true, profiler = true)
public final class TestMenuUI extends UI {

	private static final double CELL   = 64D;
	private static final double MARGIN = 8D;

	@Getter private Node panel;

	private final InventoryMenu menu;

	public TestMenuUI(final InventoryMenu menu) {
		this.menu = menu;
	}

	@Override
	public void init() {
		this.panel = RectNode
		.create(960 - 704D / 2D, 540 - 664D / 2D, 704, 664)
		.color(new Color(18, 18, 24, 200))
		.border(Color.CYAN, 4D)
		.anchor(Align.CENTER)
		.body(panel -> {
			FlexNode
			.vertical(32, 32, TestMenuUI.CELL)
			.margin(TestMenuUI.MARGIN)
			.align(Align.CENTER)
			.body(flex -> {
				for (int index = 0; index < 4; index++) {
					this.slot(5 + index).attach(flex);
				}
			})
			.attach(panel);

			this.slot(45).x(120D + 200D).y(248).attach(panel);

			RectNode
			.create(120, 32, 180, 280)
			.color(Color.BLACK.copyAlpha(0.6F))
			.overflow(OverflowProperty.HIDDEN)
			.body(entityContainer -> {
				final double width = 120D;
				final double height = 220D;
				EntityNode
				.create(entityContainer.dw(2D) -  width / 2D, entityContainer.dh(2D) - height / 2D, width, height)
				.entity(() -> Minecraft.getInstance().player)
				.followMouse(true)
				.attach(entityContainer);
			})
			.attach(panel);

			GridNode
			.create(456, 104, (TestMenuUI.CELL + TestMenuUI.MARGIN) * 2D, (TestMenuUI.CELL + TestMenuUI.MARGIN) * 2D)
			.margin(TestMenuUI.MARGIN)
			.body(grid -> {
				for (int index = 0; index < 4; index++) {
					this.slot(1 + index).attach(grid);
				}
			})
			.attach(panel);

			this.slot(0).x(608).y(140).attach(panel);

			GridNode
			.create(32, 344, (TestMenuUI.CELL + TestMenuUI.MARGIN) * 9D, (TestMenuUI.CELL + TestMenuUI.MARGIN) * 3D)
			.margin(TestMenuUI.MARGIN)
			.body(grid -> {
				for (int index = 0; index < 27; index++) {
					this.slot(9 + index).attach(grid);
				}
			})
			.attach(panel);

			FlexNode
			.horizontal(panel.dw(2D), 568, TestMenuUI.CELL)
			.margin(TestMenuUI.MARGIN)
			.align(Align.CENTER)
			.body(flex -> {
				for (int index = 0; index < 9; index++) {
					this.slot(36 + index).attach(flex);
				}
			})
			.anchorX(Align.CENTER)
			.attach(panel);
		})
		.attach(this);
	}

	private Node slot(final int index) {
		return SlotNode
		.create(this.menu.getSlot(index), 0, 0, TestMenuUI.CELL)
		.placeholder(Resource.of("https://placehold.co/40x40.png"))
		.body(slot -> {
			RectNode
			.create(0, 0, TestMenuUI.CELL, TestMenuUI.CELL)
			.color(Color.WHITE.copyAlpha(0.08F))
			.border(Color.CYAN.copyAlpha(0.3F), 2D)
			.attach(slot);
		});
	}

}