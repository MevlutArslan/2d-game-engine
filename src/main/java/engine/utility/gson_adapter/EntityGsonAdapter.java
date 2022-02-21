package engine.utility.gson_adapter;

import com.google.gson.*;
import engine.Component;
import engine.Entity;
import components.Transform;

import java.lang.reflect.Type;

// Do as soon as I sit down
public class EntityGsonAdapter implements JsonDeserializer<Entity> {
    @Override
    public Entity deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("components");

        Entity entity = new Entity(name);
        for(JsonElement element : components){
            Component component = jsonDeserializationContext.deserialize(element, Component.class);
            entity.addComponent(component);
        }
        entity.transform = entity.getComponent(Transform.class);
        return entity;
    }
}
