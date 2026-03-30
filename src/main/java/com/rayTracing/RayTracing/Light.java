package com.rayTracing.RayTracing;

public record Light(
        Position position,
        float red,
        float green,
        float blue,
        float specular_intensity,
        float shadow_intensity,
        float radius
) {
}
