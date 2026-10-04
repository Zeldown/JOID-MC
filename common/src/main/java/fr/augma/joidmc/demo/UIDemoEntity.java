package fr.augma.joidmc.demo;

import be.zeldown.joid.demo.ui.UIDemo;
import be.zeldown.joid.lib.ui.node.impl.structure.flex.FlexNode;
import be.zeldown.joid.lib.utils.align.Align;
import fr.augma.joidmc.screen.node.EntityViewerNode;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public final class UIDemoEntity extends UIDemo {

	@Override
	public void init() {
		FlexNode
		.horizontal(960D, 540 - 250, 500)
		.margin(10D)
		.body(flex -> {
			final WitherBoss wither = new WitherBoss(EntityTypes.WITHER, Minecraft.getInstance().level);
			wither.setId(-1);
			EntityViewerNode.create(0D, 0D, 400D, 500D).entity(() -> Minecraft.getInstance().player).attach(flex);
			EntityViewerNode.create(0D, 0D, 400D, 500D).entity(wither).attach(flex);
		})
		.anchor(Align.CENTER)
		.attach(this);
	}

}