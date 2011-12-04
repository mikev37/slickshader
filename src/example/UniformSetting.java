package example;


import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import shader.Shader;

public class UniformSetting extends BasicGame {

	private Shader uniformTester;
	
	public UniformSetting(String title){
		super(title);
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		uniformTester = Shader.makeShader("data/basicVertexShader.vrt",
				                              "data/settingUniformArray.frg");
		uniformTester.startShader();
		uniformTester
		.setUniformFloatVariable("myUniformArray",
        new float[]{1.0f,1.0f,
                    1.0f,1.0f,
                    1.0f,0.0f,
                    1.0f,1.0f,
                    1.0f,1.0f})
		.setUniformFloatVariable("myUniformFloat", 0.5f)
		.setUniformFloatVariable("myUniformVector2f", 1.0f, 0.0f)
		.setUniformIntVariable("myUniformInt", 2);
		
//		programID = GL20.glCreateProgram();
//		
//		int vsid = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
//		GL20.glShaderSource(vsid , getProgramCode("data/basicVertexShader.vrt"));
//    GL20.glCompileShader(vsid);
//    if(!compiledSuccessfully(vsid)){
//    	System.out.println("Failed to compile");
//    }
//    
//    int fsid = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
//    GL20.glShaderSource(fsid , getProgramCode("data/settingUniformArray.frg"));
//    GL20.glCompileShader(fsid);
//    if(!compiledSuccessfully(fsid)){
//    	System.out.println("Failed to compile");
//    }
//    
//    GL20.glAttachShader(programID, vsid);
//    GL20.glAttachShader(programID, fsid);
//    
//    GL20.glLinkProgram(programID);
//    if(!linkedSuccessfully()){
//    	System.out.println("Failed to Link");
//    	getProgramInfoLog();
//    }
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		//DO NOTING
	}
	
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		uniformTester.startShader();
		GL11.glBegin(GL11.GL_QUADS);
	    GL11.glVertex3f(  0,   0, 0);
	    GL11.glVertex3f(  0, 400, 0);
	    GL11.glVertex3f(800, 400, 0);
	    GL11.glVertex3f(800,   0, 0);
	  GL11.glEnd();
	  
		Shader.forceFixedShader();
	}


  
  public static void main(String[] args){
    UniformSetting us = new UniformSetting("Setting Uniform Array Test");
    
    try{
      AppGameContainer agc = new AppGameContainer(us, 1200, 800, false);
      agc.start();
    }catch(SlickException e){
      e.printStackTrace();
    }
  }

  
  
  
  
  
//  
//  private boolean compiledSuccessfully(int shaderID){
//    return GL20.glGetShader(shaderID, GL20.GL_COMPILE_STATUS)==GL11.GL_TRUE;
//  }
//  
//  private boolean linkedSuccessfully(){
//  	int i = GL20.glGetShader(programID, GL20.GL_LINK_STATUS);
//  	System.out.println("link status: " + i);
//    return GL20.glGetShader(programID, GL20.GL_LINK_STATUS)==GL11.GL_TRUE;
//  }
//  
//  private String getProgramInfoLog(){
//    return GL20.glGetProgramInfoLog(programID, 512).trim();
//  }
//  
//  private ByteBuffer getProgramCode(String filename)throws SlickException{
//    InputStream fileInputStream = null;
//    byte[] shaderCode = null;
//        
//    fileInputStream = ResourceLoader.getResourceAsStream(filename);
//    DataInputStream dataStream = new DataInputStream(fileInputStream);
//    try{
//      dataStream.readFully(shaderCode = new byte[fileInputStream.available()]);
//      fileInputStream.close();
//      dataStream.close();
//    }catch (IOException e) {
//      throw new SlickException(e.getMessage());
//    }
//
//    ByteBuffer shaderPro = BufferUtils.createByteBuffer(shaderCode.length);
//
//    shaderPro.put(shaderCode);
//    shaderPro.flip();
//
//    return shaderPro;
//  }
  
}
