package com.rayTracing.RayTracing;

public class Light {
	
	public Position position;
	public float red;
	public float green;
	public float blue;
	public float specular_intensity; // to multiply diffuse color
	public float shadow_intensity;
	public float radius;
	
	public Light(Position position, float red, float green, float blue, float specular_intensity, float shadow_intensity,
			float width_radius) {
		super();
		this.position = position;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.specular_intensity = specular_intensity;
		this.shadow_intensity = shadow_intensity;
		this.radius = width_radius;
	}
}
