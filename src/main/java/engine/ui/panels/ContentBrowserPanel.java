package engine.ui.panels;

import engine.Component;
import engine.rendering.Texture;
import engine.utility.AssetPool;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;

import java.io.File;
import java.util.Objects;

import static engine.utility.Constants.PREFAB_FILE_EXTENSION;
import static engine.utility.Constants.SCENE_FILE_EXTENSION;

// https://www.youtube.com/watch?v=aBuPmOGC7hU&list=PLlrATfBNZ98dC-V-N3m0Go4deliWHPFwT&index=104&t=1035s
public class ContentBrowserPanel extends Component {

    private File assetsDirectory;
    private File currentDirectory;

    float padding = 16.0f;
    float thumbnailSize = 88.0f;

    private Texture directoryIcon;
    private Texture fileIcon;

    public ContentBrowserPanel(String directoryPath) {
        this.assetsDirectory = new File(directoryPath);
        this.currentDirectory = assetsDirectory;

        this.directoryIcon = AssetPool.getTexture("src/main/resources/icons/DirectoryIcon.png");
        this.fileIcon = AssetPool.getTexture("src/main/resources/icons/FileIcon.png");
    }

    @Override
    public void imgui() {
        ImGui.begin("Content Browser");

        if (!currentDirectory.getPath().equals(assetsDirectory.getPath())) {
            if (ImGui.button("<-")) {
                currentDirectory = new File(currentDirectory.getParent());
            }
        }

        float cellSize = thumbnailSize + padding;
        float panelWidth = ImGui.getContentRegionAvailX();
        int columnCount = (int) (panelWidth / cellSize);
        if (columnCount < 1) {
            columnCount = 1;
        }

        ImGui.columns(columnCount);
        int i = 0;
        for (File file : Objects.requireNonNull(currentDirectory.listFiles())) {
            ImGui.pushID(i++);

            // TODO IF Prefab and it has sprite in it use it as an image for the button

            Texture icon = file.isDirectory() ? directoryIcon : fileIcon;
            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
            ImGui.imageButton(icon.getTextureId(), thumbnailSize, thumbnailSize, 0, 1, 1, 0);

            // Any file with toolbox extention can be drag and droppped on the viewport.
            if (file.getPath().endsWith(".toolbox")) {
                if (ImGui.beginDragDropSource()) {
                    String itemPath = file.getPath();

                    ImGui.setDragDropPayload("VIEWPORT_DROPABLE", itemPath);

                    ImGui.endDragDropSource();
                }
            }// Drag and drop for properties panel for things like images, sounds and bla bla
//            else if(){
//
//            }

            ImGui.popStyleColor();
            if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
                if (file.isDirectory()) {
                    currentDirectory = new File(file.getPath());
                }
            }
            ImGui.textWrapped(file.getName());
            ImGui.nextColumn();

            ImGui.popID();
        }
        ImGui.columns(1);

        // For testing purposes
        float[] thumbArr = new float[]{thumbnailSize};
        if(ImGui.sliderFloat("thumbnailSize", thumbArr, 16, 512)){
            thumbnailSize = thumbArr[0];
        }

        float[] paddingArr = new float[]{padding};
        if(ImGui.sliderFloat("paddingSize", paddingArr,0, 32)){
            padding = paddingArr[0];
        }
        ImGui.end();
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }
}
