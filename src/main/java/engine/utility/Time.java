package engine.utility;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Time {
    public static float timeStarted = (float)glfwGetTime();
    // Time since our application started as static
    // variables get assigned at the start of the application
    public static float getTime(){
        // 1E-9 = 1 × 10⁻⁹ we need it for converting nanosecond to second
        return (float) (((float)glfwGetTime() - timeStarted) * 1E-9);
    }


}