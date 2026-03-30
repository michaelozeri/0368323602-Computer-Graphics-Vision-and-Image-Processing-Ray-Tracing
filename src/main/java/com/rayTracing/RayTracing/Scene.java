package com.rayTracing.RayTracing;

public record Scene(
        float bgr,
        float bgg,
        float bgb,
        int shadow_rays_num,
        int max_recursion,
        int super_sampling_level
) {
}
