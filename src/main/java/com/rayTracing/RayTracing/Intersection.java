package com.rayTracing.RayTracing;

public record Intersection(double distance, Surface surface) {

    public static double intersect(Ray ray, Surface primitive, Position cam) {
        return primitive.isIntersect(ray, cam);
    }
}
