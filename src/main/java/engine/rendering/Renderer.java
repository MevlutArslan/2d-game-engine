package engine.rendering;

import components.Entity;
import components.rendering.SpriteRenderer;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;

    public Renderer(){
        this.batches = new ArrayList<>();
    }

    public void add(Entity entity){
        SpriteRenderer spr = entity.getComponent(SpriteRenderer.class);
        if(spr != null){
            add(spr);
        }
    }

    public void add(SpriteRenderer spr){
        boolean added = false;
        for(RenderBatch batch : batches){
            if(batch.hasRoom()){
                batch.addSprite(spr);
                added = true;
                break;
            }
        }

        if(!added){
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE);
            newBatch.start();

            batches.add(newBatch);
            newBatch.addSprite(spr);
        }
    }

    public void render(){
        for(RenderBatch renderBatch : batches){
            renderBatch.render();
        }
    }
}
