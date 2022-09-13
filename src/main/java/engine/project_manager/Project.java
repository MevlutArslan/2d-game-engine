package engine.project_manager;

import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Essentially a project is made up of its Assets and its Scenes.
 * A Scene is made up of its Entities
 * Entities are made up of Components
 * So when I need to save a Scene I just need to save it's Entities.
 */
public class Project {
    private String name = "unnamed_project";
    private String location = "src/main/resources/projects/";
    private String resourceLocation = "";
    private String thumbnailLocation = "";
    private String editorConfigurationLocation = "";
    private String scenesLocation = "";
    private Map<String, String> scenes;

    public Project(String name) {
        this.name = name;
        this.location += this.name;
        this.resourceLocation = this.location + "/resources/";
        this.scenesLocation = this.location + "/scenes/";
        this.scenes = new LinkedHashMap<>();
        this.thumbnailLocation = this.resourceLocation + (name + "_thumbnail.png");
        this.editorConfigurationLocation = this.location + "/imgui.ini";
    }

    public Project(String name, String location) {
        this.name = name;
        this.location = location;
        this.resourceLocation = this.location + "/resources/";
        this.thumbnailLocation = this.resourceLocation + (name + "_thumbnail.png");
        this.editorConfigurationLocation = this.location + "/imgui.ini";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getResourceLocation() {
        return resourceLocation;
    }

    public void setResourceLocation(String resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public String getThumbnailLocation() {
        return thumbnailLocation;
    }

    public String getEditorConfigurationLocation() {
        return this.editorConfigurationLocation;
    }

    public void setEditorConfigurationLocation(String editorConfigurationLocation) {
        this.editorConfigurationLocation = editorConfigurationLocation;
    }

    public String getScenesLocation() {
        return scenesLocation;
    }

    public void setScenesLocation(String scenesLocation) {
        this.scenesLocation = scenesLocation;
    }

    @Override
    public String toString() {
        return "Project{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", resourceLocation='" + resourceLocation + '\'' +
                ", thumbnail='" + thumbnailLocation + '\'' +
                ", editorConfigurationLocation='" + editorConfigurationLocation + '\'' +
                '}';
    }
}
