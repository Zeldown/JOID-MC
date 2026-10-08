package dev.joid.impl.minecraft.render.shader.uniform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import dev.joid.impl.minecraft.render.shader.GlslCompiler;
import dev.joid.impl.minecraft.render.shader.SpirvBlockLayout;
import dev.joid.lib.bridge.render.shader.source.ShaderStage;
import dev.joid.lib.bridge.render.shader.source.ShaderVariable;

public class UniformBlockTest {

	private static final String[][] MEMBERS = {
			{"float", "a", ""},
			{"vec3", "b", ""},
			{"float", "c", ""},
			{"vec2", "d", ""},
			{"ivec3", "e", ""},
			{"bool", "f", ""},
			{"mat2", "g", ""},
			{"float", "h", "[3]"},
			{"vec3", "i", "[2]"},
			{"int", "j", ""},
			{"mat3", "k", ""},
			{"vec4", "l", "[COUNT]"},
			{"mat4", "m", ""},
			{"uvec2", "n", ""},
			{"mat4", "o", "[2]"},
			{"bvec4", "p", ""},
			{"float", "q", ""}
	};

	@Test
	public void packsAScalarAfterAVec3() {
		final UniformBlock block = UniformBlockTest.create();
		Assert.assertEquals(16, block.getMember("b").getOffset());
		Assert.assertEquals(28, block.getMember("c").getOffset());
	}

	@Test
	public void alignsAVec2OnEightBytes() {
		Assert.assertEquals(32, UniformBlockTest.create().getMember("d").getOffset());
	}

	@Test
	public void stridesArraysAndMatrixColumnsOnSixteenBytes() {
		final UniformBlock block = UniformBlockTest.create();
		Assert.assertEquals(16, block.getMember("g").getMatrixStride());
		Assert.assertEquals(32, block.getMember("g").getSize());
		Assert.assertEquals(16, block.getMember("h").getArrayStride());
		Assert.assertEquals(48, block.getMember("h").getSize());
		Assert.assertEquals(16, block.getMember("i").getArrayStride());
		Assert.assertEquals(48, block.getMember("k").getSize());
		Assert.assertEquals(64, block.getMember("o").getArrayStride());
	}

	@Test
	public void resolvesAnArrayLengthFromADefine() {
		Assert.assertEquals(80, UniformBlockTest.create().getMember("l").getSize());
	}

	@Test
	public void roundsTheBlockSizeToSixteenBytes() {
		final UniformBlock block = UniformBlockTest.create();
		Assert.assertEquals(0, block.getSize() % 16);
		Assert.assertEquals(block.getMember("q").getOffset() + 16 - block.getMember("q").getOffset() % 16, block.getSize());
	}

	@Test
	public void layoutsEveryMemberLikeTheCompiler() {
		final StringBuilder source = new StringBuilder("#version 330\n#define COUNT 5\nlayout(std140) uniform Probe {\n");
		final StringBuilder use = new StringBuilder();
		for (final String[] member : UniformBlockTest.MEMBERS) {
			source.append('\t').append(member[0]).append(' ').append(member[1]).append(member[2]).append(";\n");
			use.append(" + float(").append(member[1]).append(member[2].isEmpty() ? "" : "[0]").append(member[0].startsWith("mat") ? "[0][0]" : member[0].endsWith("vec2") || member[0].endsWith("vec3") || member[0].endsWith("vec4") ? ".x" : "").append(')');
		}
		source.append("};\nout vec4 fragColor;\nvoid main() {\n\tfragColor = vec4(0.0").append(use).append(");\n}\n");

		final Map<String, int[]> layoutMap = SpirvBlockLayout.read(GlslCompiler.compileVulkan(source.toString(), ShaderStage.FRAGMENT), "Probe");
		final UniformBlock block = UniformBlockTest.create();
		Assert.assertEquals(block.getMemberMap().keySet(), layoutMap.keySet());
		for (final UniformMember member : block.getMemberMap().values()) {
			Assert.assertArrayEquals(member.getName(), new int[] {member.getOffset(), member.getArrayStride(), member.getMatrixStride()}, layoutMap.get(member.getName()));
		}
	}

	@Test
	public void writesAMatrixColumnByColumn() {
		final UniformMember member = UniformBlockTest.create().getMember("k");
		member.putMatrix(new float[] {1F, 2F, 3F, 4F, 5F, 6F, 7F, 8F, 9F});
		Assert.assertEquals(1F, member.getData().getFloat(member.getOffset()), 0F);
		Assert.assertEquals(3F, member.getData().getFloat(member.getOffset() + 8), 0F);
		Assert.assertEquals(4F, member.getData().getFloat(member.getOffset() + 16), 0F);
		Assert.assertEquals(9F, member.getData().getFloat(member.getOffset() + 40), 0F);
	}

	@Test
	public void writesArrayElementsOnTheirStride() {
		final UniformMember member = UniformBlockTest.create().getMember("h");
		member.putArray(new float[] {1F, 2F, 3F}, 1);
		Assert.assertEquals(2F, member.getData().getFloat(member.getOffset() + 16), 0F);
		Assert.assertEquals(3F, member.getData().getFloat(member.getOffset() + 32), 0F);
	}

	@Test
	public void writesScalarsAndVectorsAtTheirOffset() {
		final UniformBlock block = UniformBlockTest.create();
		block.getMember("j").putInt(7);
		block.getMember("b").putFloats(1F, 2F, 3F);
		Assert.assertEquals(7, block.getData().getInt(block.getMember("j").getOffset()));
		Assert.assertEquals(3F, block.getData().getFloat(block.getMember("b").getOffset() + 8), 0F);
		Assert.assertEquals(0F, block.getData().getFloat(block.getMember("c").getOffset()), 0F);
	}

	@Test(expected = IllegalArgumentException.class)
	public void refusesTooManyFloatsForAVector() {
		UniformBlockTest.create().getMember("b").putFloats(1F, 2F, 3F, 4F);
	}

	@Test(expected = IllegalArgumentException.class)
	public void refusesTooManyArrayElements() {
		UniformBlockTest.create().getMember("h").putArray(new float[] {1F, 2F, 3F, 4F}, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void refusesAMatrixOfTheWrongSize() {
		UniformBlockTest.create().getMember("k").putMatrix(new float[16]);
	}

	@Test
	public void namesAnUnsupportedType() {
		try {
			UniformBlock.create(List.of(ShaderVariable.create("dmat4", "x", "", false)), "");
			Assert.fail("An unsupported type must be refused");
		} catch (final IllegalArgumentException expected) {
			Assert.assertEquals("Unsupported uniform type dmat4", expected.getMessage());
		}
	}

	@Test
	public void namesAnUnresolvedArrayLength() {
		try {
			UniformBlock.create(List.of(ShaderVariable.create("float", "x", "[SIZE]", false)), "");
			Assert.fail("An unresolved array length must be refused");
		} catch (final IllegalArgumentException expected) {
			Assert.assertEquals("Unable to resolve the length SIZE of the uniform array x", expected.getMessage());
		}
	}

	@Test
	public void returnsNoMemberForAnUnknownName() {
		Assert.assertNull(UniformBlockTest.create().getMember("unknown"));
	}

	private static UniformBlock create() {
		final List<ShaderVariable> variables = new ArrayList<>();
		for (final String[] member : UniformBlockTest.MEMBERS) {
			variables.add(ShaderVariable.create(member[0], member[1], member[2], false));
		}
		return UniformBlock.create(variables, "#define COUNT 5\n");
	}

}