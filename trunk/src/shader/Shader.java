package shader;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.ResourceLoader;

/**
 * Class used to use and access shaders without having to deal
 * with all of the fidly openGL bits.
 * @author Chronocide (Jeremy Klix)
 *
 *TODO have set methods return reference to self for easy chaining
 */
public class Shader {
  public static final int BRIEF = 128;
  public static final int MODERATE = 512;
  public static final int VERBOSE = 1024;
  private static int logging = MODERATE;
  private static final int NOT_LOADED = -1;
  
  private ShaderResourceManager srm;
  
  /**
   * ID of the <tt>Shader</tt>.  A Shader may have programID of 
   * -1 only before construction is completed, or
   * after the <tt>Shader</tt> is deleted
   */
  private int programID = NOT_LOADED;
  
  
  
  /**
   * @Deprecated use for testing only.
   * Private constructor used to guard against external extension.
   * While determining how extensions should work.</br>
   * @param srm
   * @param vertexFileName
   * @param fragmentFileName
   * @throws SlickException
   * TODO needs testing to ensure that cleaning up after failed,
   * shaders works. 
   * TODO delete
   */
  private Shader(ShaderResourceManager srm,
                 String vertexFileName,
                 String fragmentFileName) throws SlickException{
    this.srm = srm;
    StringBuilder errorMessage = new StringBuilder();
    
    programID = GL20.glCreateProgram();
    int vsid = srm.getVertexShaderID(vertexFileName);
    int fsid = srm.getFragementShaderID(fragmentFileName);
    
    srm.createProgramShaderDependancy(programID, vsid);
    srm.createProgramShaderDependancy(programID, fsid);
    
    GL20.glShaderSource(vsid, getProgramCode(vertexFileName));
      GL20.glCompileShader(vsid);
      if(!compiledSuccessfully(vsid)){
        errorMessage.append("Could not compile Vertex Shader ");
        errorMessage.append(vertexFileName);
        errorMessage.append(" failed to compile.\n");
        errorMessage.append(getShaderInfoLog(vsid));
        errorMessage.append("\n\n");
      }
    
    GL20.glShaderSource(fsid, getProgramCode(fragmentFileName));
      GL20.glCompileShader(fsid);
      if(!compiledSuccessfully(fsid)){
        errorMessage.append("Could not compile Fragment Shader ");
        errorMessage.append(fragmentFileName);
        errorMessage.append(" failed to compile.\n");
        errorMessage.append(getShaderInfoLog(fsid));
        errorMessage.append("\n\n");
      }

      GL20.glAttachShader(programID, vsid);
      GL20.glAttachShader(programID, fsid);
    
      GL20.glLinkProgram(programID);
      if(!linkedSuccessfully()){
        errorMessage.append("Linking Error\n");
        errorMessage.append(getProgramInfoLog());
        errorMessage.append("\n\n");
      }
      
      if(errorMessage.length()!=0){
        srm.removeProgram(programID);
        programID = -1;
        errorMessage.append("Stack Trace:");
        throw new SlickException(errorMessage.toString());
      }
  }
  
  
  
  private Shader(ShaderResourceManager srm,
                 Collection<String> vertex,
                 Collection<String> fragment)throws SlickException{
    this.srm = srm;
    StringBuilder errorMessage = new StringBuilder();
    
    programID = GL20.glCreateProgram();
    
    int[] shaderIds = new int[vertex.size() + fragment.size()];
    int index = 0;
    
    //Load Vertex Shaders
    for(String vertShader: vertex){
      int vsid = srm.getVertexShaderID(vertShader);
      srm.createProgramShaderDependancy(programID, vsid);
      
      //Add to shader ids array
      shaderIds[index] = vsid;
      index++;
      
      //Check for errors with shader
      if(!compiledSuccessfully(vsid)){
        errorMessage.append("Vertex Shader ");
        errorMessage.append(vertShader);
        errorMessage.append(" failed to compile.\n");
        errorMessage.append(getShaderInfoLog(vsid));
        errorMessage.append("\n\n");
      }
    }
    
    
    //Load Fragment Shaders
    for(String fragShader: fragment){
      int fsid = srm.getFragementShaderID(fragShader);
      srm.createProgramShaderDependancy(programID, fsid);

      //Add to shader ids array
      shaderIds[index] = fsid;
      index++;
      
      //Check for errors with shader
      if(!compiledSuccessfully(fsid)){
        errorMessage.append("Fragment Shader ");
        errorMessage.append(fragShader);
        errorMessage.append(" failed to compile.\n");
        errorMessage.append(getShaderInfoLog(fsid));
        errorMessage.append("\n\n");
      }
    }
    
    //Attach shaders to program
    for(int i=0; i<index; i++){
      GL20.glAttachShader(programID, shaderIds[i]);
    }
    //Link program
    GL20.glLinkProgram(programID);
    if(!linkedSuccessfully()){
      errorMessage.append("Linking Error\n");
      errorMessage.append(getProgramInfoLog());
      errorMessage.append("\n\n");
    }
    
    if(errorMessage.length()!=0){
      errorMessage.insert(0, "Could not compile shader.\n");
      srm.removeProgram(programID);
      programID = -1;
      errorMessage.append("Stack Trace:");
      throw new SlickException(errorMessage.toString());
    }
    
  }
  
  
  
  /**
   * Factory method to create a new Shader.
   * @param vertexFileName
   * @param fragmentFileName
   * @return
   * @throws SlickException
   */
  public static Shader makeShader(String vertexFileName,
                                  String fragmentFileName)throws SlickException{
    ArrayList<String> l1 = new ArrayList<String>();
    l1.add(vertexFileName);
    ArrayList<String> l2 = new ArrayList<String>();
    l2.add(fragmentFileName);
    
    return new Shader(ShaderResourceManagerImpl.getSRM(),
                      l1,
                      l2);
  }
  
  
  
  /**
   * Reverts GL context back to the fixed pixel pipeline.<br>
   */
  public static void forceFixedShader(){
    GL20.glUseProgram(0);
  }
  
  
  
  /**
   * Sets the number of characters to be returned when printing
   * errors.</br>  Suggested values are the constants
   * <tt>BRIEF</tt>, <tt>MODERATE</tt>, and <tt>VERBOSE</tt>.</br>
   * @param detailLevel number of characters to display for error
   *                    messages.
   */
  public static void setLoggingDetail(int detailLevel){
    logging = detailLevel;
  }

  
  
  /**
   * Deletes this shader and unloads all free resources.</br>
   * TODO should this be called from <tt>finalise()</tt>, or is that just
   * asking for trouble?
   */
  public void deleteShader(){
    srm.removeProgram(programID);
    programID = NOT_LOADED;
  }
  
  
  
  /**
   * Returns true if this <tt>Shader</tt> has been deleted.</br>
   * @return true if this <tt>Shader</tt> has been deleted.</br>
   */
  public boolean isDeleted(){
    return programID == NOT_LOADED;
  }
  
  
  
  /**
   * Activates the shader.</br>
   */
  public void startShader(){
    if(programID == NOT_LOADED){
      throw new IllegalStateException("Cannot start shader; this" +
                                      " Shader has been deleted");
    }
    forceFixedShader(); //Not sure why this is necessary but it is.
    GL20.glUseProgram(programID);
  }
  
  
  
//UNIFORM SETTERS
  //TODO figure out if there is any practical way to ensure that
  //the setter type used matches the variable name passed in.
  //That is if a they are calling setUnifromVariable1f on shader
  //variable "offset" offset really is a float.
  
  /**
   * Sets the value of the uniform integer Variable <tt>name</tt>.</br>
   * @param name the variable to set.
   * @param value the value to be set.
   */
  public void setUniform1iVariable(String name, int value){
    CharSequence param = new StringBuffer(prepareStringVariable(name));
    int location = GL20.glGetUniformLocation(programID, param);
    locationCheck(location, name);
    GL20.glUniform1i(location, value);
  }

  
  
  /**
   * Sets the value of the uniform integer Variable
   * <tt>name</tt>.</br>
   * @param name the variable to set.
   * @param value the value to be set.
   */
  public void setUniform1fVariable(String name, float value){
    CharSequence param = new StringBuffer(prepareStringVariable(name));
    int location = GL20.glGetUniformLocation(programID, param);
    locationCheck(location, name);
    GL20.glUniform1f(location, value);
  }
  
  
  
  public void setUniform2iVariable(String name, int v0, int v1){
    CharSequence param = new StringBuffer(prepareStringVariable(name));
    int location = GL20.glGetUniformLocation(programID, param);
    locationCheck(location, name);
    GL20.glUniform2i(location, v0, v1);
  }
  
  
  
  public void setUniform2fVariable(String name,
                                   float v0, float v1){
    CharSequence param = new StringBuffer(prepareStringVariable(name));
    int location = GL20.glGetUniformLocation(programID, param);
    locationCheck(location, name);
    GL20.glUniform2f(location, v0, v1);
  }
  
  
  
  public void setUniform3iVariable(String name,
                                   int v0, int v1, int v2){
    CharSequence param = new StringBuffer(prepareStringVariable(name));
    int location = GL20.glGetUniformLocation(programID, param);
    locationCheck(location, name);
    GL20.glUniform3i(location, v0, v1, v2);
  }
  
  
  
  public void setUniform3fVariable(String name,
                                   float v0, float v1, float v2){
    CharSequence param = new StringBuffer(prepareStringVariable(name));
    int location = GL20.glGetUniformLocation(programID, param);
    locationCheck(location, name);
    GL20.glUniform3f(location, v0, v1, v2);
  }
  
  
  
  public void setUniform3iVariable(String name,
                                   int v0, int v1, int v2, int v3){
    CharSequence param = new StringBuffer(prepareStringVariable(name));
    int location = GL20.glGetUniformLocation(programID, param);
    locationCheck(location, name);
    GL20.glUniform4i(location, v0, v1, v2, v3);
  }
  
  
  
  public void setUniform4fVariable(String name,
                                   float v0, float v1,
                                   float v2, float v3){
    CharSequence param = new StringBuffer(prepareStringVariable(name));
    int location = GL20.glGetUniformLocation(programID, param);
    locationCheck(location, name);
    GL20.glUniform4f(location, v0, v1, v2, v3);
  }
  
  
  
  //TODO Test
  public void setUniformMatrix(String name,
                               boolean transpose,
                               float[][] matrix){
    //Convert matrix format
    FloatBuffer matBuffer = matrixPrepare(matrix);
    
    //Get uniform location
    CharSequence param = new StringBuffer(prepareStringVariable(name));
    int location = GL20.glGetUniformLocation(programID, param);
    locationCheck(location, name);

    //determine correct matrixSetter
    switch(matrix.length){
      case 2: GL20.glUniformMatrix2(location, transpose, matBuffer);
      break;
      case 3: GL20.glUniformMatrix3(location, transpose, matBuffer);
      break;
      case 4: GL20.glUniformMatrix4(location, transpose, matBuffer);
      break;
    }
  }
  
  
  
  private FloatBuffer matrixPrepare(float[][] matrix){
    //Check argument validity
    if(matrix==null){
      throw new IllegalArgumentException("The matrix may not be null");
    }
    int row = matrix.length;
    if(row<2){
      throw new IllegalArgumentException("The matrix must have at least 2 rows.");
    }
    int col = matrix[0].length;
    if(col!=row){
      throw new IllegalArgumentException("The matrix must have an equal number of rows and columns.");
    }
    float[] unrolled = new float[row*col];
    
    for(int i=0;i<row;i++){
      for(int j=0;j<col;j++){
        unrolled[i*col+j] = matrix[i][j];
      }
    }
    
    return FloatBuffer.wrap(unrolled);
  }
  
  
  
  private void locationCheck(int location, String varName){
    if(location==-1){
      System.err.println("Warning: variable " + varName + " could " +
          "not be found. Ensure the name is spelled correctly");
    }
  }
  
  
  
  /**
   * Returns true if the shader compiled successfully.</br>
   * @param shaderID
   * @return true if the shader compiled successfully.</br>
   */
  private boolean compiledSuccessfully(int shaderID){
    return GL20.glGetShader(shaderID, GL20.GL_COMPILE_STATUS)==GL11.GL_TRUE;
  }
  
  
  
  private String prepareStringVariable(String name){
    if(name.endsWith("\0")){
      return name;
    }
    return name+"\0";
  }
  
  
  
  /**
   * Returns true if the shader program linked successfully.</br>
   * @param shaderID
   * @return true if the shader program linked successfully.</br>
   */
  private boolean linkedSuccessfully(){
    return GL20.glGetShader(programID, GL20.GL_LINK_STATUS)==GL11.GL_TRUE;
  }
  
  
  
  private String getShaderInfoLog(int shaderID){
    return GL20.glGetShaderInfoLog(shaderID, logging).trim();
  }
  
  
  
  private String getProgramInfoLog(){
    return GL20.glGetProgramInfoLog(programID, logging).trim();
  }
  
  
  
  /**
   * @Deprecated
   * Gets the program code from the file "filename" and puts in into a 
   * byte buffer.
   * @param filename the full name of the file.
   * @return a ByteBuffer containing the program code.
   * @throws SlickException
   * TODO delete: depended on by deprecated constructor
   */
  private ByteBuffer getProgramCode(String filename)throws SlickException{
    InputStream fileInputStream = null;
    byte[] shaderCode = null;
        
    fileInputStream = ResourceLoader.getResourceAsStream(filename);
    DataInputStream dataStream = new DataInputStream(fileInputStream);
    try{
      dataStream.readFully(shaderCode = new byte[fileInputStream.available()]);
      fileInputStream.close();
      dataStream.close();
    }catch (IOException e) {
      throw new SlickException(e.getMessage());
    }

 
    ByteBuffer shaderPro = BufferUtils.createByteBuffer(shaderCode.length);

    shaderPro.put(shaderCode);
    shaderPro.flip();

    return shaderPro;
  }
  
  
}
