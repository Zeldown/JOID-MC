package fr.augma.joidblaze3d.screen.node;

import java.util.function.Supplier;

import be.zeldown.joid.lib.ui.node.Node;
import fr.augma.joidblaze3d.draw.MCDrawUtils;
import lombok.Getter;
import lombok.NonNull;

import net.minecraft.world.entity.Entity;

@Getter
@SuppressWarnings("unchecked")
public class EntityNode extends Node {

	private static final double REFERENCE = 0.8D;
	private static final double AMPLITUDE = 20D;

	private Supplier<Entity> entity;

	private double size;
	private double xOffset;
	private double yOffset;

	private double rotationYaw;
	private double rotationPitch;

	private double  outset;
	private boolean followMouse;
	private boolean stencil;
	private float   yaw;
	private float   pitch;

	protected EntityNode(final double x, final double y, final double width, final double height) {
		super(x, y, width, height);

		this.size        = 1D;
		this.outset      = 0.25D;
		this.followMouse = false;
		this.stencil     = true;
	}

	public static @NonNull EntityNode create(final double x, final double y, final double size) {
		return new EntityNode(x, y, size, size);
	}

	public static @NonNull EntityNode create(final double x, final double y, final double width, final double height) {
		return new EntityNode(x, y, width, height);
	}

	@Override
	public void draw(final double mouseX, final double mouseY) {
		final Entity entity = this.getEntityInstance();
		if (entity == null || this.size == 0D) {
			return;
		}

		double scale = super.dw(entity.getBbWidth());
		if (entity.getBbHeight() * scale > super.getHeight()) {
			scale = super.dh(entity.getBbHeight());
		}

		scale *= this.size;

		float yaw = this.yaw;
		float pitch = this.pitch;
		if (this.followMouse && super.getUi().isOnTop()) {
			yaw   = (float) (Math.atan((mouseX - super.getAbsoluteX() - super.getWidth() / 2D) / (super.getWidth() * EntityNode.REFERENCE)) * -EntityNode.AMPLITUDE);
			pitch = (float) (Math.atan((mouseY - super.getAbsoluteY() - super.getHeight() / 2D) / (super.getHeight() * EntityNode.REFERENCE)) * EntityNode.AMPLITUDE);
		}

		MCDrawUtils.ENTITY.drawEntity(entity, super.getX() + this.xOffset, super.getY() + this.yOffset, super.getWidth(), super.getHeight(), scale, yaw, pitch, (float) this.rotationYaw, (float) this.rotationPitch, this.outset, this.stencil);
	}

	public final Entity getEntityInstance() {
		return this.entity == null ? null : this.entity.get();
	}

	public final <T extends EntityNode> @NonNull T entity(final Entity entity) {
		return this.entity(() -> entity);
	}

	public final <T extends EntityNode> @NonNull T entity(final Supplier<Entity> entity) {
		this.entity = entity;
		return (T) this;
	}

	public <T extends EntityNode> @NonNull T size(final double size) {
		this.size = size;
		return (T) this;
	}

	public final <T extends EntityNode> @NonNull T xOffset(final double xOffset) {
		this.xOffset = xOffset;
		return (T) this;
	}

	public final <T extends EntityNode> @NonNull T yOffset(final double yOffset) {
		this.yOffset = yOffset;
		return (T) this;
	}

	public <T extends EntityNode> @NonNull T rotationYaw(final double rotationYaw) {
		this.rotationYaw = rotationYaw;
		return (T) this;
	}

	public <T extends EntityNode> @NonNull T rotationPitch(final double rotationPitch) {
		this.rotationPitch = rotationPitch;
		return (T) this;
	}

	public final <T extends EntityNode> @NonNull T outset(final double outset) {
		this.outset = outset;
		return (T) this;
	}

	public final <T extends EntityNode> @NonNull T stencil(final boolean stencil) {
		this.stencil = stencil;
		return (T) this;
	}

	public final <T extends EntityNode> @NonNull T followMouse(final boolean followMouse) {
		this.followMouse = followMouse;
		return (T) this;
	}

	public final <T extends EntityNode> @NonNull T yaw(final float yaw) {
		this.yaw = yaw;
		return (T) this;
	}

	public final <T extends EntityNode> @NonNull T pitch(final float pitch) {
		this.pitch = pitch;
		return (T) this;
	}

}