package engine.utility.file_utility;

import engine.GameWindow;
import engine.utility.Constants;
import org.lwjgl.PointerBuffer;

import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.util.nfd.NativeFileDialog.*;

// TODO
public class FileDialogManager {

    // the filter is so that we can specify which type of files we want to show in the dialog
    // Example : enginename.scene
    //           enginename.entity
    //           enginename.script

    /**
     * @param filter is expected to be in Applescript format
     *              "png,jpg,pdf;customengine"
     */
    public static String openFile(String filter) {
        String selectedFilePath = "";
        PointerBuffer outPath = memAllocPointer(1);

        try {
            if (checkResult(NFD_OpenDialog(filter, "", outPath))) {
                selectedFilePath = outPath.getStringUTF8(0);
                nNFD_Free(outPath.get(0));
            }
        } finally {
            memFree(outPath);
        }

        return selectedFilePath;
    }

    public static void saveFile(){
        PointerBuffer savePath = memAllocPointer(1);

        try{
            if(checkResult(NFD_SaveDialog(Constants.SCENE_FILE_EXTENSION, "", savePath))){
                String path = savePath.getStringUTF8(0);
                GameWindow.getScene().saveAs(path);
                nNFD_Free(savePath.get(0));
            }
        }finally {
            memFree(savePath);
        }
    }

    private static boolean checkResult(int executionCode){
//      NFD_OKAY ,NFD_CANCEL, NFD_ERROR
        return executionCode == NFD_OKAY;
    }
}
