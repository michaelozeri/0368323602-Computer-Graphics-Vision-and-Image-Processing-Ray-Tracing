package RayTracing;
import java.lang.Math;

public class Vector  extends Position{
		
	public Vector(double xcor, double ycor, double zcor) {
		super(xcor,ycor,zcor);
	}
	
	/**
	 * return Dot product of the two vectors v1, v2
	 * @param v1
	 * @param v2
	 * @return - integer - v1 Dot v2
	 */
	public static double DotProduct(Position v1,Position v2){
		return (v1.Xcor*v2.Xcor+v1.Ycor*v2.Ycor+v1.Zcor*v2.Zcor);
	}
	
	/**
	 * Compute Cross Product of two vectors v1 , v2
	 * @param v1
	 * @param v2
	 * @return - RayTraceVector - the cross product of those two vectors
	 */
	public static Vector crossProduct(Position v1, Position v2){
		return new Vector((v1.Ycor*v2.Zcor-v1.Zcor*v2.Ycor), (v1.Zcor*v2.Xcor-v1.Xcor*v2.Zcor), (v1.Xcor*v2.Ycor-v1.Ycor*v2.Xcor));
	}
	
	/**
	 * compute addition of two vectors
	 * @param v1
	 * @param v2
	 * @return - RayTraceVector - the component wise addition of v1 , v2
	 */
	public static Vector AddVectors (Position v1, Position v2){
		return new Vector(v1.Xcor+v2.Xcor, v1.Ycor+v2.Ycor, v1.Zcor+v2.Zcor);
	}
	/**
	 * compute substitute of two vectors
	 * @param v1
	 * @param v2
	 * @return - RayTraceVector - the component wise addition of v1 , v2
	 */
	public static Vector SubVectors (Position v1, Position v2){
		return new Vector(v1.Xcor-v2.Xcor, v1.Ycor-v2.Ycor, v1.Zcor-v2.Zcor);
	}
	/**
	 * compute the scalar multiplication of v with scalar lambda
	 * @param v
	 * @param lambda
	 * @return - RayTraceVector - the scalar multiplication of v with scalar lambda
	 */
	public static Vector ScalarMultiply(Position v, double lambda){
		return new Vector(v.Xcor*(float)lambda, v.Ycor*(float)lambda, v.Zcor*(float)lambda);
	}
	
	/**
	 * Compute component multiplication of v1 & v2
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static Vector ComponentMultiply(Position v1, Position v2){
		return new Vector(v1.Xcor*v2.Xcor, v1.Ycor*v2.Ycor, v1.Zcor*v2.Zcor);
	}
	
	/**
	 * Compute Magnitude of vector v
	 * @param v
	 * @return double representing ||v|| = sqrt(v Dot V)
	 */
	public static double Magnitude(Position v){ //TODO: maybe this should be a field generated in constructor
		return Math.sqrt(Vector.DotProduct(v, v));
	}
	
	public static double findZRight(double x, double y, Vector v){
		x = v.Xcor * x;
		y = v.Ycor * y;
		return (-x-y)/v.Zcor;	
	}
	
	/**
	 * this func calculates the theta for the ray construction process
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double findTanThetaForRayConstruction(Position v1, Position v2, double imageWidth,double distance)
	{
		return (imageWidth/2)/distance;	
	}
	
	/**
	 * Normalize this vector size to be ||v|| = 1
	 */
	public Vector normalize(){
		double size = Magnitude(this);
		this.Xcor /= size;
		this.Ycor /= size;
		this.Zcor /= size;
		return this;
	}


}
