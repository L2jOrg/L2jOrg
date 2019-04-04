package org.l2j.commons.util;

import java.io.File;
import java.nio.file.Path;

public class FilterUtil {

    public static boolean xmlFilter(Path path) {
        return path.toString().endsWith(".xml");
    }

    public static boolean xmlFilter(File file) {
        return file.getName().endsWith(".xml");
    }
}
