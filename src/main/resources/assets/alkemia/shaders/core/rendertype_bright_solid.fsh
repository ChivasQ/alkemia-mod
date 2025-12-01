#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

vec3 rainbow(float t) {
    float r = abs(t * 6.0 - 3.0) - 1.0;
    float g = 2.0 - abs(t * 6.0 - 2.0);
    float b = 2.0 - abs(t * 6.0 - 4.0);
    return clamp(vec3(r, g, b), 0.0, 1.0);
}

void main() {
    vec4 baseColor = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;

    float timeShift = mod(gl_FragCoord.x + gl_FragCoord.y + gl_FragCoord.y * 0.5, 300.0) / 300.0;

    vec3 rainbowColor = rainbow(timeShift);

    baseColor.rgb = baseColor.rgb * rainbowColor;

    fragColor = linear_fog(baseColor, vertexDistance, FogStart, FogEnd, FogColor);
}
