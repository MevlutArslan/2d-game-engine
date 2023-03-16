package engine.project_manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.Component;
import engine.Entity;
import engine.ToolboxEditor;
import engine.rendering.Sprite;
import engine.rendering.Texture;
import engine.ui.editor.Console;
import engine.ui.editor.CustomImGuiController;
import engine.utility.AssetPool;
import engine.utility.file_utility.FileDialogManager;
import engine.utility.gson_adapter.ComponentGsonAdapter;
import engine.utility.gson_adapter.EntityGsonAdapter;
import engine.utility.gson_adapter.ProjectGsonAdapter;
import imgui.ImGui;
import imgui.type.ImString;
import org.joml.Vector2f;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static engine.utility.Constants.PROJECT_FILE_EXTENSION;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.glPixelStorei;

public class LoadProjectPanel {

    private static LoadProjectPanel instance = null;
    private final String projectsDirectoryAdress = "src/main/resources/projects";
    private final File projectsDirectory;

    private final Texture directoryIcon;
    private Texture projectIcon;

    private final Sprite projectIconAsSprite;
    private Vector2f[] textureCoords;

    private String path = "";
    ImString pathReferance = new ImString(200);

    private LinkedHashMap<String, Project> projects;

    public LoadProjectPanel() {
        projectsDirectory = new File(projectsDirectoryAdress);
        directoryIcon = AssetPool.getTexture("src/main/resources/icons/DirectoryIcon.png");
        projectIcon = AssetPool.getTexture("src/main/resources/icons/full-folder.png");
        projectIconAsSprite = new Sprite();
        projectIconAsSprite.setTexture(projectIcon);
        textureCoords = projectIconAsSprite.getTextureCoords();
        projects = new LinkedHashMap<>();

        for (File file : Objects.requireNonNull(projectsDirectory.listFiles())) {
            // change row when index % 3 == 0
            // otherwise change column
            if (file.getName().endsWith(PROJECT_FILE_EXTENSION)) {
                String displayName = file.getName().substring(0, file.getName().length() - 16);
                projects.put(displayName, loadProject(file.getPath()));
            }
        }
    }

    public void imgui() {
        CustomImGuiController.drawInputText("Location", pathReferance);
        path = pathReferance.get();

        ImGui.sameLine();

        if (ImGui.imageButton(directoryIcon.getTextureId(), 16, 16)) {
            path = FileDialogManager.openFile("");
            pathReferance.set(path);
        }

        ImGui.sameLine();
        if (ImGui.button("Load")) {
            Project project = loadProject(path);
            ToolboxEditor.get().loadProject(project);
        }

        // We will only read files that end with the extension .project.toolbox

        if (ImGui.beginTable("Projects", 3)) {
            for (Map.Entry<String, Project> entry : projects.entrySet()) {
                String displayName = entry.getKey();
                Project project = entry.getValue();
                // TODO show project's thumbnail as projectIcon and fix loading project by button click
                glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

                Texture thumbnail = AssetPool.getTexture(project.getThumbnailLocation());
                ImGui.tableNextColumn();
                if (ImGui.imageButton(thumbnail.getTextureId(), 200, 100, 0, 1, 1, 0)) {
                    ToolboxEditor.get().loadProject(project);
                }
                ImGui.textWrapped(displayName);

                glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
            }
            ImGui.endTable();
        }
    }


    public static LoadProjectPanel getInstance() {
        if (instance == null) {
            instance = new LoadProjectPanel();
        }
        return instance;
    }


    private Project loadProject(String path) {
        Gson gson = new GsonBuilder().
                setPrettyPrinting().
                registerTypeAdapter(Component.class, new ComponentGsonAdapter()).
                registerTypeAdapter(Entity.class, new EntityGsonAdapter()).
                registerTypeAdapter(Project.class, new ProjectGsonAdapter()).
                create();

        String loadedText = "";

        try {
            loadedText = new String(Files.readAllBytes(Path.of(path)));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        if (!loadedText.equals("")) {
            return gson.fromJson(loadedText, Project.class);
        } else {
            Console.getInstance().addMessage("Empty Project File!");
            return null;
        }
    }
}
