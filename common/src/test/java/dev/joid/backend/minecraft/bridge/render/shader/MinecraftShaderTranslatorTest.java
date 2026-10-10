package dev.joid.backend.minecraft.bridge.render.shader;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import dev.joid.lib.bridge.render.shader.source.CoreShader;
import dev.joid.lib.bridge.render.shader.source.ShaderSource;
import dev.joid.lib.bridge.render.shader.source.ShaderStage;
import dev.joid.lib.bridge.render.shader.source.ShaderVariable;
import dev.joid.lib.bridge.render.state.StencilEmulation;
import dev.joid.test.shader.GlslCompiler;

public class MinecraftShaderTranslatorTest {

	private static final String VERTEX   = "out vec2 vTexCoord;\nflat out vec4 vColor;\nuniform float u_Scale;\nuniform vec4 u_Colors[4];\n\nvoid main() {\n    vTexCoord = aTexCoord * u_Scale;\n    vColor = aColor * u_Colors[1];\n    gl_Position = uProjectionMatrix * uModelViewMatrix * vec4(aPosition, 1.0);\n}\n";
	private static final String FRAGMENT = "in vec2 vTexCoord;\nflat in vec4 vColor;\nuniform sampler2D tex;\nuniform vec3 u_Tint;\nuniform mat3 u_Transform;\n\nvoid main() {\n    fragColor = texture(tex, (u_Transform * vec3(vTexCoord, 1.0)).xy) * vColor * vec4(u_Tint, uLighting ? 1.0 : 0.5);\n}\n";

	@Test
	public void compilesEveryCoreShaderForVulkan() {
		for (final CoreShader shader : CoreShader.values()) {
			final ShaderSource vertex = shader.read(ShaderStage.VERTEX);
			final ShaderSource fragment = shader.read(ShaderStage.FRAGMENT);
			Assert.assertTrue(shader.name(), GlslCompiler.compileVulkan(MinecraftShaderTranslatorTest.color().translateVertex(vertex, fragment), ShaderStage.VERTEX).remaining() > 0);
			Assert.assertTrue(shader.name(), GlslCompiler.compileVulkan(MinecraftShaderTranslatorTest.color().translateFragment(vertex, fragment), ShaderStage.FRAGMENT).remaining() > 0);
			Assert.assertTrue(shader.name(), GlslCompiler.compileVulkan(MinecraftShaderTranslatorTest.color().translate(vertex, fragment).getStencilFragment(), ShaderStage.FRAGMENT).remaining() > 0);
		}
	}

	@Test
	public void compilesEveryCoreShaderForOpenGl() {
		for (final CoreShader shader : CoreShader.values()) {
			final ShaderSource vertex = shader.read(ShaderStage.VERTEX);
			final ShaderSource fragment = shader.read(ShaderStage.FRAGMENT);
			Assert.assertTrue(shader.name(), GlslCompiler.compileOpenGl(MinecraftShaderTranslatorTest.color().translateVertex(vertex, fragment), ShaderStage.VERTEX).remaining() > 0);
			Assert.assertTrue(shader.name(), GlslCompiler.compileOpenGl(MinecraftShaderTranslatorTest.color().translateFragment(vertex, fragment), ShaderStage.FRAGMENT).remaining() > 0);
			Assert.assertTrue(shader.name(), GlslCompiler.compileOpenGl(MinecraftShaderTranslatorTest.color().translate(vertex, fragment).getStencilFragment(), ShaderStage.FRAGMENT).remaining() > 0);
		}
	}

	@Test
	public void translatesEveryCoreShaderTheSameWayTwice() {
		for (final CoreShader shader : CoreShader.values()) {
			Assert.assertEquals(shader.name(), MinecraftShaderTranslatorTest.translate(shader), MinecraftShaderTranslatorTest.translate(shader));
		}
	}

	@Test
	public void wrapsTheFragmentMainOfEveryCoreShader() {
		for (final CoreShader shader : CoreShader.values()) {
			final ShaderSource vertex = shader.read(ShaderStage.VERTEX);
			final ShaderSource fragment = shader.read(ShaderStage.FRAGMENT);
			for (final String translated : new String[] {MinecraftShaderTranslatorTest.color().translateFragment(vertex, fragment), MinecraftShaderTranslatorTest.color().translate(vertex, fragment).getStencilFragment()}) {
				Assert.assertEquals(shader.name(), 1, MinecraftShaderTranslatorTest.count(translated, "void\\s+joid_main\\s*\\(\\s*\\)"));
				Assert.assertEquals(shader.name(), 1, MinecraftShaderTranslatorTest.count(translated, "void\\s+main\\s*\\(\\s*\\)"));
				Assert.assertTrue(shader.name(), translated.contains("\tjoid_main();\n"));
			}
		}
	}

	@Test
	public void sharesTheUniformBlockBetweenStages() {
		final ShaderSource vertex = ShaderSource.parse(ShaderStage.VERTEX, MinecraftShaderTranslatorTest.VERTEX);
		final ShaderSource fragment = ShaderSource.parse(ShaderStage.FRAGMENT, MinecraftShaderTranslatorTest.FRAGMENT);
		final String block = "layout(std140) uniform JoidUniforms {\n\tmat4 uProjectionMatrix;\n\tmat4 uModelViewMatrix;\n\tbool uLighting;\n\tint joid_AlphaTest;\n\tfloat joid_AlphaThreshold;\n\tint joid_StencilTest;\n\tint joid_StencilFunction;\n\tint joid_StencilReference;\n\tint joid_StencilMask;\n\tint joid_StencilFail;\n\tint joid_StencilPass;\n\tvec4 joid_CurrentColor;\n\tint joid_VertexColor;\n\tfloat u_Scale;\n\tvec4 u_Colors[4];\n\tvec3 u_Tint;\n\tmat3 u_Transform;\n};\n";
		Assert.assertTrue(MinecraftShaderTranslatorTest.color().translateVertex(vertex, fragment).contains(block));
		Assert.assertTrue(MinecraftShaderTranslatorTest.color().translateFragment(vertex, fragment).contains(block));
		Assert.assertTrue(MinecraftShaderTranslatorTest.color().translate(vertex, fragment).getStencilFragment().contains(block));
	}

	@Test
	public void declaresTheNamedAttributesAndVaryings() {
		final ShaderSource vertex = ShaderSource.parse(ShaderStage.VERTEX, MinecraftShaderTranslatorTest.VERTEX);
		final ShaderSource fragment = ShaderSource.parse(ShaderStage.FRAGMENT, MinecraftShaderTranslatorTest.FRAGMENT);
		final String translatedVertex = MinecraftShaderTranslatorTest.color().translateVertex(vertex, fragment);
		final String translatedFragment = MinecraftShaderTranslatorTest.color().translateFragment(vertex, fragment);
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
		final ShaderSource vertex = ShaderSource.parse(ShaderStage.VERTEX, MinecraftShaderTranslatorTest.VERTEX);
		final ShaderSource fragment = ShaderSource.parse(ShaderStage.FRAGMENT, MinecraftShaderTranslatorTest.FRAGMENT);
		final String translated = MinecraftShaderTranslatorTest.color().translateFragment(vertex, fragment);
		Assert.assertTrue(translated.indexOf("uniform sampler2D tex;\n") < translated.indexOf("uniform sampler2D joid_Stencil;\n"));
		Assert.assertEquals(List.of("tex"), MinecraftShaderTranslatorTest.color().getSamplers(vertex, fragment).stream().map(ShaderVariable::getName).toList());
	}

	@Test
	public void writesTheStencilOnlyInTheStencilVariant() {
		final ShaderSource vertex = ShaderSource.parse(ShaderStage.VERTEX, MinecraftShaderTranslatorTest.VERTEX);
		final ShaderSource fragment = ShaderSource.parse(ShaderStage.FRAGMENT, MinecraftShaderTranslatorTest.FRAGMENT);
		Assert.assertFalse(MinecraftShaderTranslatorTest.color().translateFragment(vertex, fragment).contains("joid_stencilApply(joid_stencilCompare"));
		Assert.assertTrue(MinecraftShaderTranslatorTest.color().translateFragment(vertex, fragment).contains("if (joid_StencilTest != 0 && !joid_stencilCompare(joid_stencilValue())) {"));
		Assert.assertTrue(MinecraftShaderTranslatorTest.color().translate(vertex, fragment).getStencilFragment().contains("value = joid_stencilApply(joid_stencilCompare(value) ? joid_StencilPass : joid_StencilFail, value);"));
	}

	@Test
	public void keepsTheLineNumbersOfTheBody() {
		final ShaderSource vertex = ShaderSource.parse(ShaderStage.VERTEX, MinecraftShaderTranslatorTest.VERTEX);
		final ShaderSource fragment = ShaderSource.parse(ShaderStage.FRAGMENT, MinecraftShaderTranslatorTest.FRAGMENT);
		final String translated = MinecraftShaderTranslatorTest.color().translateFragment(vertex, fragment);
		final String[] lines = translated.substring(translated.indexOf("#line 1\n") + "#line 1\n".length()).split("\n", -1);
		Assert.assertEquals("    fragColor = texture(tex, (u_Transform * vec3(vTexCoord, 1.0)).xy) * vColor * vec4(u_Tint, uLighting ? 1.0 : 0.5);", lines[7]);
	}

	private static MinecraftShaderTranslator color() {
		return MinecraftShaderTranslator.create().stencil(StencilEmulation.Pass.TEST);
	}

	private static String translate(final CoreShader shader) {
		final ShaderSource vertex = shader.read(ShaderStage.VERTEX);
		final ShaderSource fragment = shader.read(ShaderStage.FRAGMENT);
		return MinecraftShaderTranslatorTest.color().translateVertex(vertex, fragment) + MinecraftShaderTranslatorTest.color().translateFragment(vertex, fragment) + MinecraftShaderTranslatorTest.color().translate(vertex, fragment).getStencilFragment();
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