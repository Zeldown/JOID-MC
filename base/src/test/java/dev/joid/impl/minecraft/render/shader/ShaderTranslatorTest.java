package dev.joid.impl.minecraft.render.shader;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import dev.joid.impl.minecraft.render.shader.uniform.UniformBlock;
import dev.joid.impl.minecraft.render.shader.uniform.UniformMember;
import dev.joid.lib.bridge.render.shader.source.ShaderSource;
import dev.joid.lib.bridge.render.shader.source.ShaderStage;
import dev.joid.test.shader.CoreShaders;

public class ShaderTranslatorTest {

	private static final String VERTEX   = "out vec2 vTexCoord;\nflat out vec4 vColor;\nuniform float u_Scale;\nuniform vec4 u_Colors[4];\n\nvoid main() {\n    vTexCoord = aTexCoord * u_Scale;\n    vColor = aColor * u_Colors[1];\n    gl_Position = uProjectionMatrix * uModelViewMatrix * vec4(aPosition, 1.0);\n}\n";
	private static final String FRAGMENT = "in vec2 vTexCoord;\nflat in vec4 vColor;\nuniform sampler2D tex;\nuniform vec3 u_Tint;\nuniform mat3 u_Transform;\n\nvoid main() {\n    fragColor = texture(tex, (u_Transform * vec3(vTexCoord, 1.0)).xy) * vColor * vec4(u_Tint, uLighting ? 1.0 : 0.5);\n}\n";

	@Test
	public void compilesEveryCoreShaderForVulkan() {
		for (final String name : CoreShaders.getNames()) {
			final ShaderSource vertex = CoreShaders.read(name, ShaderStage.VERTEX);
			final ShaderSource fragment = CoreShaders.read(name, ShaderStage.FRAGMENT);
			Assert.assertTrue(name, GlslCompiler.compileVulkan(ShaderTranslator.translateVertex(vertex, fragment), ShaderStage.VERTEX).remaining() > 0);
			Assert.assertTrue(name, GlslCompiler.compileVulkan(ShaderTranslator.translateFragment(vertex, fragment), ShaderStage.FRAGMENT).remaining() > 0);
			Assert.assertTrue(name, GlslCompiler.compileVulkan(ShaderTranslator.translateStencil(vertex, fragment), ShaderStage.FRAGMENT).remaining() > 0);
		}
	}

	@Test
	public void compilesEveryCoreShaderForOpenGl() {
		for (final String name : CoreShaders.getNames()) {
			final ShaderSource vertex = CoreShaders.read(name, ShaderStage.VERTEX);
			final ShaderSource fragment = CoreShaders.read(name, ShaderStage.FRAGMENT);
			Assert.assertTrue(name, GlslCompiler.compileOpenGl(ShaderTranslator.translateVertex(vertex, fragment), ShaderStage.VERTEX).remaining() > 0);
			Assert.assertTrue(name, GlslCompiler.compileOpenGl(ShaderTranslator.translateFragment(vertex, fragment), ShaderStage.FRAGMENT).remaining() > 0);
			Assert.assertTrue(name, GlslCompiler.compileOpenGl(ShaderTranslator.translateStencil(vertex, fragment), ShaderStage.FRAGMENT).remaining() > 0);
		}
	}

	@Test
	public void translatesEveryCoreShaderTheSameWayTwice() {
		for (final String name : CoreShaders.getNames()) {
			Assert.assertEquals(name, ShaderTranslatorTest.translate(name), ShaderTranslatorTest.translate(name));
		}
	}

	@Test
	public void wrapsTheFragmentMainOfEveryCoreShader() {
		for (final String name : CoreShaders.getNames()) {
			final ShaderSource vertex = CoreShaders.read(name, ShaderStage.VERTEX);
			final ShaderSource fragment = CoreShaders.read(name, ShaderStage.FRAGMENT);
			for (final String translated : new String[] {ShaderTranslator.translateFragment(vertex, fragment), ShaderTranslator.translateStencil(vertex, fragment)}) {
				Assert.assertEquals(name, 1, ShaderTranslatorTest.count(translated, "void\\s+joid_main\\s*\\(\\s*\\)"));
				Assert.assertEquals(name, 1, ShaderTranslatorTest.count(translated, "void\\s+main\\s*\\(\\s*\\)"));
				Assert.assertTrue(name, translated.contains("\tjoid_main();\n"));
			}
		}
	}

	@Test
	public void sharesTheUniformBlockBetweenStages() {
		final ShaderSource vertex = ShaderSource.parse(ShaderStage.VERTEX, ShaderTranslatorTest.VERTEX);
		final ShaderSource fragment = ShaderSource.parse(ShaderStage.FRAGMENT, ShaderTranslatorTest.FRAGMENT);
		final String block = "layout(std140) uniform JoidUniforms {\n\tmat4 uProjectionMatrix;\n\tmat4 uModelViewMatrix;\n\tbool uLighting;\n\tint joid_AlphaTest;\n\tfloat joid_AlphaThreshold;\n\tint joid_StencilTest;\n\tint joid_StencilFunction;\n\tint joid_StencilReference;\n\tint joid_StencilMask;\n\tint joid_StencilFail;\n\tint joid_StencilPass;\n\tfloat u_Scale;\n\tvec4 u_Colors[4];\n\tvec3 u_Tint;\n\tmat3 u_Transform;\n};\n";
		Assert.assertTrue(ShaderTranslator.translateVertex(vertex, fragment).contains(block));
		Assert.assertTrue(ShaderTranslator.translateFragment(vertex, fragment).contains(block));
		Assert.assertTrue(ShaderTranslator.translateStencil(vertex, fragment).contains(block));
	}

	@Test
	public void declaresTheNamedAttributesAndVaryings() {
		final ShaderSource vertex = ShaderSource.parse(ShaderStage.VERTEX, ShaderTranslatorTest.VERTEX);
		final ShaderSource fragment = ShaderSource.parse(ShaderStage.FRAGMENT, ShaderTranslatorTest.FRAGMENT);
		final String translatedVertex = ShaderTranslator.translateVertex(vertex, fragment);
		final String translatedFragment = ShaderTranslator.translateFragment(vertex, fragment);
		Assert.assertTrue(translatedVertex.contains("in vec3 aPosition;\nin vec2 aTexCoord;\nin vec4 aColor;\nin vec3 aNormal;\n"));
		Assert.assertTrue(translatedVertex.contains("out vec2 vTexCoord;\nflat out vec4 vColor;\n"));
		Assert.assertTrue(translatedFragment.contains("in vec2 vTexCoord;\nflat in vec4 vColor;\nout vec4 fragColor;\n"));
	}

	@Test
	public void declaresTheStencilSamplerAfterTheShaderSamplers() {
		final ShaderSource vertex = ShaderSource.parse(ShaderStage.VERTEX, ShaderTranslatorTest.VERTEX);
		final ShaderSource fragment = ShaderSource.parse(ShaderStage.FRAGMENT, ShaderTranslatorTest.FRAGMENT);
		Assert.assertTrue(ShaderTranslator.translateFragment(vertex, fragment).contains("uniform sampler2D tex;\nuniform sampler2D joid_Stencil;\n"));
		Assert.assertEquals(List.of("tex"), ShaderTranslator.getSamplers(vertex, fragment));
	}

	@Test
	public void writesTheStencilOnlyInTheStencilVariant() {
		final ShaderSource vertex = ShaderSource.parse(ShaderStage.VERTEX, ShaderTranslatorTest.VERTEX);
		final ShaderSource fragment = ShaderSource.parse(ShaderStage.FRAGMENT, ShaderTranslatorTest.FRAGMENT);
		Assert.assertFalse(ShaderTranslator.translateFragment(vertex, fragment).contains("joid_stencilApply(joid_stencilCompare"));
		Assert.assertTrue(ShaderTranslator.translateFragment(vertex, fragment).contains("if (joid_StencilTest != 0 && !joid_stencilCompare(joid_stencilValue())) {"));
		Assert.assertTrue(ShaderTranslator.translateStencil(vertex, fragment).contains("value = joid_stencilApply(joid_stencilCompare(value) ? joid_StencilPass : joid_StencilFail, value);"));
	}

	@Test
	public void keepsTheLineNumbersOfTheBody() {
		final ShaderSource vertex = ShaderSource.parse(ShaderStage.VERTEX, ShaderTranslatorTest.VERTEX);
		final ShaderSource fragment = ShaderSource.parse(ShaderStage.FRAGMENT, ShaderTranslatorTest.FRAGMENT);
		final String translated = ShaderTranslator.translateFragment(vertex, fragment);
		final String[] lines = translated.substring(translated.indexOf("#line 1\n") + "#line 1\n".length()).split("\n", -1);
		Assert.assertEquals("    fragColor = texture(tex, (u_Transform * vec3(vTexCoord, 1.0)).xy) * vColor * vec4(u_Tint, uLighting ? 1.0 : 0.5);", lines[7]);
	}

	@Test
	public void layoutsTheBlockLikeTheCompiler() {
		final ShaderSource vertex = ShaderSource.parse(ShaderStage.VERTEX, ShaderTranslatorTest.VERTEX);
		final ShaderSource fragment = ShaderSource.parse(ShaderStage.FRAGMENT, ShaderTranslatorTest.FRAGMENT);
		final UniformBlock block = UniformBlock.create(ShaderTranslator.getUniforms(vertex, fragment), vertex.getBody() + fragment.getBody());
		final Map<String, int[]> layoutMap = SpirvBlockLayout.read(GlslCompiler.compileVulkan(ShaderTranslator.translateFragment(vertex, fragment), ShaderStage.FRAGMENT), ShaderTranslator.BLOCK);
		Assert.assertEquals(block.getMemberMap().keySet(), layoutMap.keySet());
		for (final UniformMember member : block.getMemberMap().values()) {
			Assert.assertArrayEquals(member.getName(), new int[] {member.getOffset(), member.getArrayStride(), member.getMatrixStride()}, layoutMap.get(member.getName()));
		}
	}

	private static String translate(final String name) {
		final ShaderSource vertex = CoreShaders.read(name, ShaderStage.VERTEX);
		final ShaderSource fragment = CoreShaders.read(name, ShaderStage.FRAGMENT);
		return ShaderTranslator.translateVertex(vertex, fragment) + ShaderTranslator.translateFragment(vertex, fragment) + ShaderTranslator.translateStencil(vertex, fragment);
	}

	private static int count(final String text, final String regex) {
		final Matcher matcher = Pattern.compile(regex).matcher(text);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		return count;
	}

}