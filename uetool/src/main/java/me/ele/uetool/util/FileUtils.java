package me.ele.uetool.util;

import java.io.File;
import me.ele.uetool.MenuHelper;
import me.ele.uetool.VEnv;

public class FileUtils {
    public static void deleteAllUEFiles() {
        new File(VEnv.DIR + "/"
                + MenuHelper.Type.TYPE_EDIT_ATTR).delete();
        new File(VEnv.DIR + "/"
                + MenuHelper.Type.TYPE_RELATIVE_POSITION).delete();
        new File(VEnv.DIR + "/"
                + MenuHelper.Type.TYPE_SHOW_GRIDDING).delete();
        new File(VEnv.DIR + "/"
                + MenuHelper.Type.TYPE_LAYOUT_LEVEL).delete();
    }
}
