package RayTracing;

public class Camera {
	
	public Position position;
	public Vector look_at;
	public Vector up_vector;
	public float screen_distance;
	public float screen_width;
	
	public Camera(Position position, Vector look_at, Vector up_vector, float screen_distance, float screen_width) {
		super();
		this.position = position;
		this.look_at = look_at;
		this.up_vector = up_vector;
		this.screen_distance = screen_distance;
		this.screen_width = screen_width;
	}
	
	/**
	 * Computes camera direction
	 * @return the vector towards = camera direction
	 */
	public Vector normalVector(){
		Vector towards = Vector.SubVectors(look_at,position);
		return towards.normalize();
	}
	
	/**
	 * return right vector of camera - normlized
	 */
	public Vector Right(){
		Vector towards = this.normalVector();
		return Vector.crossProduct(up_vector, towards).normalize();
	}

}
