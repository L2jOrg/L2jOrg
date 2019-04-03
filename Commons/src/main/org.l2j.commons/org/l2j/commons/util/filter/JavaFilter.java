package org.l2j.commons.util.filter;

import java.nio.file.Path;

public class JavaFilter  {
    public static boolean accept(Path path) {
        return path.toString().endsWith(".java");
    }
}
