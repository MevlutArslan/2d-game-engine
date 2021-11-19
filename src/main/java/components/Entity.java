package components;

import java.util.ArrayList;

public class Entity {

    public ArrayList<Component> components;
    public String name;

    public Entity(String name){
        this.name = name;
        this.components = new ArrayList<>();
    }

    public void start(){
        for(Component component : components){
            component.start();
        }
    }

    public void update(float deltaTime){
        for(Component component : components){
            component.update(this, deltaTime);
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
        this.components.remove(componentClass);
    }

    public void addComponent(Component component){
        this.components.add(component);
    }



}
