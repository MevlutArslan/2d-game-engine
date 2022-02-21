package engine.rendering;

import engine.Entity;
import components.rendering.SpriteRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {

    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;

    private static Shader currentShader = null;

    public Renderer(){
        this.batches = new ArrayList<>();
    }

    public void add(Entity entity){
        SpriteRenderer spr = entity.getComponent(SpriteRenderer.class);
        if(spr != null){
            add(spr);
        }
    }

    private void add(SpriteRenderer spr){
        boolean added = false;
        for(RenderBatch batch : batches){
            if(batch.hasRoom() && batch.getzIndex() == spr.parent.transform.zIndex){
                Texture tex = spr.getTexture();
                if(tex == null || (batch.hasTexture(tex) || batch.hasTextureRoom())) {
                    batch.addSprite(spr);
                    added = true;
                    break;
                }
            }
        }

        if(!added){
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, spr.parent.transform.zIndex, this);
            newBatch.start();

            batches.add(newBatch);
            newBatch.addSprite(spr);
            Collections.sort(batches);
        }
    }

    public void render(){
        currentShader.bind();
        for(int i = 0; i < batches.size(); i++){
            RenderBatch renderBatch = batches.get(i);
            renderBatch.render();
        }
    }

    public static Shader getCurrentShader(){
        return currentShader;
    }

    public static void bindShader(Shader shader){
        currentShader = shader;
    }

    public void destroyEntity(Entity entity){
        if(entity.getComponent(SpriteRenderer.class)==null){
            return;
        }
        for(RenderBatch batch : batches){
            if(batch.destroyIfExists(entity)){
                return;
            }
        }
    }
}
