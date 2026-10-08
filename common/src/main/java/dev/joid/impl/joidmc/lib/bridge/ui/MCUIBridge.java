package dev.joid.impl.joidmc.lib.bridge.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.ui.UIBridge;
import dev.joid.lib.bridge.window.IWindowBridge;
import dev.joid.lib.ui.core.UI;
import dev.joid.impl.joidmc.lib.bridge.render.RenderBridge;
import dev.joid.impl.joidmc.lib.ui.core.data.UIMCData;
import lombok.NonNull;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public abstract class MCUIBridge extends UIBridge {

	private static final int    REFERENCE = 4;
	private static final double DEPTH     = -2000D;
	private static final double PIPELINE  = 10D;

	private List<String> hoverList;
	private ItemStack    hoverStack;

	@Override
	public void drawHover(final @NonNull UI ui, final @NonNull List<@NonNull String> lines, final double mouseX, final double mouseY) {
		this.hoverList = new ArrayList<>(lines);
	}

	public void drawHover(final @NonNull ItemStack stack) {
		this.hoverStack = stack;
	}

	protected final void extract(final @NonNull GuiGraphicsExtractor graphics, final @NonNull List<UI> uiList, final int mouseX, final int mouseY) {
		final Window window = Minecraft.getInstance().getWindow();
		final RenderBridge render = (RenderBridge) BridgeHandler.RENDER.get();
		render.beginFrame(window.getWidth(), window.getHeight());
		this.draw(render, uiList);
		final GpuTextureView view = render.endFrame();

		final float scale = 1F / window.getGuiScale();
		graphics.pose().pushMatrix();
		graphics.pose().scale(scale, scale);
		graphics.blit(view, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST), 0, 0, view.getWidth(0), view.getHeight(0), 0F, 1F, 1F, 0F);
		graphics.pose().popMatrix();

		final ItemStack hoverStack = this.hoverStack;
		this.hoverStack = null;
		if (hoverStack != null) {
			graphics.setTooltipForNextFrame(Minecraft.getInstance().font, hoverStack, mouseX, mouseY);
			return;
		}

		final List<String> hoverList = this.hoverList;
		this.hoverList = null;
		if (hoverList != null && !hoverList.isEmpty()) {
			graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, hoverList.stream().<Component>map(Component::literal).collect(Collectors.toList()), mouseX, mouseY);
		}
	}

	public static boolean pause(final @NonNull UI ui) {
		final UIMCData data = ui.getClass().getAnnotation(UIMCData.class);
		return data == null || data.pause();
	}

	@Override
	public double getInterfaceScale(final @NonNull UI ui) {
		final UIMCData data = ui.getClass().getAnnotation(UIMCData.class);
		if (data == null || !data.guiScale()) {
			return 1D;
		}

		final Minecraft minecraft = Minecraft.getInstance();
		final int maximum = minecraft.getWindow().calculateScale(0, minecraft.options.forceUnicodeFont().get());
		final int scale = data.guiScaleLimit() > 0 ? Math.min(minecraft.getWindow().getGuiScale(), data.guiScaleLimit()) : minecraft.getWindow().getGuiScale();
		return Math.min(1D, scale / (double) Math.min(maximum, MCUIBridge.REFERENCE));
	}

	private void draw(final RenderBridge render, final List<UI> uiList) {
		final IWindowBridge window = BridgeHandler.WINDOW.get();
		double depth = MCUIBridge.DEPTH;
		double level = 0D;
		for (final UI ui : uiList) {
			if (!ui.getData().visible()) {
				continue;
			}

			level += ui.getData().zlevel();
			depth += level;
			render.pushMatrix();
			render.translate(0D, 0D, depth);
			ui.draw(window.getMouseX(), window.getMouseY());
			render.popMatrix();
			level += ui.getRenderPipelineLevel() + MCUIBridge.PIPELINE;
		}
	}

}