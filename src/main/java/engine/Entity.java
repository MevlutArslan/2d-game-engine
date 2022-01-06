package engine;

import components.Transform;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class Entity {

    private String name;
    public Transform transform;

    private ArrayList<Component> components;

    // TODO : Turn every zIndex into ENUMS instead of using Integers
    // Like -> BACKGROUND_LAYER, PLAYABLE_LEVEL, EFFECTS...
    private int zIndex;

    // We need to seperate the entityCounter from the entityId
    // as when serializing & deserializing it will overlap and restart the counter if we keep it
    // all in a static variable
    private static long entityCounter = 0;
    // set to -1 to specify this id has not been set
    // to make sure we don't have duplicate components
    private long entityId = -1;

    private boolean shouldSerialize = true;

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

        // IF ANY PROBLEMS RELATED TO IDS CHECK HERE FIRST
        this.entityId = entityCounter++;
    }

    public static void init(long maxEntityCount){
        entityCounter = maxEntityCount;
    }

    public void start(){
        for(int i = 0; i < components.size(); i++){
            components.get(i).start();
        }
    }

    public void update(float deltaTime){
        for(int i = 0; i < components.size(); i++){
            components.get(i).update(deltaTime);
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
        // if the component already has an id it wont generate a new one
        // which means it has been loaded in
        component.generateComponentId();
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

    public long getEntityId(){
        return this.entityId;
    }

    public List<Component> getAllComponents() {
        return this.components;
    }

    public void setNoSerialize(){
        this.shouldSerialize = false;
    }

    public boolean getShouldSerialize() {
        return this.shouldSerialize;
    }
}
