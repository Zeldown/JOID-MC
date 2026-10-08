package dev.joid.impl.joidmc.lib.ui.node.impl.design.entity;

import dev.joid.lib.utils.click.ClickType;
import dev.joid.lib.utils.context.InternalContext;
import lombok.Getter;
import lombok.NonNull;

@Getter
@SuppressWarnings("unchecked")
public class EntityViewerNode extends EntityNode {

	private static final double DRAG  = 5D;
	private static final double WHEEL = 3000D;
	private static final double SPEED = 0.1D;

	private boolean dragged;
	private double  draggedMouseX;
	private double  draggedMouseY;

	private double targetSize;
	private double targetRotationYaw;
	private double targetRotationPitch;

	private double minSize;
	private double maxSize;

	private double minRotationYaw;
	private double maxRotationYaw;

	private double minRotationPitch;
	private double maxRotationPitch;

	protected EntityViewerNode(final double x, final double y, final double width, final double height) {
		super(x, y, width, height);

		this.targetSize          = super.getSize();
		this.targetRotationYaw   = super.getRotationYaw();
		this.targetRotationPitch = super.getRotationPitch();

		this.minSize = 0.1D;
		this.maxSize = 2D;

		this.minRotationYaw = -Double.MAX_VALUE;
		this.maxRotationYaw = Double.MAX_VALUE;

		this.minRotationPitch = -Double.MAX_VALUE;
		this.maxRotationPitch = Double.MAX_VALUE;
	}

	public static @NonNull EntityViewerNode create(final double x, final double y, final double size) {
		return new EntityViewerNode(x, y, size, size);
	}

	public static @NonNull EntityViewerNode create(final double x, final double y, final double width, final double height) {
		return new EntityViewerNode(x, y, width, height);
	}

	@Override
	public void draw(final double mouseX, final double mouseY) {
		if (super.getEntityInstance() == null) {
			return;
		}

		if (this.dragged) {
			this.targetRotationYaw   += (mouseX - this.draggedMouseX) / EntityViewerNode.DRAG;
			this.targetRotationPitch -= (mouseY - this.draggedMouseY) / EntityViewerNode.DRAG;
			this.draggedMouseX        = mouseX;
			this.draggedMouseY        = mouseY;

			this.targetRotationYaw   = Math.max(this.minRotationYaw, Math.min(this.maxRotationYaw, this.targetRotationYaw));
			this.targetRotationPitch = Math.max(this.minRotationPitch, Math.min(this.maxRotationPitch, this.targetRotationPitch));
		}

		if (super.getRotationYaw() != this.targetRotationYaw) {
			super.rotationYaw(super.getUi().lerpByFramerate(super.getRotationYaw(), this.targetRotationYaw, EntityViewerNode.SPEED, EntityViewerNode.SPEED, true));
		}

		if (super.getRotationPitch() != this.targetRotationPitch) {
			super.rotationPitch(super.getUi().lerpByFramerate(super.getRotationPitch(), this.targetRotationPitch, EntityViewerNode.SPEED, EntityViewerNode.SPEED, true));
		}

		if (super.getSize() != this.targetSize) {
			super.size(super.getUi().lerpByFramerate(super.getSize(), this.targetSize, EntityViewerNode.SPEED, 0D, true));
		}

		super.draw(mouseX, mouseY);
	}

	@Override
	public void mouseScroll(final double mouseX, final double mouseY, final int value, final @NonNull InternalContext context) {
		if (!super.isHovered(mouseX, mouseY)) {
			return;
		}

		context.cancel(() -> this.zoom(this.targetSize + value / EntityViewerNode.WHEEL));
	}

	@Override
	public void mousePressed(final double mouseX, final double mouseY, final @NonNull ClickType click, final @NonNull InternalContext context) {
		if (!super.isHovered(mouseX, mouseY)) {
			return;
		}

		context.cancel(() -> {
			this.dragged       = true;
			this.draggedMouseX = mouseX;
			this.draggedMouseY = mouseY;
		});
	}

	@Override
	public void mouseReleased(final double mouseX, final double mouseY, final @NonNull ClickType click, final @NonNull InternalContext context) {
		this.dragged = false;
	}

	@Override
	public <T extends EntityNode> @NonNull T size(final double size) {
		this.targetSize = size;
		return super.size(size);
	}

	@Override
	public <T extends EntityNode> @NonNull T rotationYaw(final double rotationYaw) {
		this.targetRotationYaw = rotationYaw;
		return super.rotationYaw(rotationYaw);
	}

	@Override
	public <T extends EntityNode> @NonNull T rotationPitch(final double rotationPitch) {
		this.targetRotationPitch = rotationPitch;
		return super.rotationPitch(rotationPitch);
	}

	public final <T extends EntityViewerNode> @NonNull T zoom(final double zoom) {
		this.targetSize = Math.max(this.minSize, Math.min(this.maxSize, zoom));
		return (T) this;
	}

	public final <T extends EntityViewerNode> @NonNull T sizeRange(final double minimum, final double maximum) {
		this.minSize = minimum;
		this.maxSize = maximum;
		return (T) this;
	}

	public final <T extends EntityViewerNode> @NonNull T rotationYawRange(final double minimum, final double maximum) {
		this.minRotationYaw = minimum;
		this.maxRotationYaw = maximum;
		return (T) this;
	}

	public final <T extends EntityViewerNode> @NonNull T rotationPitchRange(final double minimum, final double maximum) {
		this.minRotationPitch = minimum;
		this.maxRotationPitch = maximum;
		return (T) this;
	}

}