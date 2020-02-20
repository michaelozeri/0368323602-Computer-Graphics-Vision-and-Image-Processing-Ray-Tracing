package com.rayTracing.RayTracing;

public class Ray {
	
	public Vector direction; //V
	public Position position; // P0
	
	public Ray(Vector direction, Position position){
		if(Vector.Magnitude(direction) == 1){
			this.direction = direction;
		}
		else{
			this.direction = direction.normalize();
		}
		this.position = position;
		
	}

}
