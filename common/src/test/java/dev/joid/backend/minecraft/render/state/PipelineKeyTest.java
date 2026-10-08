package dev.joid.backend.minecraft.render.state;

import java.lang.reflect.Proxy;

import org.junit.Assert;
import org.junit.Test;

import dev.joid.lib.bridge.render.shader.IShader;
import dev.joid.lib.bridge.render.state.BlendState;
import dev.joid.lib.bridge.render.state.RenderState;

public class PipelineKeyTest {

	private static final IShader SHADER = (IShader) Proxy.newProxyInstance(IShader.class.getClassLoader(), new Class<?>[] {IShader.class}, (proxy, method, arguments) -> "equals".equals(method.getName()) ? proxy == arguments[0] : "hashCode".equals(method.getName()) ? System.identityHashCode(proxy) : null);

	@Test
	public void equalsAKeyOfAnEqualBlend() {
		final RenderState first = new RenderState();
		final RenderState second = new RenderState();
		first.setBlend(BlendState.create(BlendState.Equation.ADD, BlendState.Factor.ONE, BlendState.Factor.ONE_MINUS_SRC_ALPHA));
		second.setBlend(BlendState.PREMULTIPLIED);
		Assert.assertEquals(PipelineKey.create(PipelineKeyTest.SHADER, first, false), PipelineKey.create(PipelineKeyTest.SHADER, second, false));
		Assert.assertEquals(PipelineKey.create(PipelineKeyTest.SHADER, first, false).hashCode(), PipelineKey.create(PipelineKeyTest.SHADER, second, false).hashCode());
	}

	@Test
	public void differsFromAKeyOfAnotherBlend() {
		final RenderState first = new RenderState();
		final RenderState second = new RenderState();
		first.setBlend(BlendState.NORMAL);
		second.setBlend(BlendState.PREMULTIPLIED);
		Assert.assertNotEquals(PipelineKey.create(PipelineKeyTest.SHADER, first, false), PipelineKey.create(PipelineKeyTest.SHADER, second, false));
	}

	@Test
	public void ignoresTheFactorsOfADisabledBlend() {
		final RenderState state = new RenderState();
		state.setBlend(BlendState.DISABLED);
		final PipelineKey key = PipelineKey.create(PipelineKeyTest.SHADER, state, false);
		Assert.assertFalse(key.isBlend());
		Assert.assertEquals(BlendState.Factor.ONE, key.getSourceColor());
		Assert.assertEquals(BlendState.Factor.ZERO, key.getDestinationColor());
	}

	@Test
	public void writesNoDepthWithoutTheDepthTest() {
		final RenderState state = new RenderState();
		state.setDepthTest(false);
		state.setDepthWrite(true);
		Assert.assertFalse(PipelineKey.create(PipelineKeyTest.SHADER, state, false).isDepthWrite());
		state.setDepthTest(true);
		Assert.assertTrue(PipelineKey.create(PipelineKeyTest.SHADER, state, false).isDepthWrite());
	}

	@Test
	public void separatesTrianglesFromLines() {
		final RenderState state = new RenderState();
		Assert.assertNotEquals(PipelineKey.create(PipelineKeyTest.SHADER, state, false), PipelineKey.create(PipelineKeyTest.SHADER, state, true));
	}

	@Test
	public void writesTheStencilWithoutBlendNorDepth() {
		final RenderState state = new RenderState();
		state.setBlend(BlendState.NORMAL);
		state.setColorMask(false);
		state.setDepthTest(true);
		state.setCull(true);
		final PipelineKey key = PipelineKey.stencil(PipelineKeyTest.SHADER, state, false);
		Assert.assertTrue(key.isStencil());
		Assert.assertFalse(key.isBlend());
		Assert.assertTrue(key.isColorMask());
		Assert.assertFalse(key.isDepthTest());
		Assert.assertFalse(key.isDepthWrite());
		Assert.assertTrue(key.isCull());
		Assert.assertNotEquals(PipelineKey.create(PipelineKeyTest.SHADER, state, false), key);
	}

}