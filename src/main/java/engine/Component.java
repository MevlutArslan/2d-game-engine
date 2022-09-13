package engine;

import components.physics.RigidBody;
import components.rendering.SpriteRenderer;
import engine.rendering.DebugDraw;
import engine.ui.editor.CustomImGuiController;
import imgui.ImGui;
import imgui.type.ImInt;
import org.jbox2d.collision.ManifoldPoint;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component {

    public transient Entity parent;
    protected boolean allowForRemoval = true;

    // We are giving Components IDs for the sole purpose of loading them in properly
    private static long componentCounter = 0;
    // Same reason as in the Entity class
    private long componentId = -1;

    public void start() {

    }

    public static void init(long maxComponentCount) {
        componentCounter = maxComponentCount;
    }

    public void update(float deltaTime) {

    }

    public void onUpdateEditor(float deltaTime) {

    }

    public void generateComponentId() {
        if (componentId == -1) {
            this.componentId = componentCounter++;
        }
    }

    public long getComponentId() {
        return this.componentId;
    }

    // TODO : Create an annotation to indicate variables we want to expose. Maybe, not sure if possible.
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
        // I need try catch for the IllegalAccessException
        try {
            for (Field field : fields) {
                // We don't want to expose transient variables
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if (isTransient) {
                    continue;
                }

                if(this.parent.getComponent(RigidBody.class) != null && this.parent.getComponent(RigidBody.class).getBody().getFixtureList() == null){
                    if(field.getName().equals("density") || field.getName().equals("collisionGroup")){
                        continue;
                    }
                }

                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if (isPrivate) {
                    field.setAccessible(true);
                }

                Class type = field.getType();
                // https://stackoverflow.com/questions/39053175/how-to-find-the-access-modifier-of-a-member-using-java-reflection
                String modifier = Modifier.toString(field.getModifiers());

                // To Deal with private properties I want to modify I can make sure they have setters and modify them that way
                // without opening them up to reflection
                if (!modifier.equals("private final")) {
                    Object val = field.get(this);
                    String name = field.getName();
                    // https://stackoverflow.com/questions/3904579/how-to-capitalize-the-first-letter-of-a-string-in-java
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);

                    if (type == int.class) {
                        int value = (int) val;
                        // taken from https://github.com/codingminecraft/MarioYoutube/blob/69f043f765f32e494e758b9400f732d2f0c9b004/src/main/java/components/Component.java
                        field.set(this, CustomImGuiController.dragInt(name, value));
                    } else if (type == float.class) {
                        float value = (float) val;
                        // taken from https://github.com/codingminecraft/MarioYoutube/blob/69f043f765f32e494e758b9400f732d2f0c9b004/src/main/java/components/Component.java
                        field.set(this, CustomImGuiController.dragFloat(name, value));
                    } else if (type == Vector2f.class) {
                        Vector2f value = (Vector2f) val;
                        CustomImGuiController.drawVec2Control(name, value, 0);
                    } else if (type == Vector3f.class) {
                        Vector3f value = (Vector3f) val;
                        CustomImGuiController.drawVec3Control(name, value, 0);
                    } else if (type == Vector4f.class) {
                        Vector4f value = (Vector4f) val;

                        // TODO : seperate between Vec4 and Color by making a subcomponent that is ComponentHasColorField
                        float[] color = {value.x, value.y, value.z, value.w};
//                       if(ImGui.colorEdit4(name, color)){
//                           value.set(color[0], color[1], color[2], color[3]);
//                           if(this.getClass() == SpriteRenderer.class){
//                               ((SpriteRenderer) this).setHasChanged();
//                           }
//                       }
                        if (ImGui.colorPicker4("", color)) {
                            value.set(color[0], color[1], color[2], color[3]);
                            if (this.getClass() == SpriteRenderer.class) {
                                ((SpriteRenderer) this).setHasChanged();
                            }
                        }
                    } else if (type.isEnum()) {
                        String[] enumValues = getEnumValues(type);
                        String enumType = ((Enum)val).name();
                        ImInt index = new ImInt(indexOf(enumType, enumValues));
                        if(ImGui.combo(name, index, enumValues, enumValues.length)){
                            field.set(this, type.getEnumConstants()[index.get()]);
                        }
                    } else if (type == boolean.class) {
                        boolean value = (boolean)val;

                        field.set(this, CustomImGuiController.drawBoolean(name, value));
                    }

                    if (isPrivate) {
                        field.setAccessible(false);
                    }
                }
            }


        } catch (
                IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private <T extends Enum<T>> String[] getEnumValues(Class<T> enumType){
        String[] enumValues = new String[enumType.getEnumConstants().length];
        int i = 0;
        for(T enumIntegerValue : enumType.getEnumConstants()){
            enumValues[i] = enumIntegerValue.name();
            i++;
        }

        return enumValues;
    }

    private int indexOf(String str, String[] arr){
        for(int i = 0; i < arr.length; i++){
            if(str.equals(arr[i])){
                return i;
            }
        }

        return -1;
    }

    public void destroy() {

    }


    public boolean isAllowForRemoval() {
        return allowForRemoval;
    }

    public void setAllowForRemoval(boolean allowForRemoval) {
        this.allowForRemoval = allowForRemoval;
    }

    public void beginCollision(Entity collidingEntity, Contact contact, Vector2f contactNormal) {

    }

    public void endCollision(Entity collidingEntity, Contact contact, Vector2f contactNormal) {

    }

    public void preSolve(Entity collidingEntity, Contact contact, Vector2f contactNormal) {

    }

    public void postSolve(Entity collidingEntity, Contact contact, Vector2f contactNormal) {

    }
}
