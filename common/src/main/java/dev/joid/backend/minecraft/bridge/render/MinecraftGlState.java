package dev.joid.backend.minecraft.bridge.render;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftGlState {

	private final int[]     blend     = new int[4];
	private final int[]     depth     = new int[2];
	private final int[]     textures  = new int[2];
	private final float[]   color     = new float[4];
	private final boolean[] lights    = new boolean[2];
	private final boolean[] colorMask = new boolean[4];
	private final boolean[] texturing = new boolean[2];

	private boolean fog;
	private int     alphaFunc;
	private float   alphaRef;
	private int     shadeModel;
	private boolean blending;
	private boolean lighting;
	private boolean cullFace;
	private boolean alphaTest;
	private boolean depthTest;
	private boolean normalize;
	private int     activeTexture;
	private boolean rescaleNormal;
	private boolean colorMaterial;

	public static void preserve(final @NonNull Runnable draw) {
		final MinecraftGlState state = MinecraftGlState.read();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		try {
			draw.run();
		} finally {
			GL11.glPopAttrib();
			state.restore();
		}
	}

	public static @NonNull MinecraftGlState read() {
		final MinecraftGlState state = new MinecraftGlState();
		state.alphaTest     = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);
		state.blending      = GL11.glIsEnabled(GL11.GL_BLEND);
		state.depthTest     = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
		state.lighting      = GL11.glIsEnabled(GL11.GL_LIGHTING);
		state.cullFace      = GL11.glIsEnabled(GL11.GL_CULL_FACE);
		state.fog           = GL11.glIsEnabled(GL11.GL_FOG);
		state.normalize     = GL11.glIsEnabled(GL11.GL_NORMALIZE);
		state.rescaleNormal = GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL);
		state.colorMaterial = GL11.glIsEnabled(GL11.GL_COLOR_MATERIAL);
		state.alphaFunc     = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
		state.alphaRef      = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
		state.shadeModel    = GL11.glGetInteger(GL11.GL_SHADE_MODEL);
		state.activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
		state.lights[0]     = GL11.glIsEnabled(GL11.GL_LIGHT0);
		state.lights[1]     = GL11.glIsEnabled(GL11.GL_LIGHT1);
		state.blend[0]      = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
		state.blend[1]      = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
		state.blend[2]      = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
		state.blend[3]      = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
		state.depth[0]      = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
		state.depth[1]      = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK) ? 1 : 0;

		final FloatBuffer color = BufferUtils.createFloatBuffer(16);
		GL11.glGetFloat(GL11.GL_CURRENT_COLOR, color);
		color.get(state.color);

		final ByteBuffer mask = BufferUtils.createByteBuffer(16);
		GL11.glGetBoolean(GL11.GL_COLOR_WRITEMASK, mask);
		for (int index = 0; index < state.colorMask.length; index++) {
			state.colorMask[index] = mask.get(index) != 0;
		}

		for (int unit = 0; unit < state.textures.length; unit++) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
			state.texturing[unit] = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
			state.textures[unit]  = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		}
		GL13.glActiveTexture(state.activeTexture);
		return state;
	}

	public void restore() {
		MinecraftGlState.toggle(this.alphaTest, GlStateManager::enableAlpha, GlStateManager::disableAlpha);
		MinecraftGlState.toggle(this.blending, GlStateManager::enableBlend, GlStateManager::disableBlend);
		MinecraftGlState.toggle(this.depthTest, GlStateManager::enableDepth, GlStateManager::disableDepth);
		MinecraftGlState.toggle(this.lighting, GlStateManager::enableLighting, GlStateManager::disableLighting);
		MinecraftGlState.toggle(this.cullFace, GlStateManager::enableCull, GlStateManager::disableCull);
		MinecraftGlState.toggle(this.fog, GlStateManager::enableFog, GlStateManager::disableFog);
		MinecraftGlState.toggle(this.normalize, GlStateManager::enableNormalize, GlStateManager::disableNormalize);
		MinecraftGlState.toggle(this.rescaleNormal, GlStateManager::enableRescaleNormal, GlStateManager::disableRescaleNormal);
		MinecraftGlState.toggle(this.colorMaterial, GlStateManager::enableColorMaterial, GlStateManager::disableColorMaterial);
		MinecraftGlState.toggle(this.lights[0], () -> GlStateManager.enableLight(0), () -> GlStateManager.disableLight(0));
		MinecraftGlState.toggle(this.lights[1], () -> GlStateManager.enableLight(1), () -> GlStateManager.disableLight(1));
		GlStateManager.alphaFunc(this.alphaFunc, this.alphaRef);
		GlStateManager.shadeModel(this.shadeModel);
		GlStateManager.tryBlendFuncSeparate(this.blend[0], this.blend[1], this.blend[2], this.blend[3]);
		GlStateManager.depthFunc(this.depth[0]);
		GlStateManager.depthMask(this.depth[1] != 0);
		GlStateManager.color(this.color[0], this.color[1], this.color[2], this.color[3]);
		GlStateManager.colorMask(this.colorMask[0], this.colorMask[1], this.colorMask[2], this.colorMask[3]);

		GlStateManager.setActiveTexture(this.activeTexture);
		for (int unit = 0; unit < this.textures.length; unit++) {
			GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + unit);
			MinecraftGlState.toggle(this.texturing[unit], GlStateManager::enableTexture2D, GlStateManager::disableTexture2D);
			GlStateManager.bindTexture(this.textures[unit]);
		}
		GlStateManager.setActiveTexture(this.activeTexture);
	}

	private static void toggle(final boolean enabled, final Runnable enable, final Runnable disable) {
		if (enabled) {
			enable.run();
		} else {
			disable.run();
		}
	}

}