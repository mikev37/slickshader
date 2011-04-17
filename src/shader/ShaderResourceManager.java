package shader;



public interface ShaderResourceManager{
  
  
  int getFragementShaderID(String fragmentFileName);
  
  
  
  int getVertexShaderID(String vertexFileName);
  
 
  
  /**
   * Link a shader that the shader program depends on to operate.</br>
   * @param programID
   * @param shaderID
   */
  void createProgramShaderDependancy(int programID, int shaderID);
  
  
  void removeProgram(int programID);
}
