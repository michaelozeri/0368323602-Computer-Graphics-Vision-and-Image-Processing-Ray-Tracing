package RayTracing;

public class Material {
	
	//diffuse color
	public float dr,dg,db;
	//specular color
	public float sr,sg,sb;
	//reflection color
	public float rr,rg,rb;
	public float  phong_specularity_coefficient;
	public float  transparency; // 0 - not transparent, 1 - fully transparent
	public int incidence; 
	
	public Material(float dr, float dg, float db, float sr, float sg, float sb, float rr, float rg, float rb,
			float phong_specularity_coefficient, float transparency) {
		super();
		this.dr = dr;
		this.dg = dg;
		this.db = db;
		this.sr = sr;
		this.sg = sg;
		this.sb = sb;
		this.rr = rr;
		this.rg = rg;
		this.rb = rb;
		this.phong_specularity_coefficient = phong_specularity_coefficient;
		this.transparency = transparency;
		this.incidence = 1; 
	}

}
