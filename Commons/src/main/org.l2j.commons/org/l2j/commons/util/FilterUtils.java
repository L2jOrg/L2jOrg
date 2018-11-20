package org.l2j.commons.util;

import java.io.File;

public class FilterUtils {

    public static boolean htmlFilter(File file) {
        if (!file.isDirectory()) {
            return (file.getName().endsWith(".htm") || file.getName().endsWith(".html"));
        }
        return true;
    }
}
