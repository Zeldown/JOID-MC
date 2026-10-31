package dev.joid.backend.minecraft.bridge.snapshot;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftGlState {

	private static final int[] PIXEL_STORE  = {GL11.GL_PACK_ALIGNMENT, GL11.GL_PACK_ROW_LENGTH, GL11.GL_PACK_SKIP_ROWS, GL11.GL_PACK_SKIP_PIXELS, GL11.GL_UNPACK_ALIGNMENT, GL11.GL_UNPACK_ROW_LENGTH, GL11.GL_UNPACK_SKIP_ROWS, GL11.GL_UNPACK_SKIP_PIXELS};
	private static final int[] CAPABILITIES = {GL11.GL_BLEND, GL11.GL_SCISSOR_TEST, GL11.GL_COLOR_LOGIC_OP, GL11.GL_CULL_FACE, GL11.GL_DEPTH_TEST, GL11.GL_STENCIL_TEST, GL11.GL_POLYGON_OFFSET_FILL};

	private final int[]     blend        = new int[6];
	private final int[]     faces        = new int[3];
	private final int[]     depth        = new int[2];
	private final int[]     buffers      = new int[3];
	private final int[]     textures     = new int[4];
	private final int[]     samplers     = new int[4];
	private final int[]     viewport     = new int[4];
	private final int[]     pixelStore   = new int[MinecraftGlState.PIXEL_STORE.length];
	private final int[]     scissorBox   = new int[4];
	private final byte[]    colorMask    = new byte[4];
	private final int[]     frameBuffers = new int[2];
	private final float[]   clearColor   = new float[4];
	private final boolean[] capabilities = new boolean[MinecraftGlState.CAPABILITIES.length];

	private int    logicOp;
	private int    vertexArray;
	private int    stencilMask;
	private int    renderBuffer;
	private int    clearStencil;
	private int    activeTexture;
	private double clearDepth;

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
			state.textures[unit] = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
			state.samplers[unit] = GL11.glGetInteger(GL33.GL_SAMPLER_BINDING);
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
		state.vertexArray     = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
		state.renderBuffer    = GL11.glGetInteger(GL30.GL_RENDERBUFFER_BINDING);
		state.logicOp         = GL11.glGetInteger(GL11.GL_LOGIC_OP_MODE);
		state.stencilMask     = GL11.glGetInteger(GL11.GL_STENCIL_WRITEMASK);
		state.clearStencil    = GL11.glGetInteger(GL11.GL_STENCIL_CLEAR_VALUE);
		state.clearDepth      = GL11.glGetDouble(GL11.GL_DEPTH_CLEAR_VALUE);
		return state;
	}

	public void restore() {
		for (int index = 0; index < MinecraftGlState.CAPABILITIES.length; index++) {
			if (this.capabilities[index]) {
				GL11.glEnable(MinecraftGlState.CAPABILITIES[index]);
			} else {
				GL11.glDisable(MinecraftGlState.CAPABILITIES[index]);
			}
		}

		for (int index = 0; index < MinecraftGlState.PIXEL_STORE.length; index++) {
			GL11.glPixelStorei(MinecraftGlState.PIXEL_STORE[index], this.pixelStore[index]);
		}

		for (int unit = 0; unit < this.textures.length; unit++) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textures[unit]);
			GL33.glBindSampler(unit, this.samplers[unit]);
		}
		GL13.glActiveTexture(this.activeTexture);

		GL11.glViewport(this.viewport[0], this.viewport[1], this.viewport[2], this.viewport[3]);
		GL11.glScissor(this.scissorBox[0], this.scissorBox[1], this.scissorBox[2], this.scissorBox[3]);
		GL14.glBlendFuncSeparate(this.blend[0], this.blend[1], this.blend[2], this.blend[3]);
		GL20.glBlendEquationSeparate(this.blend[4], this.blend[5]);
		GL11.glClearColor(this.clearColor[0], this.clearColor[1], this.clearColor[2], this.clearColor[3]);
		GL11.glColorMask(this.colorMask[0] != 0, this.colorMask[1] != 0, this.colorMask[2] != 0, this.colorMask[3] != 0);
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
		GL11.glStencilMask(this.stencilMask);
		GL11.glClearStencil(this.clearStencil);
		GL11.glClearDepth(this.clearDepth);
	}

	public static void resetPixelStore() {
		for (final int name : MinecraftGlState.PIXEL_STORE) {
			GL11.glPixelStorei(name, name == GL11.GL_PACK_ALIGNMENT || name == GL11.GL_UNPACK_ALIGNMENT ? 4 : 0);
		}
	}

}