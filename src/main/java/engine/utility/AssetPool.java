package engine.utility;

import engine.rendering.Shader;
import engine.rendering.Texture;

import java.io.File;
import java.util.HashMap;

/** https://www.youtube.com/watch?v=mEXCabDeGpg&list=PLtrSb4XxIVbp8AKuEAlwNXDxr99e3woGE&index=13 **/
public class AssetPool {
    private static HashMap<String[], Shader> shaders = new HashMap<>();

    private static HashMap<String, Texture> textures = new HashMap<>();

    public static Shader getShader(String[] resourcePath){
        File file = new File(resourcePath[0]);
        File file2 = new File(resourcePath[1]);

        String[] absolutePath =  new String[]{
                file.getAbsolutePath(), file2.getAbsolutePath()
        };

        if(AssetPool.shaders.containsKey(absolutePath)){
            return AssetPool.shaders.get(absolutePath);
        }
        else{
            Shader shader = new Shader(resourcePath[0], resourcePath[1]);
            shader.compile();
            AssetPool.shaders.put(absolutePath, shader);
            return shader;
        }
    }

    public static Texture getTexture(String resourcePath){
        File file = new File(resourcePath);
        if(AssetPool.textures.containsKey(file.getAbsolutePath())){
            return AssetPool.textures.get(file.getAbsolutePath());
        }else{
            Texture texture = new Texture(resourcePath);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }
}
