package shader;


import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.newdawn.slick.Image;
import org.newdawn.slick.Renderable;
import org.newdawn.slick.SlickException;
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
public class MultiTex implements Renderable{

  public Texture tex1;
  public Texture tex2;
  
  public MultiTex(String t1, String t2)throws SlickException{
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    tex1 = new Image(t1).getTexture();
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    
    GL13.glActiveTexture(GL13.GL_TEXTURE1);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    tex2 = new Image(t2).getTexture();
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
  }
  
  
  public void draw(float x, float y){
    GL11.glTranslatef(x, y, 0);
    
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex1.getTextureID());

    GL13.glActiveTexture(GL13.GL_TEXTURE1);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex2.getTextureID());
    

    GL11.glBegin(SGL.GL_QUADS); 
      drawEmbedded(0,0,
                   tex1.getImageWidth(),
                   tex1.getImageHeight()); 
    GL11.glEnd(); 
    
    GL11.glTranslatef(-x, -y, 0);
    
    
    GL13.glActiveTexture(GL13.GL_TEXTURE1);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }


  private void drawEmbedded(int x, int y, int width, int height){
    GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0, 0, 0);
    GL13.glMultiTexCoord2f(GL13.GL_TEXTURE1, 0, 0);
    GL11.glVertex3f(x, y, 0);
    
    GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0, 0, 1);
    GL13.glMultiTexCoord2f(GL13.GL_TEXTURE1, 0, 1);
    GL11.glVertex3f(x, y + height, 0);
    
    GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0, 1, 1);
    GL13.glMultiTexCoord2f(GL13.GL_TEXTURE1, 1, 1);
    GL11.glVertex3f(x + width, y + height, 0);
    
    GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0, 1, 0);
    GL13.glMultiTexCoord2f(GL13.GL_TEXTURE1, 1, 0);
    GL11.glVertex3f(x + width, y, 0);
  }
  
}
