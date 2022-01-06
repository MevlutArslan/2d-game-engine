package engine;

import imgui.ImGui;
import imgui.type.ImInt;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public abstract class Component {

    public transient Entity parent;

    // We are giving Components IDs for the sole purpose of loading them in properly
    private static long componentCounter = 0;
    // Same reason as in the Entity class
    private long componentId = -1;

    public abstract void start();

    public static void init(long maxComponentCount) {
        componentCounter = maxComponentCount;
    }

    public abstract void update(float deltaTime);

    public void generateComponentId(){
        if(componentId == -1){
            this.componentId = componentCounter++;
        }
    }

    public long getComponentId(){
        return this.componentId;
    }

    public void imgui() {
        // I want every component to have their section of properties like in unity
        // https://www.google.com/search?q=unity+editor&sxsrf=AOaemvKNmrmYvj9anvATPDTdsJ2DidQibQ:1638689004341&source=lnms&tbm=isch&sa=X&ved=2ahUKEwiwuYCgkMz0AhVKpYsKHSuuDjQQ_AUoAXoECAEQAw&biw=1440&bih=789&dpr=2#imgrc=sL7EkGw2cSloMM
        // ENTITY
        //  -> Component
        //      -> Property
        //  -> Component
        //  -> Component
        //  -> Component

        // Java Reflection api can be used for getting the properties and their properties
        // https://www.baeldung.com/java-reflection
        Field[] fields = this.getClass().getDeclaredFields();

        // I need to create widgets according to fields type
        // Only for public variables

        // I need try catch for the IllegalAccessException
        try {
            for (Field field : fields) {
                Class type = field.getType();
                // https://stackoverflow.com/questions/39053175/how-to-find-the-access-modifier-of-a-member-using-java-reflection
                String modifier = Modifier.toString(field.getModifiers());

                // To Deal with private properties I want to modify I can make sure they have setters and modify them that way
                // without opening them up to reflection
                if(!modifier.equals("private") && !modifier.equals("private transient") && !modifier.equals("private final")){
                    Object val = field.get(this);
                    String name = field.getName();


                   if(type == int.class){
                       int value = (int) val;
                       int[] imInt = {value};
                       // apparently inputInt requires imInt whereas dragInt
                       // requires an int array with length 1
                       if(ImGui.dragInt(name, imInt)){
                           field.set(this, imInt[0]);
                       }
                   }
                   else if(type == float.class){
                       float value = (float) val;
                       float[] imFloat = {value};
                        if(ImGui.dragFloat(name, imFloat)){
                            field.set(this, imFloat[0]);
                        }
                    }
                   else if(type == Vector2f.class){
                       Vector2f value = (Vector2f) val;
                       float[] imVec2f = {value.x, value.y};
                       if(ImGui.dragFloat2(name, imVec2f)){
                           value.set(imVec2f[0], imVec2f[1]);
                           field.set(this, value);
                       }
                   }
                   else if(type == Vector3f.class){
                       Vector3f value = (Vector3f) val;
                       float[] imVec3f = {value.x, value.y, value.z};

                       if(ImGui.dragFloat3(name, imVec3f)){
                           value.set(imVec3f[0], imVec3f[1], imVec3f[2]);
                           field.set(this, value);
                       }
                   }
                   else if(type == Vector4f.class){
                       Vector4f value = (Vector4f) val;
                       float[] imVec4f = {value.x, value.y, value.z, value.w};
                       if(ImGui.dragFloat4(name, imVec4f)){
                           value.set(imVec4f[0], imVec4f[1], imVec4f[2], imVec4f[3]);
                           field.set(this, value);
                       }
                   }
                }

            }


        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
