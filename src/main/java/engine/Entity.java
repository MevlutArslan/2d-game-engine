package engine;

import components.Transform;
import imgui.ImGui;

import java.util.ArrayList;

public class Entity {

    private String name;
    public Transform transform;

    private ArrayList<Component> components;

    // TODO : Turn every zIndex into ENUMS instead of using Integers
    // Like -> BACKGROUND_LAYER, PLAYABLE_LEVEL, EFFECTS...
    private int zIndex;

    public Entity(String name){
        this.name = name;
        this.components = new ArrayList<>();
        this.transform = new Transform();
        this.zIndex = 0;
    }

    public Entity(String name, Transform transform, int zIndex){
        this.name = name;
        this.components = new ArrayList<>();
        this.transform = transform;
        this.zIndex = zIndex;
    }

    public void start(){
        for(Component component : components){
            component.start();
        }
    }

    public void update(float deltaTime){
        for(Component component : components){
            component.update(deltaTime);
        }
    }

    public <T extends Component>T getComponent(Class<T> componentClass){
        for(Component component : components){
            if(componentClass.isAssignableFrom(component.getClass())){
                try{
                    return componentClass.cast(component);
                }catch (Exception e){
                    System.err.println("Error : casting Component");
                }
            }
        }
        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass){
        if(components.contains(componentClass)){
            this.getComponent(componentClass).parent = null;
            this.components.remove(componentClass);
        }
    }

    public void addComponent(Component component){
        this.components.add(component);
        component.parent = this;
    }

    public int getzIndex(){
        return this.zIndex;
    }

    public void imgui(){
        for(Component c : components){
            if(c.getClass().getDeclaredFields().length > 0){
                if(ImGui.collapsingHeader(c.getClass().getSimpleName())){
                    c.imgui();
                }
            }
        }
    }
}
