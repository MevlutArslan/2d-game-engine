package engine.utility;

import com.google.gson.*;
import components.Component;

import java.lang.reflect.Type;


// https://www.javacodegeeks.com/2012/04/json-with-gson-and-abstract-classes.html
public class ComponentGsonAdapter implements JsonSerializer<Component> ,JsonDeserializer<Component> {
    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext jsonSerializationContext){
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
        result.add("properties", jsonSerializationContext.serialize(src, src.getClass()));

        return result;
    }

    @Override
    public Component deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try{
            return jsonDeserializationContext.deserialize(element, Class.forName(type));
        } catch (ClassNotFoundException e){
            throw new JsonParseException("Unknown element type : " + type);
        }
    }
}
