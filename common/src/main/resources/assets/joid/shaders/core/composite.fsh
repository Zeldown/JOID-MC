#version 330

#moj_import <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

in vec4 vertexColor;

out vec4 fragColor;

void main() {
    fragColor = texelFetch(Sampler0, ivec2(gl_FragCoord.xy), 0) * vertexColor * ColorModulator;
}
