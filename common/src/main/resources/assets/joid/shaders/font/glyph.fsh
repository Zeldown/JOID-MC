in vec2 vTexCoord;
in vec4 vColor;
in vec2 vPosition;

uniform sampler2D glyphs;
uniform vec4 color;
uniform int grayscale;

uniform int u_HasGradient;
uniform vec4 u_GradientStart;
uniform vec4 u_GradientEnd;
uniform vec2 u_GradientStartPos;
uniform vec2 u_GradientEndPos;
uniform vec4 u_GradientCanvas;

vec2 snappedPosition() {
    return floor(vPosition * 256.0 + 0.5) / 256.0;
}

vec4 resolveColor() {
    if (u_HasGradient == 0) {
        return color;
    }

    vec2 normalizedPos;
    if (u_GradientCanvas.z > u_GradientCanvas.x && u_GradientCanvas.w > u_GradientCanvas.y) {
        vec2 rectSize = vec2(u_GradientCanvas.z - u_GradientCanvas.x, u_GradientCanvas.w - u_GradientCanvas.y);
        normalizedPos = (snappedPosition() - u_GradientCanvas.xy) / rectSize;
    } else {
        normalizedPos = snappedPosition();
    }

    vec2 dir = u_GradientEndPos - u_GradientStartPos;
    float t = 0.0;
    if (length(dir) > 0.001) {
        t = dot(normalizedPos - u_GradientStartPos, dir) / dot(dir, dir);
        t = clamp(t, 0.0, 1.0);
    }

    return mix(u_GradientStart, u_GradientEnd, t);
}

void main() {
    vec4 texel = texture(glyphs, vTexCoord);
    if (grayscale == 1) {
        texel = texel.rrrr;
    }

    if (texel.a < 0.1) {
        discard;
    }

    fragColor = texel * resolveColor() * vColor;
}