package engine.project_manager;

import engine.ToolboxEditor;
import engine.rendering.Sprite;
import engine.rendering.Texture;
import engine.ui.editor.CustomImGuiController;
import engine.utility.AssetPool;
import engine.utility.file_utility.FileDialogManager;
import imgui.ImGui;
import imgui.type.ImString;
import org.joml.Vector2f;

import java.io.File;
import java.util.Objects;

import static engine.utility.Constants.PROJECT_FILE_EXTENSION;

public class LoadProjectPanel {

    private static LoadProjectPanel instance = null;
    private final String projectsDirectoryAdress = "src/main/resources/projects";
    private final File projectsDirectory;

    private final Texture directoryIcon;
    private final Texture projectIcon;

    private final Sprite projectIconAsSprite;
    private final Vector2f[] textureCoords;

    private String path = "";
    ImString pathReferance = new ImString();

    public LoadProjectPanel() {
        projectsDirectory = new File(projectsDirectoryAdress);
        directoryIcon = AssetPool.getTexture("src/main/resources/icons/DirectoryIcon.png");
        projectIcon = AssetPool.getTexture("src/main/resources/icons/full-folder.png");
        projectIconAsSprite = new Sprite();
        projectIconAsSprite.setTexture(projectIcon);
        textureCoords = projectIconAsSprite.getTextureCoords();
    }

    public void imgui() {
        CustomImGuiController.drawInputText("Location", pathReferance);
        path = pathReferance.get();

        ImGui.sameLine();

        if (ImGui.imageButton(directoryIcon.getTextureId(), 16, 16)) {
            path = FileDialogManager.openFile("project,toolbox");
            pathReferance.set(path);
        }

        ImGui.sameLine();
        if (ImGui.button("Load")) {
            Project project = loadProject(path);
            ToolboxEditor.get().loadProject(project);
        }

        // We will only read files that end with the extension .project.toolbox
        for (File file : Objects.requireNonNull(projectsDirectory.listFiles())) {
            if (file.getName().endsWith(PROJECT_FILE_EXTENSION)) {
                String displayName = file.getName().substring(0, file.getName().length() - 16);

                if (ImGui.imageButton(projectIconAsSprite.getTexId(), 64,64, textureCoords[0].x, textureCoords[0].y, textureCoords[2].x, textureCoords[2].y)) {

                }
                ImGui.textWrapped(displayName);

            }
        }
    }


    public static LoadProjectPanel getInstance() {
        if (instance == null) {
            instance = new LoadProjectPanel();
        }
        return instance;
    }


    private Project loadProject(String path) {
//        Gson gson = new GsonBuilder().
//                setPrettyPrinting().
//                registerTypeAdapter(Component.class, new ComponentGsonAdapter()).
//                registerTypeAdapter(Entity.class, new EntityGsonAdapter()).
//                registerTypeAdapter(Project.class, new ProjectGsonAdapter()).
//                create();
//
//        String loadedText = "";
//
//        try {
//            loadedText = new String(Files.readAllBytes(Path.of(new File("").toURI().relativize(new File(path).toURI()))));
//            System.err.println(loadedText);
//        } catch (IOException exception) {
//            exception.printStackTrace();
//        }
//
//        if (!loadedText.equals("")) {
//            return gson.fromJson(loadedText, Project.class);
//        } else {
//            Console.getInstance().addMessage("Empty Project File!");
//            return null;
//        }

        return null;
    }


}
