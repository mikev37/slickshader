package example;



import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

import org.newdawn.slick.SlickException;

import shader.MultiTex;
import shader.Shader;


/**
 * Simple example/test class for shader support stuff. 
 * @author Chronocide (Jeremy Klix)
 *
 */
public class Example extends BasicGame{
  
  private Shader mandelbrot;
  private Shader julia;
  private Shader wave;
  private Shader multi;
  
  private Image img;
  private MultiTex img2;
  
  private float[] quad1;
  private float[] quad2;
  
  private float cX, mx;
  private float cY, my;
  
  private float zoom = 1.0f;
  private float dx, dy = 0.0f;
  
  private float shift = 1.0f;
  
  public Example(String title){
    super(title);
  }
  
  

  @Override
  public void init(GameContainer gc) throws SlickException{
    //String s= GL11.glGetString(GL11.GL_EXTENSIONS);
    //System.out.println(s);
    //System.out.println();
    
    mandelbrot = Shader.makeShader("data/fractal.vrt", "data/mandelbrot.frg");
    julia = Shader.makeShader("data/fractal.vrt", "data/julia.frg");
    wave = Shader.makeShader("data/wave.vrt", "data/wave.frg");
    multi = Shader.makeShader("data/multiTex.vrt", "data/multiTex.frg");
    
    img = new Image("data/base.png");
    img2 = new MultiTex("data/base.png", "data/normal.png");

    
    quad1 = new float[]{0, 0, 0,
                        0, 2, 0,
                        3, 2, 0,
                        3, 0, 0};
    quad2 = new float[]{0, 0, 0,
                        0, 1, 0,
                        1, 1, 0,
                        1, 0, 0};
  }

  
  
  @Override
  public void update(GameContainer gc, int delta) throws SlickException{
    mx = gc.getInput().getMouseX();
    my = gc.getInput().getMouseY();
    cX=(mx/400.0f)-2.0f;
    cY=(my/400.0f)-1.0f;
    
    if(gc.getInput().isKeyDown(Input.KEY_LEFT)){
      dx -= (0.0005f*delta)/zoom;
    }else if(gc.getInput().isKeyDown(Input.KEY_RIGHT)){
      dx += (0.0005f*delta)/zoom;
    }
    if(gc.getInput().isKeyDown(Input.KEY_UP)){
      dy += (0.0005f*delta)/zoom;
    }else if(gc.getInput().isKeyDown(Input.KEY_DOWN)){
      dy -= (0.0005f*delta)/zoom;
    }
    
    if(gc.getInput().isKeyDown(Input.KEY_ADD)){
      zoom = zoom*1.01f; 
    }else if(gc.getInput().isKeyDown(Input.KEY_SUBTRACT)){
      zoom = zoom/1.01f;
      if(zoom<1.00f){
        zoom = 1.00f;
      }
    }
    
    shift += delta/150.0f;
  }

  
  
  public void render(GameContainer gc, Graphics g)
      throws SlickException{
    g.scale(400, 400);
    
    mandelbrot.startShader();
      GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(-2.0f/zoom + dx, 1.0f/zoom + dy);
        GL11.glVertex3f(quad1[0],  quad1[1],  quad1[2]);
        
        GL11.glTexCoord2f(-2.0f/zoom + dx, -1.0f/zoom + dy);
        GL11.glVertex3f(quad1[3],  quad1[4],  quad1[5]);
        
        GL11.glTexCoord2f(1.0f/zoom + dx, -1.0f/zoom + dy);
        GL11.glVertex3f(quad1[6],  quad1[7],  quad1[8]);
        
        GL11.glTexCoord2f(1.0f/zoom + dx, 1.0f/zoom + dy);
        GL11.glVertex3f(quad1[9], quad1[10], quad1[11]);
      GL11.glEnd();
    
    

    
    julia.startShader();
      julia.setUniformFVariable("cX\0", cX/zoom + dx);
      julia.setUniformFVariable("cY\0", cY/zoom + dy);
      GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(-1.75f, 1.5f);
        GL11.glVertex3f(quad2[0],  quad2[1],  quad2[2]);
        
        GL11.glTexCoord2f(-1.75f, -1.5f);
        GL11.glVertex3f(quad2[3],  quad2[4],  quad2[5]);
        
        GL11.glTexCoord2f(1.75f, -1.5f);
        GL11.glVertex3f(quad2[6],  quad2[7],  quad2[8]);
        
        GL11.glTexCoord2f(1.75f, 1.5f);
        GL11.glVertex3f(quad2[9], quad2[10], quad2[11]);
      GL11.glEnd();
    
    
    g.scale(1.0f/400.0f, 1.0f/400.0f);
    
    
    wave.startShader();
      wave.setUniformFVariable("offset\0", shift);
      img.draw(800,500);
    
      multi.startShader();
        multi.setUniform2fVariable("lighting\0", (mx-200)/200.00f, (my-20)/200.0f);
        multi.setUniformIVariable("colorMap\0", 0);
        multi.setUniformIVariable("normalMap\0", 1);
        img2.draw(200, 20);

    Shader.forceFixedShader();

  }


  
  public static void main(String[] args){
    Example t = new Example("Psuedo Texture Example");
    
    try{
      AppGameContainer agc = new AppGameContainer(t, 1200, 800, false);
      agc.start();
    }catch(SlickException e){
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
