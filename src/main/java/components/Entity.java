package components;

import java.util.ArrayList;

public class Entity {

    private ArrayList<Component> components;
    private String name;

    public Transform transform;

    public Entity(String name){
        this.name = name;
        this.components = new ArrayList<>();
        this.transform = new Transform();
    }

    public Entity(String name, Transform transform){
        this.name = name;
        this.components = new ArrayList<>();
        this.transform = transform;
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
        this.getComponent(componentClass).parent = null;
        this.components.remove(componentClass);
    }

    public void addComponent(Component component){
        this.components.add(component);
        component.parent = this;
    }



}
