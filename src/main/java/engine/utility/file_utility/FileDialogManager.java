package engine.utility.file_utility;

import org.lwjgl.PointerBuffer;

import java.awt.*;
import java.io.*;
import java.util.Locale;

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
            if (NFD_OpenDialog(filter, "", outPath) == NFD_OKAY) {
                selectedFilePath = outPath.getStringUTF8(0);
                nNFD_Free(outPath.get(0));
            }
        } finally {
            memFree(outPath);
        }

        return selectedFilePath;
    }

}
