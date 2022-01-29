package engine;

import components.Transform;
import engine.ui.editor.CustomImGuiController;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.ArrayList;
import java.util.List;

public class Entity {

    private String name;

    private ArrayList<Component> components;

    private int zIndex;
    private boolean isDead = false;
    public Transform transform;

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

    public void onUpdateEditor(float deltaTime) {
        for(int i = 0; i < components.size(); i++){
            components.get(i).onUpdateEditor(deltaTime);
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
        for(int i = 0; i < components.size(); i++){
            Component component = components.get(i);
            if(componentClass.isAssignableFrom(component.getClass())){
                this.components.remove(component);
                return;
            }
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
        transform.imgui();
        for(int i = 0; i < components.size(); i++){
            Component c = components.get(i);
            if(c.getClass().getDeclaredFields().length > 0){
                CustomImGuiController.drawComponent(c, c.getClass().getSimpleName(), this, c.isAllowForRemoval());
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

    public void destroy(){
        this.isDead = true;
        for (int i=0; i < components.size(); i++) {
            components.get(i).destroy();
        }
    }

    public boolean isDead(){
        return this.isDead;
    }


}
