package RayTracing;

public class Sphere extends Surface {
	
	public Position center;
	public float radius;
	
	public Sphere(Position pos, float radius, int material_index) {
		super();
		this.center = pos;
		this.radius = radius;
		super.material_index = material_index;
	}
	
	public double isIntersect(Ray ray, Position Cam){
		Vector L = Vector.SubVectors(center,Cam);
		double Tca = Vector.DotProduct(L, ray.direction);
		if(Tca<0){
			return -1;
		}
		double doubleD = Vector.DotProduct(L, L) - Math.pow(Tca, 2);
		if(doubleD > Math.pow(this.radius, 2)){
			return -1;
		}
		double Thc = Math.sqrt( Math.pow(this.radius, 2)- doubleD);
		super.exit = Tca+ Thc;
		return (Tca-Thc)+RayTracer.epsilon;
	}
	
	@Override
	public Vector calculateNormalAtPosition(Position pos, Position camera) {
		return Vector.SubVectors(pos, this.center).normalize();
	}
}
