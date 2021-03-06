package geogebra3D.euclidian3D.opengl;



import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.GLProfile;



import javax.media.opengl.GL2; 
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.GLBuffers;


public class RendererJogl {
	
	protected GL2 gl2; 
	
	public GL getGL(){
		
		return gl2; 
	}
	
	public GL2 getGL2(){
		
		return gl2; 
	}
	
	
	
	public void setGL(GLAutoDrawable gLDrawable){		
		gl2 = gLDrawable.getGL().getGL2();
	}
	
	
	
	private javax.media.opengl.GL2ES2 gl2es2; 
	
	
	/**
	 * 
	 * @return current GL (as GL2ES2)
	 */
	public javax.media.opengl.GL2ES2 getGL2ES2(){
		
		return gl2es2; 
	}
	
	
	
	public void setGL2ES2(GLAutoDrawable gLDrawable){		
		gl2es2 = gLDrawable.getGL().getGL2ES2();
	}
	
	

	public final static IntBuffer newIntBuffer(int size){
		return GLBuffers.newDirectIntBuffer(size); 
	}
	
	public final static ByteBuffer newByteBuffer(int size){
		return GLBuffers.newDirectByteBuffer(size); 
	}

	public interface GLlocal extends GL2{}

	public interface GL2ES2 extends javax.media.opengl.GL2ES2{}
	
	public static GLCapabilities caps;
	

	final static public void initCaps(){
		
		
		GLProfile.initSingleton(); 
		
		//caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		System.out.println("GLProfile -- isAnyAvailable = "+GLProfile.isAnyAvailable()+" -- isAWTAvailable = "+GLProfile.isAWTAvailable());
				
		GLProfile profile = GLProfile.getDefault();
		System.out.println("profile -- is GL2 = " + profile.isGL2()+" -- isHardwareRasterizer = "+ profile.isHardwareRasterizer());
		caps = new GLCapabilities(profile);
		
		
		//caps.setAlphaBits(8);

		
		//anti-aliasing
		caps.setSampleBuffers(true);
		caps.setNumSamples(4);    	
		
		//avoid flickering
		caps.setDoubleBuffered(true);
		//caps.setDoubleBuffered(false);
		
		//stencil buffer is needed for hacked passive 3D
		caps.setStencilBits(1);
		

	}
	
	final public static String getGLInfos(GLAutoDrawable drawable){
		
		GL gl = drawable.getGL(); 

		GLCapabilitiesImmutable c = drawable.getChosenGLCapabilities();
		
		return "Init on "+Thread.currentThread()
				+"\nChosen GLCapabilities: " + c
				+"\ndouble buffered: " + c.getDoubleBuffered()
				+"\nstereo: " + c.getStereo()
				+"\nstencil: " + c.getStencilBits()
				+"\nINIT GL IS: " + gl.getClass().getName()
				+"\nGL_VENDOR: " + gl.glGetString(GL.GL_VENDOR)
				+"\nGL_RENDERER: " + gl.glGetString(GL.GL_RENDERER)
				+"\nGL_VERSION: " + gl.glGetString(GL.GL_VERSION);
		
	}
	
	
	
	static private boolean useCanvas;
	
	/**
	 * 
	 * @param useCanvas0 says if we use Canvas or JPanel
	 * @return 3D component
	 */
	static public Component3D createComponent3D(boolean useCanvas0){
		
		useCanvas = useCanvas0;
		
		if(useCanvas){
			return new ComponentGLCanvas();
		}else{
			return new ComponentGLJPanel();
		}
		
	}
	

	static public Animator createAnimator(Component3D canvas, int i){

		if(useCanvas){
			return new AnimatorCanvas((GLCanvas) canvas, i);
		}else{
			return new AnimatorJPanel((GLJPanel) canvas, i);	
		}

		
	}
	
	
	/////////////////////////
	// 3D Component
	
	
	private static class ComponentGLJPanel extends GLJPanel implements Component3D{ 
		
		public ComponentGLJPanel(){
			super(caps);
		}
		
	}
	
	private static class ComponentGLCanvas extends GLCanvas implements Component3D{ 
		
		public ComponentGLCanvas(){
			super(caps);
		}
		
	}
	
	/////////////////////////
	// 3D Animator
	
	
	private static class AnimatorJPanel extends FPSAnimator implements Animator{ 
		
		public AnimatorJPanel(GLJPanel canvas, int i){
			super(canvas,i);
		}
		
	}
	
	private static class AnimatorCanvas extends FPSAnimator implements Animator{ 
		
		public AnimatorCanvas(GLCanvas canvas, int i){
			super(canvas,i);
		}
		
	}
	
	
	
	
	
	/////////////////////////
	// JOGL Version
	
	
	
	final public static String JOGL_VERSION="JOGL2";
	
}
