
import engine.GameWindow;

import static org.lwjgl.system.MemoryUtil.memFree;

//import org.lwjgl.util.nfd.NativeFileDialog

public class Main {

    public static void main(String[] args) {

        GameWindow.get().run();
        // works
//        System.out.println(FileDialogManager.openFile("png,jpg,pdf,customengine"));
//        FileDialogManager.saveFile("DSayoay");
    }
}
