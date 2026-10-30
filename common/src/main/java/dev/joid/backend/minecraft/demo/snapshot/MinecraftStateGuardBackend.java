package dev.joid.backend.minecraft.demo.snapshot;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.TreeMap;

import org.joml.Vector4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.textures.GpuTextureView;

import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.render.pass.PassEncoder;
import dev.joid.backend.minecraft.bridge.snapshot.MinecraftSnapshotBackend;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.test.contract.IStateGuardBackend;
import dev.joid.test.contract.StateTrap;
import dev.joid.test.snapshot.SnapshotImage;
import lombok.NonNull;

public final class MinecraftStateGuardBackend implements IStateGuardBackend {

	private final MinecraftSnapshotBackend backend = new MinecraftSnapshotBackend();

	@Override
	public void create(final int width, final int height) {
		this.backend.create(width, height);
	}

	@Override
	public void destroy() {
		RenderSystem.disableScissorForRenderTypeDraws();
		this.backend.destroy();
	}

	@Override
	public void present() {
		this.backend.present();
	}

	@Override
	public @NonNull SnapshotImage capture(final int width, final int height) {
		return this.backend.capture(width, height);
	}

	@Override
	public @NonNull String getRenderer() {
		return this.backend.getRenderer();
	}

	@Override
	public void drawExternal() {
		RenderSystem.enableScissorForRenderTypeDraws(2, 2, 4, 4);
		this.fill(2, 2, 4, 4, 0xFF808080);
	}

	@Override
	public void inject(final @NonNull StateTrap trap) {
		throw new IllegalArgumentException("Blaze3D has no global state for the trap " + trap + " to set: JOID records its own render passes");
	}

	@Override
	public boolean supports(final @NonNull StateTrap trap) {
		return false;
	}

	@Override
	public void fill(final int x, final int y, final int width, final int height, final int color) {
		final MinecraftRenderBridge bridge = (MinecraftRenderBridge) BridgeHandler.RENDER.get();
		final GpuTextureView output = (bridge.getRasterTarget() != null ? bridge.getRasterTarget() : bridge.getScreenTarget()).getView();
		final Vector4f clear = new Vector4f((color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F, (color >>> 24) / 255F);
		RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Host", output, Optional.of(clear), null, OptionalDouble.empty(), new RenderPass.RenderArea(x, y, width, height)).close();
	}

	@Override
	public @NonNull Map<@NonNull String, @NonNull String> readState() {
		final Map<String, String> state = new TreeMap<>();
		final ScissorState scissor = RenderSystem.getScissorStateForRenderTypeDraws();
		state.put("JOID_RENDER_PASS", Boolean.toString(MinecraftStateGuardBackend.isPassOpen()));
		state.put("RASTER_TARGET", String.valueOf(((MinecraftRenderBridge) BridgeHandler.RENDER.get()).getRasterTarget()));
		state.put("SCISSOR", scissor.enabled() ? scissor.x() + "," + scissor.y() + " " + scissor.width() + "x" + scissor.height() : "disabled");
		state.put("PROJECTION", RenderSystem.getProjectionType() + " " + RenderSystem.getProjectionMatrixBuffer());
		state.put("MODEL_VIEW", RenderSystem.getModelViewMatrixCopy().toString());
		return state;
	}

	private static boolean isPassOpen() {
		try {
			final Field pass = PassEncoder.class.getDeclaredField("pass");
			pass.setAccessible(true);
			return pass.get(((MinecraftRenderBridge) BridgeHandler.RENDER.get()).getPassEncoder()) != null;
		} catch (final ReflectiveOperationException exception) {
			throw new IllegalStateException(exception);
		}
	}

}