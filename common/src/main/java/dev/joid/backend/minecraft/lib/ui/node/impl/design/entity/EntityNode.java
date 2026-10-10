package dev.joid.backend.minecraft.lib.ui.node.impl.design.entity;

import java.util.function.Supplier;

import dev.joid.backend.minecraft.lib.draw.entity.DrawEntity;
import dev.joid.lib.signal.Signal;
import dev.joid.lib.ui.node.Node;
import lombok.Getter;
import lombok.NonNull;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntitySpawnRequest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;

@Getter
public class EntityNode extends Node {

	private Entity            entity;
	private PlayerSkin        skin;
	private EntityType<?>     type;
	private ResolvableProfile profile;

	private double scale;
	private double rotationYaw;
	private double rotationPitch;

	private boolean followMouse;

	protected EntityNode(final double x, final double y, final double width, final double height) {
		super(x, y, width, height);

		this.scale = 1D;
	}

	public static @NonNull EntityNode create(final double x, final double y, final double width, final double height) {
		return new EntityNode(x, y, width, height);
	}

	@Override
	public void draw(final double mouseX, final double mouseY) {
		final Minecraft minecraft = Minecraft.getInstance();
		if (this.type != null && (this.entity == null || this.entity.level() != minecraft.level)) {
			this.entity = minecraft.level == null ? null : this.type.create(minecraft.level, new EntitySpawnRequest(EntitySpawnReason.LOAD, true));
			if (this.entity != null) {
				this.entity.setId(-1);
			}
		}

		final boolean follow = this.followMouse && super.getUi().isOnTop();
		final double lookX = follow ? super.toDrawX(mouseX) : super.getX() + super.getWidth() / 2D;
		final double lookY = follow ? super.toDrawY(mouseY) : super.getY() + super.getHeight() / 2D;
		if (this.entity != null) {
			DrawEntity.inst().drawEntity(super.getX(), super.getY(), super.getWidth(), super.getHeight(), this.entity, this.scale, this.rotationYaw, this.rotationPitch, lookX, lookY);
		} else if (this.profile != null || this.skin != null) {
			final PlayerSkin skin = this.skin != null ? this.skin : minecraft.playerSkinRenderCache().getOrDefault(this.profile).playerSkin();
			DrawEntity.inst().drawPlayer(super.getX(), super.getY(), super.getWidth(), super.getHeight(), skin, this.scale, this.rotationYaw, this.rotationPitch, lookX, lookY);
		}
	}

	public final <T extends EntityNode> @NonNull T entity(final @NonNull Entity entity) {
		return this.entity(Signal.from(entity));
	}

	public final <T extends EntityNode> @NonNull T entity(final @NonNull Supplier<Entity> entity) {
		return super.follow("source", entity, value -> this.source(value, null, null, null));
	}

	public final <T extends EntityNode> @NonNull T skin(final @NonNull PlayerSkin skin) {
		return this.skin(Signal.from(skin));
	}

	public final <T extends EntityNode> @NonNull T skin(final @NonNull Supplier<@NonNull PlayerSkin> skin) {
		return super.follow("source", skin, value -> this.source(null, value, null, null));
	}

	public final <T extends EntityNode> @NonNull T type(final @NonNull EntityType<?> type) {
		return this.type(Signal.from(type));
	}

	public final <T extends EntityNode> @NonNull T type(final @NonNull Supplier<@NonNull EntityType<?>> type) {
		return super.follow("source", type, value -> this.source(null, null, value, null));
	}

	public final <T extends EntityNode> @NonNull T profile(final @NonNull GameProfile profile) {
		return this.profile(ResolvableProfile.createResolved(profile));
	}

	public final <T extends EntityNode> @NonNull T profile(final @NonNull ResolvableProfile profile) {
		return this.profile(Signal.from(profile));
	}

	public final <T extends EntityNode> @NonNull T profile(final @NonNull Supplier<@NonNull ResolvableProfile> profile) {
		return super.follow("source", profile, value -> this.source(null, null, null, value));
	}

	public final <T extends EntityNode> @NonNull T scale(final double scale) {
		return this.scale(Signal.from(scale));
	}

	public final <T extends EntityNode> @NonNull T scale(final @NonNull Supplier<Double> scale) {
		return super.follow("scale", scale, value -> this.scale = value);
	}

	public final <T extends EntityNode> @NonNull T rotationYaw(final double rotationYaw) {
		return this.rotationYaw(Signal.from(rotationYaw));
	}

	public final <T extends EntityNode> @NonNull T rotationYaw(final @NonNull Supplier<Double> rotationYaw) {
		return super.follow("rotationYaw", rotationYaw, value -> this.rotationYaw = value);
	}

	public final <T extends EntityNode> @NonNull T rotationPitch(final double rotationPitch) {
		return this.rotationPitch(Signal.from(rotationPitch));
	}

	public final <T extends EntityNode> @NonNull T rotationPitch(final @NonNull Supplier<Double> rotationPitch) {
		return super.follow("rotationPitch", rotationPitch, value -> this.rotationPitch = value);
	}

	public final <T extends EntityNode> @NonNull T followMouse(final boolean followMouse) {
		return this.followMouse(Signal.from(followMouse));
	}

	public final <T extends EntityNode> @NonNull T followMouse(final @NonNull Supplier<Boolean> followMouse) {
		return super.follow("followMouse", followMouse, value -> this.followMouse = value);
	}

	private void source(final Entity entity, final PlayerSkin skin, final EntityType<?> type, final ResolvableProfile profile) {
		this.entity  = entity;
		this.skin    = skin;
		this.type    = type;
		this.profile = profile;
	}

}