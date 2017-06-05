package RayTracing;

public class Plane extends Surface {
	
	public Vector normal;
	public float offset;
	
	public Plane(Vector normal, float offset, int material_index) {
		super();
		this.normal = normal.normalize();
		this.offset = offset;
		super.material_index = material_index;
		super.used = false;
	}
	
	public double isIntersect(Ray ray, Position cameraPosition){
		//check if plane is not parallel to ray
		Vector orthogonal = Vector.crossProduct(this.normal, ray.direction);
		if(Vector.Magnitude(orthogonal) < RayTracer.m_epsilon){
			return -1;
		}
		Vector norm = this.normal;
		double t = findTforIntersectWithPlane(ray.direction, ray.position,norm.normalize(),this.offset);
		
		if (t < 0 ){ 
			return -1;
		}
		super.exit = t+ 2*RayTracer.m_epsilon;
		return t + RayTracer.m_epsilon;
	}
	
	public Vector calculateNormalAtPosition(Position pos, Position camera){
		
		return this.normal;
	}
	
}
