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
        result.addProperty("editorConfigurationLocation", project.getEditorConfigurationLocation());

        return result;
    }

    @Override
    public Project deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();

        String name = jsonObject.get("name").getAsString();
        String location = jsonObject.get("location").getAsString();
        String resourceLocation = jsonObject.get("resourceLocation").getAsString();
        String editorConfigurationLocation = jsonObject.get("editorConfigurationLocation").getAsString();

        Project project = new Project(name, location);
        project.setResourceLocation(resourceLocation);
        project.setEditorConfigurationLocation(editorConfigurationLocation);

        return project;
    }
}
