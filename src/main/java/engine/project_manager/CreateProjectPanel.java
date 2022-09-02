package engine.project_manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.ToolboxEditor;
import engine.rendering.Sprite;
import engine.rendering.Texture;
import engine.ui.WindowType;
import engine.ui.editor.CustomImGuiController;
import engine.utility.AssetPool;
import engine.utility.Constants;
import engine.utility.gson_adapter.ProjectGsonAdapter;
import imgui.ImGui;
import imgui.type.ImString;
import org.joml.Vector2f;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CreateProjectPanel {
    private static CreateProjectPanel instance;

    private final String projectsDirectoryAdress = "src/main/resources/projects";
    private final File projectsDirectory;

    private final Texture directoryIcon;
    private final Texture projectIcon;

    private final Sprite projectIconAsSprite;
    private final Vector2f[] textureCoords;

    private String projectName = "";
    ImString projectNameReferance = new ImString();

    private int debugInt = 0;

    private Gson gson;

    private CreateProjectPanel(){
        projectsDirectory = new File(projectsDirectoryAdress);
        directoryIcon = AssetPool.getTexture("src/main/resources/icons/DirectoryIcon.png");
        projectIcon = AssetPool.getTexture("src/main/resources/icons/full-folder.png");
        projectIconAsSprite = new Sprite();
        projectIconAsSprite.setTexture(projectIcon);
        textureCoords = projectIconAsSprite.getTextureCoords();

        gson = new GsonBuilder().
                setPrettyPrinting().
                registerTypeAdapter(Project.class, new ProjectGsonAdapter()).
                create();

    }

    public void imgui(){
        CustomImGuiController.drawInputText("Project name", projectNameReferance);
        projectName = projectNameReferance.get();

        ImGui.sameLine();

        if(ImGui.button("Create Project")){
            Project project = new Project(projectName);

            saveProject(project);
            ToolboxEditor.get().loadProject(project);
        }
    }

    public static CreateProjectPanel getInstance(){
        if(instance == null){
            instance = new CreateProjectPanel();
        }
        return instance;
    }

    private void saveProject(Project project){

        try {
            File file = new File(project.getLocation() + "." + Constants.PROJECT_FILE_EXTENSION);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);

            fileWriter.write(gson.toJson(project));
            fileWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
