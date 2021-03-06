package engine.utility;

import engine.Sound;
import engine.rendering.Shader;
import engine.rendering.SpriteSheet;
import engine.rendering.Texture;

import java.io.File;
import java.util.HashMap;

/** https://www.youtube.com/watch?v=mEXCabDeGpg&list=PLtrSb4XxIVbp8AKuEAlwNXDxr99e3woGE&index=13 **/
public class AssetPool {
    private static HashMap<String[], Shader> shaders = new HashMap<>();

    private static HashMap<String, Texture> textures = new HashMap<>();

    private static HashMap<String, SpriteSheet> spriteSheets = new HashMap<>();

    private static HashMap<String, Sound> sounds = new HashMap<>();

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
            Texture texture = new Texture();
            texture.init(resourcePath);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    public static void addSpriteSheet(String resourceName, SpriteSheet spriteSheet){
        File file = new File(resourceName);
        if(!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())){
            AssetPool.spriteSheets.put(file.getAbsolutePath(), spriteSheet);
        }
    }

    public static SpriteSheet getSpriteSheet(String resourceName){
        File file = new File(resourceName);
        if(!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())){
            System.err.println("You tried to access a spritesheet that does not exist! : \n" + resourceName);
        }
        return AssetPool.spriteSheets.getOrDefault(file.getAbsolutePath(), null);
    }

    public static Sound getSound(String resourceName) {
        File file = new File(resourceName);
        if (sounds.containsKey(file.getAbsolutePath())) {
            return sounds.get(file.getAbsolutePath());
        } else {
            System.err.println("Sound file not added!");
        }

        return null;
    }

    public static Sound addSound(String resourceName, boolean loops){
        File file = new File(resourceName);
        if (sounds.containsKey(file.getAbsolutePath())) {
            return sounds.get(file.getAbsolutePath());
        } else {
            Sound sound = new Sound(file.getAbsolutePath(), loops);
            AssetPool.sounds.put(file.getAbsolutePath(), sound);
            return sound;
        }
    }
}
