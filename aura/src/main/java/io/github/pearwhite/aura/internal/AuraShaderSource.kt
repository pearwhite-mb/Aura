package io.github.pearwhite.aura.internal

internal const val AURA_SHADER_SRC = """
uniform float2 resolution;
uniform float4 colors[8];
uniform float  bounds[8];    
uniform int    colorCount;
uniform float  colorBlend;
uniform float  time;
uniform float  seed;
uniform float  coreRadius;
uniform float  edgeSoftness;
uniform float  glowIntensity;
uniform float  glowSpread;
uniform float  warpStrength;
uniform float  patternScale;
uniform int   shapeMode;   
uniform float shapeParam;  

half4 samplePalette(float t) {
    half4 result = half4(0.0);
    float prev = 1.0;
    for (int i = 0; i < 8; i++) {
        if (i >= colorCount) break;
        float next = 0.0;
        if (i < colorCount - 1) {
            next = smoothstep(bounds[i] - colorBlend, bounds[i] + colorBlend, t);
        }
        result += half4(colors[i]) * half(prev - next);
        prev = next;
    }
    return result;
}

float coverage(float2 p) {
    if (shapeMode == 1) {
        return 1.0;
    }
    if (shapeMode == 2) {
        float2 h = resolution / min(resolution.x, resolution.y);
        float r = min(shapeParam, min(h.x, h.y));
        float2 q = abs(p) - h + float2(r);
        float d = length(max(q, float2(0.0))) + min(max(q.x, q.y), 0.0) - r;  // < 0 внутри
        return 1.0 - smoothstep(-2.0 * edgeSoftness, 0.0, d);
    }
    float2 a = abs(p);
    float dist = pow(pow(a.x, shapeParam) + pow(a.y, shapeParam), 1.0 / shapeParam);
    float core = 1.0 - smoothstep(coreRadius - edgeSoftness, coreRadius + edgeSoftness, dist);
    float fade = 1.0 - smoothstep(coreRadius, 1.0, dist);
    float glow = pow(fade, glowSpread) * glowIntensity;
    return core + (1.0 - core) * glow;
}

half4 main(float2 fragCoord) {
    float2 p = (fragCoord - 0.5 * resolution) / (0.5 * min(resolution.x, resolution.y));
    float alpha = coverage(p);

    float2 w = p;
    w.x += sin(p.y * 2.5 + time * 0.3 + seed) * warpStrength;
    w.y += cos(p.x * 2.5 - time * 0.4 - seed) * warpStrength;

    float u = 0.5
        + 0.25 * sin(w.x * patternScale + time * 0.5)
        + 0.25 * cos(w.y * patternScale - time * 0.3 + sin((w.x + w.y) * 4.5));
    float t = mix(u, smoothstep(0.0, 1.0, u), 0.6);

    half4 c = samplePalette(t);
    return half4(c.rgb * half(alpha), half(alpha));
}
"""
