package RayTracing;

public class Triangle  extends Surface{
	
	public Position vertex1;
	public Position vertex2;
	public Position vertex3;
	
	public Triangle(Position vertex1, Position vertex2, Position vertex3, int material_index) {
		super();
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.vertex3 = vertex3;
		super.material_index = material_index;
		super.used = false;
	}
	public double isIntersect(Ray ray, Position Cam){
		Vector camera = new Vector(ray.position.Xcor,ray.position.Ycor, ray.position.Zcor);
		Vector t1 = new Vector(this.vertex1.Xcor,this.vertex1.Ycor, this.vertex1.Zcor);
		Vector t2 = new Vector(this.vertex2.Xcor,this.vertex2.Ycor, this.vertex2.Zcor);
		Vector t3 = new Vector(this.vertex3.Xcor,this.vertex3.Ycor, this.vertex3.Zcor);
		Vector norm = Vector.crossProduct(Vector.SubVectors(t3, t1), Vector.SubVectors(t3, t2));
		norm = norm.normalize();
		Vector orthogonal = Vector.crossProduct(norm, ray.direction);
		if(Vector.Magnitude(orthogonal) < RayTracer.epsilon){
			System.out.println("Cross is: ("+orthogonal.Xcor+","+orthogonal.Ycor+","+orthogonal.Zcor+") plane and ray not intersect");
			return -1;
		}
		/*
		if(Vector.DotProduct(ray.direction, norm)>0){
			norm = Vector.ScalarMultiply(norm,-1);
		}
		*/
		double d = Vector.DotProduct(norm, t1);
		double t = findTforIntersectWithPlane(ray.direction, ray.position, norm, d);
		Vector P = Vector.AddVectors(camera, Vector.ScalarMultiply(ray.direction, t));
		if(checkInTriangle(t1,t2, camera, P) == false)
			return -1;
		if(checkInTriangle(t3,t1, camera, P) == false)
			return -1;
		if(checkInTriangle(t2,t3, camera, P) == false)
			return -1;
		super.exit = t+ 2*RayTracer.epsilon;
		return t+RayTracer.epsilon;
		
	}
	private boolean checkInTriangle(Vector t1, Vector t2, Vector camera, Vector ray){
		Vector v1 = Vector.SubVectors(t1, camera);
		Vector v2 = Vector.SubVectors(t2, camera);
		Vector N = Vector.crossProduct(v2, v1);
		N = N.normalize();
		if((Vector.DotProduct(Vector.SubVectors(ray,camera), N) )<0)
			return false;
		return true;
	}
	
	@Override
	public Vector calculateNormalAtPosition(Position pos, Position camera) {
		Vector t1 = new Vector(this.vertex1.Xcor,this.vertex1.Ycor, this.vertex1.Zcor);
		Vector t2 = new Vector(this.vertex2.Xcor,this.vertex2.Ycor, this.vertex2.Zcor);
		Vector position = new Vector(pos.Xcor,pos.Ycor, pos.Zcor);
		t1 = Vector.SubVectors(position, t1);
		t2 = Vector.SubVectors(position, t2);
		Vector N = Vector.crossProduct(t2, t1);
		N = N.normalize();
		return N;

	}

	
	
}
