package RayTracing;

import java.awt.Transparency;
import java.awt.color.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

/**
 *  Main class for ray tracing exercise.
 */
public class RayTracer {

	public int imageWidth;
	public int imageHeight;
	
	//added fields for the program
	public Scene m_scene;
	public Camera m_camera;
	public ArrayList<Light> m_lights;
	public ArrayList<Material> m_materials;
	public ArrayList<Surface> m_Surfaces;
	byte[] m_rgbData;
	public static double epsilon = 0.000001;
	
	/**
	 * for initializing the lists
	 */
	public RayTracer(){
		this.m_materials = new ArrayList<Material>();
		this.m_Surfaces = new ArrayList<Surface>();
		this.m_lights = new ArrayList<Light>();
	}

	/**
	 * Runs the ray tracer. Takes scene file, output image file and image size as input.
	 */
	public static void main(String[] args) {

		try {
			RayTracer tracer = new RayTracer();

            // Default values:
			tracer.imageWidth = 500;
			tracer.imageHeight = 500;

			if (args.length < 2){
				throw new RayTracerException("Not enough arguments provided. Please specify an input"
						+ " scene file and an output image file for rendering.");
			}

			String sceneFileName = args[0];
			String outputFileName = args[1];

			if (args.length > 3)
			{
				tracer.imageWidth = Integer.parseInt(args[2]);
				tracer.imageHeight = Integer.parseInt(args[3]);
			}


			// Parse scene file:
			tracer.parseScene(sceneFileName);

			// Render scene:
			tracer.renderScene(outputFileName);

		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (RayTracerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	
	/**
	 * Parses the scene file and creates the scene. Change this function so it generates the required objects.
	 * @param sceneFileName - the path to the scene file description
	 * @throws IOException
	 * @throws RayTracerException
	 */
	public void parseScene(String sceneFileName) throws IOException, RayTracerException
	{
		//TODO: do we need to initialize all our class lists here?
		FileReader fr = new FileReader(sceneFileName);
		BufferedReader bfr = new BufferedReader(fr);
		String line = null;
		int lineNum = 0;
		System.out.println("Started parsing scene file " + sceneFileName);

		while ((line = bfr.readLine()) != null)
		{
			line = line.trim();
			++lineNum;

			if (line.isEmpty() || (line.charAt(0) == '#'))
			{  // This line in the scene file is a comment
				continue;
			}
			else
			{
				String code = line.substring(0, 3).toLowerCase();
				// Split according to white space characters:
				String[] params = line.substring(3).trim().toLowerCase().split("\\s+");

				if (code.equals("cam"))
				{
					this.m_camera = new Camera(new Position(Float.parseFloat(params[0]), Float.parseFloat(params[1]), Float.parseFloat(params[2])),
							new Vector(Float.parseFloat(params[3]), Float.parseFloat(params[4]), Float.parseFloat(params[5])),
							new Vector(Float.parseFloat(params[6]), Float.parseFloat(params[7]),Float.parseFloat(params[8])),
							Float.parseFloat(params[9]),
							Float.parseFloat(params[10]));
					System.out.println(String.format("Parsed camera parameters (line %d)", lineNum));
				}
				else if (code.equals("set"))
				{
					this.m_scene = new Scene(Float.parseFloat(params[0]),Float.parseFloat(params[1]),Float.parseFloat(params[2]),
							Integer.parseInt(params[3]),
							Integer.parseInt(params[4]),
							Integer.parseInt(params[5]));
					System.out.println(String.format("Parsed general settings (line %d)", lineNum));
				}
				else if (code.equals("mtl"))
				{
					Material material = new Material(Float.parseFloat(params[0]),Float.parseFloat(params[1]),Float.parseFloat(params[2]),
							Float.parseFloat(params[3]),Float.parseFloat(params[4]),Float.parseFloat(params[5]),
							Float.parseFloat(params[6]),Float.parseFloat(params[7]),Float.parseFloat(params[8]),
							Float.parseFloat(params[9]),
							Float.parseFloat(params[10]));
					this.m_materials.add(material);
					System.out.println(String.format("Parsed material (line %d)", lineNum));
				}
				else if (code.equals("sph"))
				{
					Sphere sp = new Sphere(new Position(Float.parseFloat(params[0]), Float.parseFloat(params[1]), Float.parseFloat(params[2])),
							Float.parseFloat(params[3]),
							Integer.parseInt(params[4])-1);
					this.m_Surfaces.add(sp);
					System.out.println(String.format("Parsed sphere (line %d)", lineNum));
				}
				else if (code.equals("pln"))
				{
					Plane p = new Plane(new Vector(Float.parseFloat(params[0]), Float.parseFloat(params[1]), Float.parseFloat(params[2])),
							Float.parseFloat(params[3]),
							Integer.parseInt(params[4])-1);
					this.m_Surfaces.add(p);
					System.out.println(String.format("Parsed plane (line %d)", lineNum));
				}
				else if (code.equals("trg"))
				{
					Triangle t = new Triangle(new Position(Float.parseFloat(params[0]), Float.parseFloat(params[1]), Float.parseFloat(params[2])),
							new Position(Float.parseFloat(params[3]), Float.parseFloat(params[4]), Float.parseFloat(params[5])),
							new Position(Float.parseFloat(params[6]), Float.parseFloat(params[7]), Float.parseFloat(params[8])),
							Integer.parseInt(params[9])-1);
					this.m_Surfaces.add(t);
					System.out.println(String.format("Parsed cylinder (line %d)", lineNum));
				}
				else if (code.equals("lgt"))
				{
                   Light l = new Light(new Position(Float.parseFloat(params[0]), Float.parseFloat(params[1]), Float.parseFloat(params[2])),
                		   Float.parseFloat(params[3]),
                		   Float.parseFloat(params[4]),
                		   Float.parseFloat(params[5]),
                		   Float.parseFloat(params[6]),
                		   Float.parseFloat(params[7]),
                		   Float.parseFloat(params[8]));
                   this.m_lights.add(l);
					System.out.println(String.format("Parsed light (line %d)", lineNum));
				}
				else
				{
					System.out.println(String.format("ERROR: Did not recognize object: %s (line %d)", code, lineNum));
				}
			}
		}

		//TODO: validate that the scene is o.k.
        // It is recommended that you check here that the scene is valid,
        // for example camera settings and all necessary materials were defined.

		System.out.println("Finished parsing scene file " + sceneFileName);
		bfr.close();
	}

	
	/**
	 * Renders the loaded scene and saves it to the specified file location.
	 * @param outputFileName - the path to the output file where it should be written
	 * @throws IOException //TODO: remove this throws and write image like skeleton 
	 */
	public void renderScene(String outputFileName) 
	{
		long startTime = System.currentTimeMillis();

		// Create a byte array to hold the pixel data:
		m_rgbData = new byte[this.imageWidth * this.imageHeight * 3];

        // Put your ray tracing code here!
        //
        // Write pixel color values in RGB format to rgbData:
        // Pixel [x, y] red component is in rgbData[(y * this.imageWidth + x) * 3]
        //            green component is in rgbData[(y * this.imageWidth + x) * 3 + 1]
        //             blue component is in rgbData[(y * this.imageWidth + x) * 3 + 2]
        //
        // Each of the red, green and blue components should be a byte, i.e. 0-255
		
		
		for (int i = 0; i < this.imageHeight; i++) 
		{
			for (int j = 0; j < this.imageWidth; j++) 
			{
				//Construct ray from eye position through view plane
				Ray ray = ConstructRayThroughPixel(i, j);
				//Find first surface intersected by ray through pixel
				
				Intersection hit = FindIntersection(ray,m_camera.position, -2, true);
				while(hit.surface!= null && m_materials.get(hit.surface.material_index).transparency != 0){
					//Compute color of sample based on surface radiance
					GetColor(i,j,hit, ray.direction,m_camera.position,m_scene.max_recursion,1,1,1);
					hit.surface.setUsed(true);
					hit = FindIntersection(ray,m_camera.position, -2, true);
				}
				
				GetColor(i,j,hit, ray.direction,m_camera.position,m_scene.max_recursion,1,1,1);
				for(Surface surf : m_Surfaces)
					surf.setUsed(false);
			}
			if(i%100 == 0){
				System.out.println("finished for row: "+i);
			}
		}
		
		long endTime = System.currentTimeMillis();
		Long renderTime = endTime - startTime;

		// The time is measured for your own convenience, rendering speed will not affect your score
		// unless it is exceptionally slow (more than a couple of minutes)
		System.out.println("Finished rendering scene in " + renderTime.toString() + " milliseconds.");

		// This is already implemented, and should work without adding any code.
		saveImage(this.imageWidth, m_rgbData, outputFileName); 
		
		System.out.println("Saved file " + outputFileName);
	}
	
	/**
	 * Saves RGB data as an image in png format to the specified location.
	 */
	public static void saveImage(int width, byte[] rgbData, String fileName)
	{
		try {

			BufferedImage image = bytes2RGB(width, rgbData);
			ImageIO.write(image, "png", new File(fileName));

		} catch (IOException e) {
			System.out.println("ERROR SAVING FILE: " + e.getMessage());
		}

	}

	/**
	 * Producing a BufferedImage that can be saved as png from a byte array of RGB values.
	 */
	public static BufferedImage bytes2RGB(int width, byte[] buffer) {
	    int height = buffer.length / width / 3;
	    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
	    ColorModel cm = new ComponentColorModel(cs, false, false,
	            Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
	    SampleModel sm = cm.createCompatibleSampleModel(width, height);
	    DataBufferByte db = new DataBufferByte(buffer, width * height);
	    WritableRaster raster = Raster.createWritableRaster(sm, db, null);
	    BufferedImage result = new BufferedImage(cm, raster, false, null);

	    return result;
	}
	
	/**
	 * Constructs a Vector Ray for pixel i,j in the view plane
	 * @param i
	 * @param j
	 * @return - Vector - representing the Ray from the camera
	 */
	private Ray ConstructRayThroughPixel(int i,int j){
				
		double W = m_camera.screen_width;
		double H = W*(imageHeight/imageWidth);
		Vector temp1;
		Vector temp2;
		//normalized vector ||normalVector|| = 1
		Vector normalVector = m_camera.normalVector();
		//normalized vector ||right|| = 1
		Vector right = m_camera.Right();
		//calculate V such that ||V|| = 1
		Vector V = Vector.crossProduct(normalVector, right);
		//calculate center of image position
		temp1 = Vector.ScalarMultiply(normalVector, m_camera.screen_distance);
		Vector centerImagePosition = Vector.AddVectors(m_camera.position, temp1);
		//calculate top left vector 
		temp1 = Vector.ScalarMultiply(V, H/2);
		temp2 = Vector.ScalarMultiply(right, -W/2);
		Vector topLeft = Vector.AddVectors(centerImagePosition, temp1);
		topLeft = Vector.AddVectors(topLeft, temp2);
		
		//calculate the point coordinates
		temp1 = Vector.ScalarMultiply(right, i*W/imageWidth); 
		temp2 = Vector.ScalarMultiply(V, -j*H/imageHeight);
		Vector s =Vector.AddVectors(temp1, temp2);
		s = Vector.AddVectors(s, topLeft);
		Vector rayVectorDirection = Vector.SubVectors(s, m_camera.position).normalize();
		return new Ray(rayVectorDirection, m_camera.position);
	}
	
	/**
	 * finds an intersection between the ray given to all the surfaces given in the scene
	 * @param ray
	 * @return intersection
	 */
	private Intersection FindIntersection(Ray ray,Position camPosition, double distance, boolean isMain) { 
		double min_t = Double.MAX_VALUE;
		Surface min_primitive = null;
		for(Surface primitive : m_Surfaces){
			if(isMain==true && primitive.getUsed()==true)
				continue;
			double t = Intersection.Intersect(ray, primitive, camPosition);
			if(distance < 0){
				if (t < min_t && t != -1){
					min_primitive = primitive;
					min_t = t; 
				}
			}
			else{
				if (t < min_t && t != -1  && (m_materials.get(primitive.material_index).transparency == 0 || t == distance)){
					min_primitive = primitive;
					min_t = t+epsilon; 
					if(min_t<distance){
						return new Intersection(-1, null);
					}
				}
				
			}
		}
		return new Intersection(min_t, min_primitive);
	}
	
	/**
	 * sets the color for a pixel in the image after calculation all light factors
	 * @param intersectionData
	 * @return Color - the light to the pixel
	 */
	private void GetColor(int i,int j,Intersection intersectionData, Vector rayDirection,Position cameraPosition,int MaxRecursionLevel,float refRed,float refGreen,float refBlue){
		
		rayDirection.normalize();
		//if we didn't hit no surface we return the background color
		if(intersectionData.surface == null){
			insertColorIntoArray(i, j, m_scene.bgr*refRed, 0);
			insertColorIntoArray(i, j,m_scene.bgg*refGreen, 1);
			insertColorIntoArray(i, j,m_scene.bgb*refBlue, 2);	
			return;
		}
		
		Material surfaceMaterial = m_materials.get(intersectionData.surface.material_index);
		Vector hitPositionOnSurface = Vector.AddVectors(cameraPosition, Vector.ScalarMultiply(rayDirection, intersectionData.distance-epsilon));
		Vector normalAtHitPosition = intersectionData.surface.calculateNormalAtPosition(hitPositionOnSurface, cameraPosition).normalize(); //N
		
		
		//calculate background / transparency color 
		if(surfaceMaterial.transparency > 0){
			//construct new Ray - reduce epsilon of normal direction
			Ray transparencyRay = new Ray(rayDirection, Vector.AddVectors(cameraPosition, Vector.ScalarMultiply(rayDirection,intersectionData.surface.exit)));
			//Find first surface intersected by ray through pixel
			Intersection transparencyHit = FindIntersection(transparencyRay,transparencyRay.position,-2,false);
			if(transparencyHit.surface != null){
				//Compute color of sample based on surface radiance
				GetColor(i,j,transparencyHit, transparencyRay.direction,transparencyRay.position,MaxRecursionLevel,(1-surfaceMaterial.transparency)*refRed,(1-surfaceMaterial.transparency)*refGreen,(1-surfaceMaterial.transparency)*refBlue); //TODO: should we multiply in transparency reference color?
			}
			else{
				//insertColorIntoArray(i, j, (surfaceMaterial.transparency)*m_scene.bgr*refRed, 0);
				//insertColorIntoArray(i, j,(surfaceMaterial.transparency)*m_scene.bgg*refGreen, 1);
				//insertColorIntoArray(i, j,(surfaceMaterial.transparency)*m_scene.bgb*refBlue, 2);	
			}
		}
		
		//add values to each R,G,B
		double redPixelColor =  0;
		double greenPixelColor =  0;
		double bluePixelColor = 0;
		
		
		//for each light add its color it projects on 
		for(Light light : m_lights){
		
			Position lightPosition = light.position;
			Vector toLight = Vector.SubVectors(lightPosition,hitPositionOnSurface); //L - vector to Light
			
			double distanceFromLight = Vector.Magnitude(toLight); //distance from light
			
			if((!(intersectionData.surface instanceof Sphere)) && (Vector.DotProduct(normalAtHitPosition,toLight)<0)){
				normalAtHitPosition = Vector.ScalarMultiply(normalAtHitPosition, -1);
			}
			toLight.normalize();
			//check if light hits the object
			double minHitDistanceFromLight = FindIntersection(new Ray(Vector.ScalarMultiply(toLight, -1),lightPosition), lightPosition, distanceFromLight, false).distance;
			
			
			
			Vector R = Vector.SubVectors(Vector.ScalarMultiply(normalAtHitPosition, 2*Vector.DotProduct(toLight, normalAtHitPosition)), toLight).normalize();
			Vector V = Vector.ScalarMultiply(rayDirection, -1).normalize();
			//if we are the first object the light hits
			if(minHitDistanceFromLight-distanceFromLight >= -epsilon ){
				
				//calculating diffuse color
				if(Vector.DotProduct(normalAtHitPosition, toLight) > 0){
					redPixelColor += surfaceMaterial.dr* Vector.DotProduct(normalAtHitPosition, toLight)*light.red*(1-surfaceMaterial.transparency);
					greenPixelColor += surfaceMaterial.dg* Vector.DotProduct(normalAtHitPosition, toLight)*light.green*(1-surfaceMaterial.transparency);
					bluePixelColor += surfaceMaterial.db* Vector.DotProduct(normalAtHitPosition, toLight)*light.blue*(1-surfaceMaterial.transparency);
				}
				
				//calculating specular color
				if(Vector.DotProduct(V,R)>0){
					redPixelColor += surfaceMaterial.sr*Math.pow(Vector.DotProduct(V,R), surfaceMaterial.phong_specularity_coefficient)*(light.red*light.specular_intensity)*(1-surfaceMaterial.transparency);
					greenPixelColor += surfaceMaterial.sg*Math.pow(Vector.DotProduct(V,R), surfaceMaterial.phong_specularity_coefficient)*(light.green*light.specular_intensity)*(1-surfaceMaterial.transparency);
					bluePixelColor += surfaceMaterial.sb*Math.pow(Vector.DotProduct(V,R), surfaceMaterial.phong_specularity_coefficient)*(light.blue*light.specular_intensity)*(1-surfaceMaterial.transparency);
				}
				
				
			}
			
		}
		
		//calculating reflection color
		if(MaxRecursionLevel>0 && ((refRed > 0)||(refGreen>0)||(refBlue>0))){
			//construct new Ray
			Vector oppositeRayDirection = Vector.ScalarMultiply(rayDirection, -1).normalize();
			Vector recursionRayDirection = Vector.SubVectors(Vector.ScalarMultiply(normalAtHitPosition, 2*Vector.DotProduct(oppositeRayDirection, normalAtHitPosition)), oppositeRayDirection).normalize();
			Ray recursionRay = new Ray(recursionRayDirection, hitPositionOnSurface);
			//Find first surface intersected by ray through pixel
			Intersection recursionHit = FindIntersection(recursionRay,recursionRay.position,-2,false);
			if(recursionHit.surface != null){
				//Compute color of sample based on surface radiance
				GetColor(i,j,recursionHit, recursionRay.direction,recursionRay.position,MaxRecursionLevel-1,surfaceMaterial.rr*refRed,surfaceMaterial.rg*refGreen,surfaceMaterial.rb*refBlue);
			}
		}
		
		//updating byte array
		insertColorIntoArray(i, j, redPixelColor*refRed, 0);
		insertColorIntoArray(i, j, greenPixelColor*refGreen, 1);
		insertColorIntoArray(i, j, bluePixelColor*refBlue, 2);
		
	}
	
	public static class RayTracerException extends Exception {
		public RayTracerException(String msg) {  super(msg); }
	}
	
	public void insertColorIntoArray(int x,int y,double data,int colorNum){
		if(data>1.0)
			data=1;
	
		byte byteColor = (byte)(255*data);
		switch(colorNum){
			case 0:
				if(((m_rgbData[(y * imageWidth + x) * 3] & 0xFF) + (byteColor & 0xFF))  > 255){
					m_rgbData[(y * imageWidth + x) * 3] = (byte)255;				
				}
				else{
					m_rgbData[(y * imageWidth + x) * 3] += byteColor;
				}
			break;
			case 1:
				if(((m_rgbData[(y * imageWidth + x) * 3 + 1] & 0xFF) + (byteColor & 0xFF))  > 255){
					m_rgbData[(y * imageWidth + x) * 3 + 1] = (byte)255;				
				}
				else{
					m_rgbData[(y * imageWidth + x) * 3 + 1] += byteColor;
				}
			break;
			case 2:
				if(((m_rgbData[(y * imageWidth + x) * 3 + 2] & 0xFF) + (byteColor & 0xFF))  > 255){
					m_rgbData[(y * imageWidth + x) * 3 + 2] = (byte)255;				
				}
				else{
					m_rgbData[(y * imageWidth + x) * 3 + 2] += byteColor;
				}
			break;
		}
		}
	
}
