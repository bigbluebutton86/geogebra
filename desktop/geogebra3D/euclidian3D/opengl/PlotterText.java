package geogebra3D.euclidian3D.opengl;

import geogebra3D.euclidian3D.opengl.RendererJogl.GLlocal;


/*
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;
*/

/**
 * Class that manages text rendering
 * 
 * @author matthieu
 *
 */
public class PlotterText {
	
	/** geometry manager */
	private Manager manager;
	

	
	
	/**
	 * common constructor
	 * @param manager
	 */
	public PlotterText(Manager manager){
		
		this.manager = manager;
		
	}
	
	
	/**
	 * draws a rectangle
	 * @param x
	 * @param y
	 * @param z
	 * @param width
	 * @param height
	 */
	public void rectangle(int x, int y, int z, int width, int height){
		
		manager.startGeometry(Manager.QUADS);
		manager.texture(0, 0);
		manager.vertexInt(x,y,z); 
		manager.texture(1, 0);
		manager.vertexInt(x+width,y,z); 
		manager.texture(1, 1);
		manager.vertexInt(x+width,y+height,z); 
		manager.texture(0, 1);
		manager.vertexInt(x,y+height,z); 	
		manager.endGeometry();
		
	}
	
	
	public void rectangleBounds(int x, int y, int z, int width, int height){

		manager.startGeometry(GLlocal.GL_LINE_LOOP);
		manager.vertexInt(x,y,z); 
		manager.vertexInt(x+width,y,z); 
		manager.vertexInt(x+width,y+height,z); 
		manager.vertexInt(x,y+height,z); 	
		manager.endGeometry();
		
	}
	
}
