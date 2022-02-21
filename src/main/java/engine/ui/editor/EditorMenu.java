package engine.ui.editor;

import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class EditorMenu extends EditorComponent {

    private final List<EditorComponent> menuList = new ArrayList<>();

    public EditorMenu(){

    }

    @Override
    public void update(float deltaTime) {
        if(ImGui.beginMenuBar()){
            for(int i = 0; i < menuList.size(); i++){
                menuList.get(i).update(deltaTime);
            }
            
            ImGui.endMenuBar();
        }

    }

    public void addEditorMenu(EditorComponent newMenu){
        if(newMenu != null){
            menuList.add(newMenu);
        }
    }


}
