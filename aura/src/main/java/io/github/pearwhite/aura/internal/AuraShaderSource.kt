package io.github.pearwhite.aura.internal

internal const val AURA_SHADER_SRC = """
uniform float2 resolution;
uniform float4 colors[8];
uniform int    colorCount;
uniform float  time;
uniform float  seed;
uniform float  coreRadius;
uniform float  edgeSoftness;
uniform float  glowIntensity;
uniform float  glowSpread;
uniform float  warpStrength;
uniform float  patternScale;
uniform float  shapeExponent;

half4 samplePalette(float t) {
    float x = clamp(t, 0.0, 1.0) * float(colorCount - 1);
    half4 result = half4(colors[0]);
    for (int i = 0; i < 7; i++) {
        if (i >= colorCount - 1) break;
        float k = smoothstep(float(i), float(i + 1), x);
        result = mix(result, half4(colors[i + 1]), half(k));
    }
    return result;
}

half4 main(float2 fragCoord) {
    float2 p = fragCoord / resolution * 2.0 - 1.0;
    p.x *= resolution.x / resolution.y;

    float2 a = abs(p);
    float dist = pow(pow(a.x, shapeExponent) + pow(a.y, shapeExponent), 1.0 / shapeExponent);

    float core  = 1.0 - smoothstep(coreRadius - edgeSoftness, coreRadius + edgeSoftness, dist);
    float glow  = exp(-dist * dist * glowSpread) * glowIntensity;
    float alpha = clamp(core + glow, 0.0, 1.0);

    float2 w = p;
    w.x += sin(p.y * 2.5 + time * 0.3 + seed) * warpStrength;
    w.y += cos(p.x * 2.5 - time * 0.4 - seed) * warpStrength;

    float t = 0.5
        + 0.25 * sin(w.x * patternScale + time * 0.5)
        + 0.25 * cos(w.y * patternScale - time * 0.3 + sin((w.x + w.y) * 4.5));

    half4 c = samplePalette(t);
    return half4(c.rgb * half(alpha), half(alpha));
}
"""
