package dev.joid.backend.minecraft.bridge.render;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import com.mojang.blaze3d.platform.GlStateManager;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftGlState {

	private static final int[] PIXEL_STORE  = {GL11.GL_PACK_ALIGNMENT, GL11.GL_PACK_ROW_LENGTH, GL11.GL_PACK_SKIP_ROWS, GL11.GL_PACK_SKIP_PIXELS, GL11.GL_UNPACK_ALIGNMENT, GL11.GL_UNPACK_ROW_LENGTH, GL11.GL_UNPACK_SKIP_ROWS, GL11.GL_UNPACK_SKIP_PIXELS};
	private static final int[] CAPABILITIES = {GL11.GL_BLEND, GL11.GL_SCISSOR_TEST, GL11.GL_COLOR_LOGIC_OP, GL11.GL_CULL_FACE, GL11.GL_DEPTH_TEST, GL11.GL_STENCIL_TEST, GL11.GL_POLYGON_OFFSET_FILL, GL11.GL_ALPHA_TEST, GL11.GL_LIGHTING, GL11.GL_LIGHT0, GL11.GL_LIGHT1, GL11.GL_COLOR_MATERIAL, GL11.GL_FOG, GL11.GL_NORMALIZE, GL12.GL_RESCALE_NORMAL};

	private final int[]     blend        = new int[6];
	private final int[]     faces        = new int[3];
	private final int[]     depth        = new int[2];
	private final int[]     buffers      = new int[3];
	private final int[]     textures     = new int[4];
	private final int[]     samplers     = new int[4];
	private final int[]     viewport     = new int[4];
	private final float[]   color        = new float[4];
	private final int[]     pixelStore   = new int[MinecraftGlState.PIXEL_STORE.length];
	private final int[]     scissorBox   = new int[4];
	private final byte[]    colorMask    = new byte[4];
	private final boolean[] texturing    = new boolean[4];
	private final int[]     frameBuffers = new int[2];
	private final float[]   clearColor   = new float[4];
	private final boolean[] capabilities = new boolean[MinecraftGlState.CAPABILITIES.length];

	private int    program;
	private int    logicOp;
	private float  alphaRef;
	private int    alphaFunc;
	private int    shadeModel;
	private int    vertexArray;
	private int    stencilMask;
	private int    renderBuffer;
	private int    clearStencil;
	private int    activeTexture;
	private double clearDepth;

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
		for (int index = 0; index < MinecraftGlState.CAPABILITIES.length; index++) {
			state.capabilities[index] = GL11.glIsEnabled(MinecraftGlState.CAPABILITIES[index]);
		}

		for (int index = 0; index < MinecraftGlState.PIXEL_STORE.length; index++) {
			state.pixelStore[index] = GL11.glGetInteger(MinecraftGlState.PIXEL_STORE[index]);
		}

		state.activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
		for (int unit = 0; unit < state.textures.length; unit++) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
			state.texturing[unit] = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
			state.textures[unit]  = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
			state.samplers[unit]  = GL11.glGetInteger(GL33.GL_SAMPLER_BINDING);
		}
		GL13.glActiveTexture(state.activeTexture);

		GL11.glGetIntegerv(GL11.GL_VIEWPORT, state.viewport);
		GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, state.scissorBox);
		state.blend[0] = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
		state.blend[1] = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
		state.blend[2] = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
		state.blend[3] = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
		state.blend[4] = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_RGB);
		state.blend[5] = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_ALPHA);
		GL11.glGetFloatv(GL11.GL_COLOR_CLEAR_VALUE, state.clearColor);
		GL11.glGetFloatv(GL11.GL_CURRENT_COLOR, state.color);
		final ByteBuffer mask = BufferUtils.createByteBuffer(16);
		GL11.glGetBooleanv(GL11.GL_COLOR_WRITEMASK, mask);
		mask.get(state.colorMask);
		state.buffers[0]      = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
		state.buffers[1]      = GL11.glGetInteger(GL21.GL_PIXEL_PACK_BUFFER_BINDING);
		state.buffers[2]      = GL11.glGetInteger(GL21.GL_PIXEL_UNPACK_BUFFER_BINDING);
		state.frameBuffers[0] = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
		state.frameBuffers[1] = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
		state.faces[0]        = GL11.glGetInteger(GL11.GL_CULL_FACE_MODE);
		state.faces[1]        = GL11.glGetInteger(GL11.GL_FRONT_FACE);
		state.faces[2]        = GL11.glGetInteger(GL11.GL_POLYGON_MODE);
		state.depth[0]        = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
		state.depth[1]        = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK) ? 1 : 0;
		state.program         = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
		state.logicOp         = GL11.glGetInteger(GL11.GL_LOGIC_OP_MODE);
		state.alphaRef        = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
		state.alphaFunc       = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
		state.shadeModel      = GL11.glGetInteger(GL11.GL_SHADE_MODEL);
		state.vertexArray     = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
		state.stencilMask     = GL11.glGetInteger(GL11.GL_STENCIL_WRITEMASK);
		state.renderBuffer    = GL11.glGetInteger(GL30.GL_RENDERBUFFER_BINDING);
		state.clearStencil    = GL11.glGetInteger(GL11.GL_STENCIL_CLEAR_VALUE);
		state.clearDepth      = GL11.glGetDouble(GL11.GL_DEPTH_CLEAR_VALUE);
		return state;
	}

	public void restore() {
		for (int index = 0; index < MinecraftGlState.CAPABILITIES.length; index++) {
			MinecraftGlState.toggle(this.capabilities[index], MinecraftGlState.CAPABILITIES[index]);
		}

		for (int index = 0; index < MinecraftGlState.PIXEL_STORE.length; index++) {
			GL11.glPixelStorei(MinecraftGlState.PIXEL_STORE[index], this.pixelStore[index]);
		}

		for (int unit = 0; unit < this.textures.length; unit++) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
			MinecraftGlState.toggle(this.texturing[unit], GL11.GL_TEXTURE_2D);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textures[unit]);
			GL33.glBindSampler(unit, this.samplers[unit]);
		}
		GL13.glActiveTexture(this.activeTexture);

		GL11.glViewport(this.viewport[0], this.viewport[1], this.viewport[2], this.viewport[3]);
		GL11.glScissor(this.scissorBox[0], this.scissorBox[1], this.scissorBox[2], this.scissorBox[3]);
		GL14.glBlendFuncSeparate(this.blend[0], this.blend[1], this.blend[2], this.blend[3]);
		GL20.glBlendEquationSeparate(this.blend[4], this.blend[5]);
		GL11.glClearColor(this.clearColor[0], this.clearColor[1], this.clearColor[2], this.clearColor[3]);
		GL11.glColor4f(this.color[0], this.color[1], this.color[2], this.color[3]);
		GL11.glColorMask(this.colorMask[0] != 0, this.colorMask[1] != 0, this.colorMask[2] != 0, this.colorMask[3] != 0);
		GL20.glUseProgram(this.program);
		GL30.glBindVertexArray(this.vertexArray);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.buffers[0]);
		GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, this.buffers[1]);
		GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, this.buffers[2]);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.frameBuffers[0]);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.frameBuffers[1]);
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.renderBuffer);
		GL11.glCullFace(this.faces[0]);
		GL11.glFrontFace(this.faces[1]);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, this.faces[2]);
		GL11.glDepthFunc(this.depth[0]);
		GL11.glDepthMask(this.depth[1] != 0);
		GL11.glLogicOp(this.logicOp);
		GL11.glAlphaFunc(this.alphaFunc, this.alphaRef);
		GL11.glShadeModel(this.shadeModel);
		GL11.glStencilMask(this.stencilMask);
		GL11.glClearStencil(this.clearStencil);
		GL11.glClearDepth(this.clearDepth);
		this.synchronize();
	}

	public static void resetPixelStore() {
		for (final int name : MinecraftGlState.PIXEL_STORE) {
			GL11.glPixelStorei(name, name == GL11.GL_PACK_ALIGNMENT || name == GL11.GL_UNPACK_ALIGNMENT ? 4 : 0);
		}
	}

	private void synchronize() {
		MinecraftGlState.toggle(this.capabilities[0], GlStateManager::_enableBlend, GlStateManager::_disableBlend);
		MinecraftGlState.toggle(this.capabilities[1], GlStateManager::_enableScissorTest, GlStateManager::_disableScissorTest);
		MinecraftGlState.toggle(this.capabilities[2], GlStateManager::_enableColorLogicOp, GlStateManager::_disableColorLogicOp);
		MinecraftGlState.toggle(this.capabilities[3], GlStateManager::_enableCull, GlStateManager::_disableCull);
		MinecraftGlState.toggle(this.capabilities[4], GlStateManager::_enableDepthTest, GlStateManager::_disableDepthTest);
		MinecraftGlState.toggle(this.capabilities[6], GlStateManager::_enablePolygonOffset, GlStateManager::_disablePolygonOffset);
		MinecraftGlState.toggle(this.capabilities[7], GlStateManager::_enableAlphaTest, GlStateManager::_disableAlphaTest);
		MinecraftGlState.toggle(this.capabilities[8], GlStateManager::_enableLighting, GlStateManager::_disableLighting);
		MinecraftGlState.toggle(this.capabilities[11], GlStateManager::_enableColorMaterial, GlStateManager::_disableColorMaterial);
		MinecraftGlState.toggle(this.capabilities[12], GlStateManager::_enableFog, GlStateManager::_disableFog);
		MinecraftGlState.toggle(this.capabilities[14], GlStateManager::_enableRescaleNormal, GlStateManager::_disableRescaleNormal);
		GlStateManager._blendFuncSeparate(this.blend[0], this.blend[1], this.blend[2], this.blend[3]);
		GlStateManager._depthFunc(this.depth[0]);
		GlStateManager._depthMask(this.depth[1] != 0);
		GlStateManager._logicOp(this.logicOp);
		GlStateManager._alphaFunc(this.alphaFunc, this.alphaRef);
		GlStateManager._shadeModel(this.shadeModel);
		GlStateManager._stencilMask(this.stencilMask);
		GlStateManager._clearDepth(this.clearDepth);
		GlStateManager._clearStencil(this.clearStencil);
		GlStateManager._clearColor(this.clearColor[0], this.clearColor[1], this.clearColor[2], this.clearColor[3]);
		GlStateManager._color4f(this.color[0], this.color[1], this.color[2], this.color[3]);
		GlStateManager._colorMask(this.colorMask[0] != 0, this.colorMask[1] != 0, this.colorMask[2] != 0, this.colorMask[3] != 0);
		for (int unit = 0; unit < this.textures.length; unit++) {
			GlStateManager._activeTexture(GL13.GL_TEXTURE0 + unit);
			MinecraftGlState.toggle(this.texturing[unit], GlStateManager::_enableTexture, GlStateManager::_disableTexture);
			GlStateManager._bindTexture(this.textures[unit]);
		}
		GlStateManager._activeTexture(this.activeTexture);
	}

	private static void toggle(final boolean enabled, final int capability) {
		if (enabled) {
			GL11.glEnable(capability);
		} else {
			GL11.glDisable(capability);
		}
	}

	private static void toggle(final boolean enabled, final Runnable enable, final Runnable disable) {
		if (enabled) {
			enable.run();
		} else {
			disable.run();
		}
	}

}