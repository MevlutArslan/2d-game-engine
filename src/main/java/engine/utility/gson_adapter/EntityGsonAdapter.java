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
        Transform transform = jsonDeserializationContext.deserialize(jsonObject.get("transform"), Transform.class);
        JsonArray components = jsonObject.getAsJsonArray("components");
        int zIndex = jsonDeserializationContext.deserialize(jsonObject.get("zIndex"), int.class);

        Entity entity = new Entity(name, transform, zIndex);
        for(JsonElement element : components){
            Component component = jsonDeserializationContext.deserialize(element, Component.class);
            entity.addComponent(component);
        }
        return entity;
    }
}
