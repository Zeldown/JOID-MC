package dev.joid.backend.minecraft.showcase.ui;

import dev.joid.lib.color.Color;
import dev.joid.lib.draw.DrawUtils;
import dev.joid.lib.ui.node.impl.control.field.TextFieldNode;

public class ShowFieldNode extends TextFieldNode {

	private final Color accent;

	private double last;
	private double focus;

	protected ShowFieldNode(final double x, final double y, final double width, final Color accent) {
		super(x, y, width, 68);

		this.accent = accent;
	}

	public static ShowFieldNode create(final double x, final double y, final double width, final Color accent) {
		return new ShowFieldNode(x, y, width, accent);
	}

	@Override
	public void draw(final double mouseX, final double mouseY) {
		final double now = ShowTheme.now();
		final double delta = this.last == 0D ? 10D : now - this.last;
		this.last  = now;
		this.focus = this.focus + ((super.isFocused() ? 1D : 0D) - this.focus) * (1D - Math.exp(-delta * 12D));
		final float focus = (float) this.focus;
		if (focus > 0.01F) {
			DrawUtils.SHAPE.drawShadow(super.getX(), super.getY(), super.getWidth(), super.getHeight(), this.accent.withAlpha(0.45F * focus), 18F, 22F);
		}

		DrawUtils.SHAPE.drawRoundedRect(super.getX(), super.getY(), super.getWidth(), super.getHeight(), Color.decode("#0B0E14").to(Color.decode("#141A26"), focus), 18F);
		DrawUtils.SHAPE.drawRoundedBorder(super.getX(), super.getY(), super.getWidth(), super.getHeight(), Color.WHITE.withAlpha(0.12F).to(this.accent, focus), 18F, 2D);
		super.draw(mouseX, mouseY);
	}

}