#version 330

flat out ivec2 offset;

void main() {
    vec2 uv = vec2((gl_VertexID << 1) & 2, gl_VertexID & 2);
    int encoded = gl_VertexID >> 2;

    gl_Position = vec4(uv * vec2(2, 2) + vec2(-1, -1), 0, 1);
    offset = ivec2(encoded >> 14, encoded & 16383);
}
