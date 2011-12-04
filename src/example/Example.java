package example;



import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
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
 *TODO add a shader that tests more of the things in Shader.java
 */
public class Example extends BasicGame{
  
  private Shader mandelbrot;
  private Shader julia;
  private Shader wave;
  private Shader multi;
  private Shader multi2;
  
  private Image img;
  private MultiTex img2;
  
  private float[] quad1;
  private float[] quad2;
  
  private float cX, mx;
  private float cY, my;
  
  private float zoom = 1.0f;
  private float dx, dy = 0.0f;
  
  private float shift = 1.0f;
  
  private int corner = 0; 
  
  public Example(String title){
    super(title);
  }
  
  

  @Override
  public void init(GameContainer gc) throws SlickException{    
    mandelbrot = Shader.makeShader("data/fractal.vrt", "data/mandelbrot.frg");
    julia = Shader.makeShader("data/fractal.vrt", "data/julia.frg");
    wave = Shader.makeShader("data/wave.vrt", "data/wave.frg");
//    multi = Shader.makeShader("data/multiTex.vrt", "data/multiTex.frg");
//    multi2 = Shader.makeShader("data/glLight_VertShader.vrt",
//                               "data/glLight_VertShader.frg");
    
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

    
    FloatBuffer fbpos = BufferUtils.createFloatBuffer(4);
    fbpos.put(new float[]{mx, my, my/25.0f, 0.0f}).flip();
    
    FloatBuffer spec = BufferUtils.createFloatBuffer(4);
    spec.put(new float[]{0.5f, 0.5f, 0.5f, 1.0f}).flip();
    
    FloatBuffer amb = BufferUtils.createFloatBuffer(4);
    amb.put(new float[]{0.25f, 0.25f, 0.25f, 1.0f}).flip();
    
    FloatBuffer diff = BufferUtils.createFloatBuffer(4);
    diff.put(new float[]{0.5f, 0.5f, 0.5f, 1.0f}).flip();
    
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_LIGHT0);
    GL11.glShadeModel(GL11.GL_SMOOTH);
    GL11.glLight(GL11.GL_LIGHT0,
                 GL11.GL_POSITION,
                 fbpos);
    GL11.glLight(GL11.GL_LIGHT0,
                 GL11.GL_SPECULAR,
                 spec);
    GL11.glLight(GL11.GL_LIGHT0,
                 GL11.GL_AMBIENT,
                 amb);
    GL11.glLight(GL11.GL_LIGHT0,
                 GL11.GL_DIFFUSE,
                 diff);
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
    
    if(gc.getInput().isKeyDown(Input.KEY_0)){
     corner = 0;
    }else if(gc.getInput().isKeyDown(Input.KEY_1)){
     corner = 1;
    }else if(gc.getInput().isKeyDown(Input.KEY_2)){
     corner = 2;
    }else if(gc.getInput().isKeyDown(Input.KEY_3)){
     corner = 3;
    }
      
    if(gc.getInput().isKeyDown(Input.KEY_R)){
      img2.setColour(corner, Color.red);
    }else if(gc.getInput().isKeyDown(Input.KEY_G)){
      img2.setColour(corner, Color.green);
    }else if(gc.getInput().isKeyDown(Input.KEY_B)){
      img2.setColour(corner, Color.blue);
    }else if(gc.getInput().isKeyDown(Input.KEY_W)){
      img2.setColour(corner, Color.white);
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
      julia.setUniformFloatVariable("cX", cX/zoom + dx);
      julia.setUniformFloatVariable("cY", cY/zoom + dy);
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
    
    

    
//    multi.startShader();
//      multi.setUniformFloatVariable("lighting", (mx-400)/200.00f, (my-20)/200.0f)
//           .setUniformIntVariable("colorMap\0", 0)
//           .setUniformIntVariable("normalMap\0", 1);
//      img2.draw(400, 20);

    wave.startShader();
      wave.setUniformFloatVariable("offset", shift);
      img.draw(1000,600);
        
    
    
//    FloatBuffer fbpos = BufferUtils.createFloatBuffer(4);
//    fbpos.put(new float[]{mx, my, my/25.0f, 0.0f}).flip();
//    
//    GL11.glEnable(GL11.GL_LIGHTING);
//    GL11.glEnable(GL11.GL_LIGHT0);
//    GL11.glShadeModel(GL11.GL_SMOOTH);
//    GL11.glLight(GL11.GL_LIGHT0,
//                 GL11.GL_POSITION,
//                 fbpos);
//
//    multi2.startShader();
//      multi2.setUniformIntVariable("colorMap\0", 0)
//            .setUniformIntVariable("normalMap\0", 1);
//      
//      img2.draw(80,240);
//    
//    GL11.glDisable(GL11.GL_LIGHTING);

    Shader.forceFixedShader();
    
    g.drawString("Use [0] [1] [2] [3] to select corner\nUse [R] [G] [B] [W] to change colour", 400, 185);
  }


  
  public static void main(String[] args){
    Example t = new Example("Psuedo Texture Example");
    
    try{
      AppGameContainer agc = new AppGameContainer(t, 1200, 800, false);
      agc.start();
    }catch(SlickException e){
      e.printStackTrace();
    }
  }
}
