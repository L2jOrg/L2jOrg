package org.l2j.commons.util;

import java.nio.file.Path;

public class FilterUtil {
    public static boolean xmlFilter(Path path) {
        return path.toString().endsWith(".xml");
    }
}
