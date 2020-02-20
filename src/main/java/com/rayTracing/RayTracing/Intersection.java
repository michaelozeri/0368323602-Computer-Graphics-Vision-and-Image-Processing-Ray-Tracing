package com.rayTracing.RayTracing;

public class Intersection {
	double distance;
	Surface surface;
	
	public Intersection(double min_t, Surface min_primitive) {
		this.surface = min_primitive;
		this.distance = min_t;

	}

	public static double Intersect(Ray ray, Surface primitive, Position cam) {
		return primitive.isIntersect(ray, cam);
	}


}
