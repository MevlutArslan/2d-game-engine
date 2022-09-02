package engine.utility.gson_adapter;

import com.google.gson.*;
import engine.project_manager.Project;

import java.lang.reflect.Type;

public class ProjectGsonAdapter implements JsonSerializer<Project>, JsonDeserializer<Project> {

    @Override
    public JsonElement serialize(Project project, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(project.getClass().getCanonicalName()));

        result.addProperty("name", project.getName());
        result.addProperty("location", project.getLocation());
        result.addProperty("resourceLocation", project.getResourceLocation());
        result.addProperty("thumbnail", project.getThumbnail());
        result.addProperty("editorConfigurationLocation", project.getEditorConfigurationLocation());

        return result;
    }

    @Override
    public Project deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            return jsonDeserializationContext.deserialize(element, Class.forName(type));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown element type : " + type);
        }
    }
}
