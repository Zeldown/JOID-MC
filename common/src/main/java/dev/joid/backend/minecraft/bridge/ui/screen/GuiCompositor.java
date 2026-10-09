package dev.joid.backend.minecraft.bridge.ui.screen;

import dev.joid.backend.minecraft.MinecraftBackend;
import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.render.MinecraftRenderTarget;
import dev.joid.backend.minecraft.bridge.render.MinecraftRenderTargetPool;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.render.texture.TextureFilter;
import dev.joid.lib.bridge.render.texture.TextureSampling;
import dev.joid.lib.bridge.render.texture.TextureWrap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.resources.Identifier;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class GuiCompositor {

	private static final RenderPipeline PIPELINE = RenderPipeline.builder()
			.withLocation(Identifier.fromNamespaceAndPath(MinecraftBackend.MOD_ID, "pipeline/composite"))
			.withVertexShader(Identifier.fromNamespaceAndPath(MinecraftBackend.MOD_ID, "core/composite"))
			.withFragmentShader(Identifier.fromNamespaceAndPath(MinecraftBackend.MOD_ID, "core/composite"))
			.withBindGroupLayout(BindGroupLayouts.GLOBALS)
			.withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
			.withBindGroupLayout(BindGroupLayouts.SAMPLER0)
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA))
			.withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
			.withPrimitiveTopology(PrimitiveTopology.QUADS)
			.build();

	private final MinecraftRenderTargetPool targetPool;

	public static @NonNull GuiCompositor create(final @NonNull MinecraftRenderBridge bridge) {
		return new GuiCompositor(MinecraftRenderTargetPool.create(bridge));
	}

	public void composite(final @NonNull GuiGraphicsExtractor graphics, final @NonNull Runnable draw) {
		final Window window = Minecraft.getInstance().getWindow();
		final MinecraftRenderBridge render = (MinecraftRenderBridge) BridgeHandler.RENDER.get();
		final MinecraftRenderTarget target = this.targetPool.acquire(window.getWidth(), window.getHeight());
		render.screenTarget(target).beginFrame();
		try {
			render.screen(window.getWidth(), window.getHeight());
			render.clear(0F, 0F, 0F, 0F);
			draw.run();
		} finally {
			render.endFrame();
		}

		graphics.fill(GuiCompositor.PIPELINE, TextureSetup.singleTexture(target.getView(), MinecraftRenderBridge.getSampler(TextureSampling.of(TextureFilter.NEAREST, TextureWrap.CLAMP_TO_EDGE, false))), 0, 0, graphics.guiWidth(), graphics.guiHeight());
	}

}