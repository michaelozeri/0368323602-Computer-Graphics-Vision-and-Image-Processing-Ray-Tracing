package RayTracing;


public class Scene {
	
	public float bgr;
	public float bgg;
	public float bgb;
	public int shadow_rays_num;
	public int max_recursion;
	public int super_sampling_level;
	
	public Scene(float bgr, float bgg, float bgb, int shadow_rays_num, int max_recursion, int super_sampling_level) {
		super();
		this.bgr = bgr;
		this.bgg = bgg;
		this.bgb = bgb;
		this.shadow_rays_num = shadow_rays_num;
		this.max_recursion = max_recursion;
		this.super_sampling_level = super_sampling_level;
	}
	
	
}
