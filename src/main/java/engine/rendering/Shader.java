package engine.rendering;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private String sourceUrl;
    private String sourceScript;
    private int shaderProgramId;

    public Shader(String url, int shaderProgramId){
        this.sourceUrl = url;
        this.shaderProgramId = shaderProgramId;
        this.getShaderScript();
    }

    public void getShaderScript(){
        StringBuilder sb = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(this.sourceUrl));
            String line;
            while((line = br.readLine()) != null){
                sb.append(line);
                sb.append("\n");
            }
        }
        catch (IOException exception){
            System.out.println(exception);
        }
        this.sourceScript = sb.toString();
    }

    public String getShaderSource(){
        return this.sourceScript;
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
