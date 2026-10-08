#version 330

uniform sampler2D Sampler0;

flat in ivec2 offset;

out vec4 fragColor;

void main() {
    fragColor = texelFetch(Sampler0, ivec2(gl_FragCoord.xy) + offset, 0);
}
