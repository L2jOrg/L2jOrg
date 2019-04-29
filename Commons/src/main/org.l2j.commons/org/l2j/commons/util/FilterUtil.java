package org.l2j.commons.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class FilterUtil {

    private static final Pattern XML_PATTERN = Pattern.compile(".+\\.xml$", Pattern.CASE_INSENSITIVE);
    private static final Pattern HTML_PATTERN = Pattern.compile(".+\\.html?$", Pattern.CASE_INSENSITIVE);

    public static boolean xmlFilter(Path path) {
        return Files.isRegularFile(path) && XML_PATTERN.matcher(path.toString()).matches();
    }

    public static boolean xmlFilter(File file) {
        return file.isFile() && XML_PATTERN.matcher(file.getName()).matches();
    }

    public static boolean htmlFilter(Path path) {
        return Files.isRegularFile(path) && HTML_PATTERN.matcher(path.toString()).matches();
    }
}
