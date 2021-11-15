package engine.rendering;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int shaderProgramId;

    private int vertexShaderId;
    private int fragmentShaderId;

    private String vertexShaderSrc;
    private String fragmentShaderSrc;


    public Shader(String vertexShaderSrc, String fragmentShaderSrc) {
        this.vertexShaderSrc = vertexShaderSrc;
        this.fragmentShaderSrc = fragmentShaderSrc;
    }

    private String getShaderScript(String url){
        StringBuilder sb = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(url));
            String line;
            while((line = br.readLine()) != null){
                sb.append(line);
                sb.append("\n");
            }
        }
        catch (IOException exception){
            System.err.println("Error loading script!");
        }

        return sb.toString();
    }

    public void compile() {
        // ============================================================
        // Compile and link shaders
        // ============================================================
        int vertexID, fragmentID;

        // First load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(vertexID, getShaderScript(vertexShaderSrc));
        glCompileShader(vertexID);

        // Check for errors in compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + vertexShaderSrc + "'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        // First load and compile the vertex shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(fragmentID, getShaderScript(fragmentShaderSrc));
        glCompileShader(fragmentID);

        // Check for errors in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + fragmentShaderSrc + "'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        // Link shaders and check for errors
        shaderProgramId = glCreateProgram();
        glAttachShader(shaderProgramId, vertexID);
        glAttachShader(shaderProgramId, fragmentID);
        glLinkProgram(shaderProgramId);

        // Check for linking errors
        success = glGetProgrami(shaderProgramId, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramId, GL_INFO_LOG_LENGTH);
            System.out.println(glGetProgramInfoLog(shaderProgramId, len));
            assert false : "";
        }
    }

    public void bind() {
        glUseProgram(shaderProgramId);
    }

    public void unbind() {
        glUseProgram(0);
    }


    public int getShaderProgramId(){
        return this.shaderProgramId;
    }

    public void uploadMat4f(String uniformName, Matrix4f matrix){
        int uniformLocation = glGetUniformLocation(this.getShaderProgramId(),uniformName);
        // capacity = matrix size ( 4 x 4 = 16 )
        // we have to flatten out matrices that is why we have this buffer.
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        matrix.get(matrixBuffer);

        glUniformMatrix4fv(uniformLocation,false,matrixBuffer);
    }
    public void uploadMat3f(String uniformName, Matrix3f matrix){
        int uniformLocation = glGetUniformLocation(this.getShaderProgramId(),uniformName);
        // capacity = matrix size ( 3 x 3 = 16 )
        // we have to flatten out matrices that is why we have this buffer.
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(9);
        matrix.get(matrixBuffer);

        glUniformMatrix3fv(uniformLocation,false,matrixBuffer);
    }

    public void uploadVec4f(String uniformName, Vector4f vector){
        int uniformLocation = glGetUniformLocation(this.getShaderProgramId(),uniformName);
        glUniform4f(uniformLocation, vector.x, vector.y, vector.z, vector.w);
    }

    public void uploadVec3f(String uniformName, Vector3f vector){
        int uniformLocation = glGetUniformLocation(this.getShaderProgramId(),uniformName);
        glUniform3f(uniformLocation, vector.x, vector.y, vector.z);
    }


    public void uploadVec2f(String uniformName, Vector2f vector){
        int uniformLocation = glGetUniformLocation(this.getShaderProgramId(),uniformName);
        glUniform2f(uniformLocation, vector.x, vector.y);
    }



    public void uploadFloat(String uniformName, float value){
        int uniformLocation = glGetUniformLocation(this.getShaderProgramId(),uniformName);
        glUniform1f(uniformLocation, value);
    }

    public void uploadInt(String uniformName, int value){
        int uniformLocation = glGetUniformLocation(this.getShaderProgramId(),uniformName);
        glUniform1i(uniformLocation, value);
    }

    public void uploadTexture(String uniformName, int slot){
        int uniformLocation = glGetUniformLocation(this.getShaderProgramId(),uniformName);
        glUniform1i(uniformLocation, slot);
    }

}
