package com.rayTracing.RayTracing;

public abstract class Surface {
	public boolean used;

	public int material_index;
	public double exit;
	public abstract double isIntersect(Ray ray, Position Cam);
	
	public abstract Vector calculateNormalAtPosition(Position pos, Position camera);
	
	public double findTforIntersectWithPlane(Vector ray, Position p0, Vector planeNormal, double planeOffset){
		return -(Vector.DotProduct(p0, planeNormal)-planeOffset)/Vector.DotProduct(planeNormal, ray);
	}
	public boolean getUsed(){
		return this.used;
	}
	public void setUsed(boolean use){
		this.used = use;
	}
}
