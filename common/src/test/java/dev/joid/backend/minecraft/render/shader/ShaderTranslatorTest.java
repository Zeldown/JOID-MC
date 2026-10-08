package dev.joid.backend.minecraft.render.shader;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import dev.joid.lib.bridge.render.shader.source.ShaderSource;
import dev.joid.lib.bridge.render.shader.source.ShaderStage;
import dev.joid.lib.bridge.render.shader.source.ShaderVariable;
import dev.joid.test.shader.CoreShaders;

public class ShaderTranslatorTest {

	private static final String VERTEX   = "out vec2 vTexCoord;\nflat out vec4 vColor;\nuniform float u_Scale;\nuniform vec4 u_Colors[4];\n\nvoid main() {\n    vTexCoord = aTexCoord * u_Scale;\n    vColor = aColor * u_Colors[1];\n    gl_Position = uProjectionMatrix * uModelViewMatrix * vec4(aPosition, 1.0);\n}\n";
	private static final String FRAGMENT = "in vec2 vTexCoord;\nflat in vec4 vColor;\nuniform sampler2D tex;\nuniform vec3 u_Tint;\nuniform mat3 u_Transform;\n\nvoid main() {\n    fragColor = texture(tex, (u_Transform * vec3(vTexCoord, 1.0)).xy) * vColor * vec4(u_Tint, uLighting ? 1.0 : 0.5);\n}\n";

	@Test
	public void compilesEveryCoreShaderForVulkan() {
		for (final String name : CoreShaders.getNames()) {
			final ShaderSource vertex = CoreShaders.read(name, ShaderStage.VERTEX);
			final ShaderSource fragment = CoreShaders.read(name, ShaderStage.FRAGMENT);
			Assert.assertTrue(name, GlslCompiler.compileVulkan(ShaderTranslator.create().translateVertex(vertex, fragment), ShaderStage.VERTEX).remaining() > 0);
			Assert.assertTrue(name, GlslCompiler.compileVulkan(ShaderTranslator.create().translateFragment(vertex, fragment), ShaderStage.FRAGMENT).remaining() > 0);
			Assert.assertTrue(name, GlslCompiler.compileVulkan(ShaderTranslator.createStencil().translateFragment(vertex, fragment), ShaderStage.FRAGMENT).remaining() > 0);
		}
	}

	@Test
	public void compilesEveryCoreShaderForOpenGl() {
		for (final String name : CoreShaders.getNames()) {
			final ShaderSource vertex = CoreShaders.read(name, ShaderStage.VERTEX);
			final ShaderSource fragment = CoreShaders.read(name, ShaderStage.FRAGMENT);
			Assert.assertTrue(name, GlslCompiler.compileOpenGl(ShaderTranslator.create().translateVertex(vertex, fragment), ShaderStage.VERTEX).remaining() > 0);
			Assert.assertTrue(name, GlslCompiler.compileOpenGl(ShaderTranslator.create().translateFragment(vertex, fragment), ShaderStage.FRAGMENT).remaining() > 0);
			Assert.assertTrue(name, GlslCompiler.compileOpenGl(ShaderTranslator.createStencil().translateFragment(vertex, fragment), ShaderStage.FRAGMENT).remaining() > 0);
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
			for (final String translated : new String[] {ShaderTranslator.create().translateFragment(vertex, fragment), ShaderTranslator.createStencil().translateFragment(vertex, fragment)}) {
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
		Assert.assertTrue(ShaderTranslator.create().translateVertex(vertex, fragment).contains(block));
		Assert.assertTrue(ShaderTranslator.create().translateFragment(vertex, fragment).contains(block));
		Assert.assertTrue(ShaderTranslator.createStencil().translateFragment(vertex, fragment).contains(block));
	}

	@Test
	public void declaresTheNamedAttributesAndVaryings() {
		final ShaderSource vertex = ShaderSource.parse(ShaderStage.VERTEX, ShaderTranslatorTest.VERTEX);
		final ShaderSource fragment = ShaderSource.parse(ShaderStage.FRAGMENT, ShaderTranslatorTest.FRAGMENT);
		final String translatedVertex = ShaderTranslator.create().translateVertex(vertex, fragment);
		final String translatedFragment = ShaderTranslator.create().translateFragment(vertex, fragment);
		Assert.assertTrue(translatedVertex.contains("in vec3 aPosition;\n"));
		Assert.assertTrue(translatedVertex.contains("in vec2 aTexCoord;\n"));
		Assert.assertTrue(translatedVertex.contains("in vec4 aColor;\n"));
		Assert.assertFalse(translatedVertex.contains("aNormal"));
		Assert.assertFalse(translatedVertex.contains("layout(location"));
		Assert.assertTrue(translatedVertex.contains("out vec2 vTexCoord;\nflat out vec4 vColor;\n"));
		Assert.assertTrue(translatedFragment.contains("in vec2 vTexCoord;\nflat in vec4 vColor;\n"));
		Assert.assertTrue(translatedFragment.contains("layout(location = 0) out vec4 fragColor;\n"));
	}

	@Test
	public void declaresTheStencilSamplerAfterTheShaderSamplers() {
		final ShaderSource vertex = ShaderSource.parse(ShaderStage.VERTEX, ShaderTranslatorTest.VERTEX);
		final ShaderSource fragment = ShaderSource.parse(ShaderStage.FRAGMENT, ShaderTranslatorTest.FRAGMENT);
		final String translated = ShaderTranslator.create().translateFragment(vertex, fragment);
		Assert.assertTrue(translated.indexOf("uniform sampler2D tex;\n") < translated.indexOf("uniform sampler2D joid_Stencil;\n"));
		Assert.assertEquals(List.of("tex"), ShaderTranslator.create().getSamplers(vertex, fragment).stream().map(ShaderVariable::getName).toList());
	}

	@Test
	public void writesTheStencilOnlyInTheStencilVariant() {
		final ShaderSource vertex = ShaderSource.parse(ShaderStage.VERTEX, ShaderTranslatorTest.VERTEX);
		final ShaderSource fragment = ShaderSource.parse(ShaderStage.FRAGMENT, ShaderTranslatorTest.FRAGMENT);
		Assert.assertFalse(ShaderTranslator.create().translateFragment(vertex, fragment).contains("joid_stencilApply(joid_stencilCompare"));
		Assert.assertTrue(ShaderTranslator.create().translateFragment(vertex, fragment).contains("if (joid_StencilTest != 0 && !joid_stencilCompare(joid_stencilValue())) {"));
		Assert.assertTrue(ShaderTranslator.createStencil().translateFragment(vertex, fragment).contains("value = joid_stencilApply(joid_stencilCompare(value) ? joid_StencilPass : joid_StencilFail, value);"));
	}

	@Test
	public void keepsTheLineNumbersOfTheBody() {
		final ShaderSource vertex = ShaderSource.parse(ShaderStage.VERTEX, ShaderTranslatorTest.VERTEX);
		final ShaderSource fragment = ShaderSource.parse(ShaderStage.FRAGMENT, ShaderTranslatorTest.FRAGMENT);
		final String translated = ShaderTranslator.create().translateFragment(vertex, fragment);
		final String[] lines = translated.substring(translated.indexOf("#line 1\n") + "#line 1\n".length()).split("\n", -1);
		Assert.assertEquals("    fragColor = texture(tex, (u_Transform * vec3(vTexCoord, 1.0)).xy) * vColor * vec4(u_Tint, uLighting ? 1.0 : 0.5);", lines[7]);
	}

	private static String translate(final String name) {
		final ShaderSource vertex = CoreShaders.read(name, ShaderStage.VERTEX);
		final ShaderSource fragment = CoreShaders.read(name, ShaderStage.FRAGMENT);
		return ShaderTranslator.create().translateVertex(vertex, fragment) + ShaderTranslator.create().translateFragment(vertex, fragment) + ShaderTranslator.createStencil().translateFragment(vertex, fragment);
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