package dev.joid.backend.minecraft.lib.ui.node.impl.design.entity;

import java.util.Map;
import java.util.function.Supplier;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import dev.joid.backend.minecraft.lib.draw.entity.DrawEntity;
import dev.joid.lib.signal.Signal;
import dev.joid.lib.ui.node.Node;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;

@Getter
public class EntityNode extends Node {

	private Entity                  entity;
	private GameProfile             profile;
	private ResourceLocation        skin;
	private Class<? extends Entity> type;

	private double scale;
	private double rotationYaw;
	private double rotationPitch;

	private boolean followMouse;

	@Getter(AccessLevel.NONE)
	private boolean sourced;

	protected EntityNode(final double x, final double y, final double width, final double height) {
		super(x, y, width, height);

		this.scale = 1D;

		super.<EntityNode>require("entity", "call entity(...), skin(...), type(...) or profile(...) before attaching it", node -> node.sourced);
	}

	public static @NonNull EntityNode create(final double x, final double y, final double width, final double height) {
		return new EntityNode(x, y, width, height);
	}

	@Override
	public void draw(final double mouseX, final double mouseY) {
		final Minecraft minecraft = Minecraft.getMinecraft();
		if (this.type != null && (this.entity == null || this.entity.worldObj != minecraft.theWorld)) {
			this.entity = minecraft.theWorld == null ? null : EntityList.createEntityByName((String) EntityList.classToStringMapping.get(this.type), minecraft.theWorld);
		}

		final boolean follow = this.followMouse && super.getUi().isOnTop();
		final double lookX = follow ? super.toDrawX(mouseX) : super.getX() + super.getWidth() / 2D;
		final double lookY = follow ? super.toDrawY(mouseY) : super.getY() + super.getHeight() / 2D;
		if (this.entity != null) {
			DrawEntity.inst().drawEntity(super.getX(), super.getY(), super.getWidth(), super.getHeight(), this.entity, this.scale, this.rotationYaw, this.rotationPitch, lookX, lookY);
		} else if (this.profile != null || this.skin != null) {
			DrawEntity.inst().drawPlayer(super.getX(), super.getY(), super.getWidth(), super.getHeight(), this.skin != null ? this.skin : EntityNode.getSkin(this.profile), this.scale, this.rotationYaw, this.rotationPitch, lookX, lookY);
		}
	}

	public final <T extends EntityNode> @NonNull T entity(final @NonNull Entity entity) {
		return this.entity(Signal.from(entity));
	}

	public final <T extends EntityNode> @NonNull T entity(final @NonNull Supplier<Entity> entity) {
		return super.follow("source", entity, value -> this.source(value, null, null, null));
	}

	public final <T extends EntityNode> @NonNull T skin(final @NonNull ResourceLocation skin) {
		return this.skin(Signal.from(skin));
	}

	public final <T extends EntityNode> @NonNull T skin(final @NonNull Supplier<@NonNull ResourceLocation> skin) {
		return super.follow("source", skin, value -> this.source(null, value, null, null));
	}

	public final <T extends EntityNode> @NonNull T type(final @NonNull Class<? extends Entity> type) {
		return this.type(Signal.from(type));
	}

	public final <T extends EntityNode> @NonNull T type(final @NonNull Supplier<@NonNull Class<? extends Entity>> type) {
		return super.follow("source", type, value -> this.source(null, null, value, null));
	}

	public final <T extends EntityNode> @NonNull T profile(final @NonNull GameProfile profile) {
		return this.profile(Signal.from(profile));
	}

	public final <T extends EntityNode> @NonNull T profile(final @NonNull Supplier<@NonNull GameProfile> profile) {
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

	private void source(final Entity entity, final ResourceLocation skin, final Class<? extends Entity> type, final GameProfile profile) {
		this.entity  = entity;
		this.skin    = skin;
		this.type    = type;
		this.profile = profile;
		this.sourced = true;
	}

	private static ResourceLocation getSkin(final GameProfile profile) {
		final SkinManager manager = Minecraft.getMinecraft().func_152342_ad();
		final Map<Type, MinecraftProfileTexture> textures = manager.func_152788_a(profile);
		return textures.containsKey(Type.SKIN) ? manager.func_152792_a(textures.get(Type.SKIN), Type.SKIN) : AbstractClientPlayer.locationStevePng;
	}

}