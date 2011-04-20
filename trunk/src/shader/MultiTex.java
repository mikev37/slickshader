package shader;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.newdawn.slick.Renderable;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.renderer.SGL;

/**
 * Class to support the concept of a single artifact being
 * comprised of multiple image resources.</br>
 * For example a colourmap, normalmap, diffusemap, and specularmap.
 * This is currently extremely buggy, and I don't know why some
 * things have to be the way the are. 
 * @author Chronocide (Jeremy Klix)
 *
 */
//TODO Make interface feel a little more like the familiar Image class
//TODO Determine a method of dealing with the case were textures
//are not all the same size.  For instance should textures be
//stretched, tiled, clamped?
//TODO Way of handling images larger then the supporting cards
//max texture size ala Slicks BigImage class.
//TODO Needs way more attention to documenting inheritance.
//TODO Test using different numbers of textures.
public class MultiTex implements Renderable{
  private static int units = -1; 
  
  public List<Texture> textures;
  
  //Width and height based off first texture loaded
  private float imgWidth, imgHeight;
  //Texture width and height clamped between 0 and 1 
  private float texWidth, texHeight; 
  
  /**
   * Constructs a new <tt>MultiTex</tt> object using the textures
   * identified in <tt>textures</tt>.</br>
   * The index of the textures in the list will be the texture unit
   * that the texture is bound to.</br>
   * @param textures a list of paths to the textures to use.
   * @throws SlickException If <tt>textures.size()</tt> is greater
   * than the maximum number of texture units.
   */
  public MultiTex(List<String> textures)throws SlickException{
    //Check how many texture units are supported 
    if(units==-1){
      units = GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS);
      System.out.println(units);
    }
    if(units < textures.size()){
      throw new UnsupportedOperationException("You attempted to " +
      		"create an artifact with " + textures.size() +
      		" textures, but your environment only supports " +
      		units + " texure image units.");
    }
    
    //Create texture list
    this.textures = new ArrayList<Texture>(textures.size());
    
    
     
    //Load textures into texture list.
    InternalTextureLoader itl = InternalTextureLoader.get();
    for(int i = 0; i<textures.size(); i++){
      GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
      GL11.glEnable(GL11.GL_TEXTURE_2D);
      try{
        this.textures.add(itl.getTexture(textures.get(i),
                                         false,
                                         GL11.GL_LINEAR,
                                         null));
      }catch(IOException e){
        throw new SlickException(e.getMessage());
      }
      GL11.glDisable(GL11.GL_TEXTURE_2D);
    }
    //FIXME pretty sure there is a rather serious problem here.
    //Since the TextureLoader used keeps track of previously loaded
    //textures, and binds them to a unit at creation.  If a single
    //image is loaded twice to two different Texture Units, it may
    //not actually be associated with the correct unit on the
    //second load.  This is because the TextureLoader will simply
    //return the earlier loaded texture.
    
    //Reset current texture unit to 0
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    
    imgWidth  = this.textures.get(0).getImageWidth();
    imgHeight = this.textures.get(0).getImageHeight();
    texWidth  = this.textures.get(0).getWidth();
    texHeight = this.textures.get(0).getHeight();
  }
  
  
  
  public MultiTex(String[] textures) throws SlickException{
    this(Arrays.asList(textures));
  }
  
  
  
  public MultiTex(String t1, String t2)throws SlickException{
    this(new String[]{t1,t2});
  }
  
  
  
  /**
   * When extending please note that this method relies on the
   * private method drawEmbedded.</br>
   */
  public void draw(float x, float y){    
    GL11.glPushMatrix();
    GL11.glTranslatef(x, y, 0);
    
      //Bind textures to their correct locations
      for(int i = 0; i < textures.size(); i++){
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures.get(i).getTextureID());
      }      
  
      //Draw quad
      GL11.glBegin(SGL.GL_QUADS); 
        drawEmbedded(); 
      GL11.glEnd(); 
    
    GL11.glPopMatrix();
    
    //Clean up texture setting to allow basic slick to operate correctly.
    for(int i = textures.size()-1; i>=0; i--){
      GL13.glActiveTexture(GL13.GL_TEXTURE0+i);
      GL11.glDisable(GL11.GL_TEXTURE_2D);
    }
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }


  
  private void drawEmbedded(){
    //TODO reduce code duplication need to produce sequence 0,1,3,2
    for(int i=0; i<textures.size(); i++){
      GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0 + i, 0, 0);
    }
    GL11.glVertex3f(0, 0, 0); 
    
    for(int i=0; i<textures.size(); i++){
      GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0 + i, 0, texHeight);
    }
    GL11.glVertex3f(0, imgHeight, 0);
    
    for(int i=0; i<textures.size(); i++){
      GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0 + i, texWidth, texHeight);
    }
    GL11.glVertex3f(imgWidth, imgHeight, 0);
    
    for(int i=0; i<textures.size(); i++){
      GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0 + i, texWidth, 0);
    }
    GL11.glVertex3f(imgWidth, 0, 0);
  }
  
}
