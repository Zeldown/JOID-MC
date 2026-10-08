package dev.joid.impl.minecraft.render.stencil;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dev.joid.impl.minecraft.render.shader.ShaderTranslator;
import dev.joid.impl.minecraft.render.shader.uniform.UniformBlock;
import dev.joid.lib.bridge.render.shader.source.ShaderSource;
import dev.joid.lib.bridge.render.shader.source.ShaderStage;
import dev.joid.lib.bridge.render.state.RenderState;
import dev.joid.lib.bridge.render.state.StencilFunction;
import dev.joid.lib.bridge.render.state.StencilOperation;

public class StencilEmulationTest {

	@Test
	public void testsNothingWhenTheStencilTestIsOff() {
		final StencilEmulation emulation = StencilEmulation.create(StencilEmulationTest.createState(false, StencilOperation.REPLACE), true);
		Assert.assertFalse(emulation.isTest());
		Assert.assertFalse(emulation.isWrite());
	}

	@Test
	public void testsNothingOutsideTheScreen() {
		final StencilEmulation emulation = StencilEmulation.create(StencilEmulationTest.createState(true, StencilOperation.REPLACE), false);
		Assert.assertFalse(emulation.isTest());
		Assert.assertFalse(emulation.isWrite());
	}

	@Test
	public void writesOnlyWhenAnOperationChangesTheStencil() {
		Assert.assertFalse(StencilEmulation.create(StencilEmulationTest.createState(true, StencilOperation.KEEP), true).isWrite());
		Assert.assertTrue(StencilEmulation.create(StencilEmulationTest.createState(true, StencilOperation.INCREMENT), true).isWrite());
	}

	@Test
	public void writesWhenOnlyTheFailOperationChangesTheStencil() {
		final RenderState state = StencilEmulationTest.createState(true, StencilOperation.KEEP);
		state.setStencilFail(StencilOperation.ZERO);
		Assert.assertTrue(StencilEmulation.create(state, true).isWrite());
	}

	@Test
	public void keepsEightBitsOfTheReferenceAndTheMask() {
		final RenderState state = StencilEmulationTest.createState(true, StencilOperation.KEEP);
		state.setStencilReference(0x1FF);
		state.setStencilMask(0xFFFFFFFF);
		final StencilEmulation emulation = StencilEmulation.create(state, true);
		Assert.assertEquals(0xFF, emulation.getReference());
		Assert.assertEquals(0xFF, emulation.getMask());
	}

	@Test
	public void writesItsUniforms() {
		final ShaderSource vertex = ShaderSource.parse(ShaderStage.VERTEX, "void main() {\n    gl_Position = vec4(aPosition, 1.0);\n}\n");
		final ShaderSource fragment = ShaderSource.parse(ShaderStage.FRAGMENT, "void main() {\n    fragColor = vec4(1.0);\n}\n");
		final UniformBlock block = UniformBlock.create(ShaderTranslator.getUniforms(vertex, fragment), "");
		final RenderState state = StencilEmulationTest.createState(true, StencilOperation.INVERT);
		state.setStencilFunction(StencilFunction.NOT_EQUAL);
		state.setStencilReference(3);
		StencilEmulation.create(state, true).write(block);
		Assert.assertEquals(1, StencilEmulationTest.read(block, "joid_StencilTest"));
		Assert.assertEquals(6, StencilEmulationTest.read(block, "joid_StencilFunction"));
		Assert.assertEquals(3, StencilEmulationTest.read(block, "joid_StencilReference"));
		Assert.assertEquals(0xFF, StencilEmulationTest.read(block, "joid_StencilMask"));
		Assert.assertEquals(0, StencilEmulationTest.read(block, "joid_StencilFail"));
		Assert.assertEquals(5, StencilEmulationTest.read(block, "joid_StencilPass"));
	}

	@Test
	public void matchesTheFunctionCodesOfTheShader() {
		Assert.assertEquals(List.of(StencilFunction.NEVER, StencilFunction.LESS, StencilFunction.LESS_EQUAL, StencilFunction.GREATER, StencilFunction.GREATER_EQUAL, StencilFunction.EQUAL, StencilFunction.NOT_EQUAL, StencilFunction.ALWAYS), List.of(StencilFunction.values()));
	}

	@Test
	public void matchesTheOperationCodesOfTheShader() {
		Assert.assertEquals(List.of(StencilOperation.KEEP, StencilOperation.ZERO, StencilOperation.REPLACE, StencilOperation.INCREMENT, StencilOperation.DECREMENT, StencilOperation.INVERT), List.of(StencilOperation.values()));
	}

	private static RenderState createState(final boolean test, final StencilOperation pass) {
		final RenderState state = new RenderState();
		state.setStencilTest(test);
		state.setStencilPass(pass);
		return state;
	}

	private static int read(final UniformBlock block, final String name) {
		return block.getData().getInt(block.getMember(name).getOffset());
	}

}