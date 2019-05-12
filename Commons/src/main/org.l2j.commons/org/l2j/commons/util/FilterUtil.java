package org.l2j.commons.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;

public class FilterUtil {

    private static final Pattern XML_PATTERN = Pattern.compile(".+\\.xml$", Pattern.CASE_INSENSITIVE);
    private static final Pattern HTML_PATTERN = Pattern.compile(".+\\.html?$", Pattern.CASE_INSENSITIVE);
    private static final Pattern JAVA_PATTERN = Pattern.compile(".+\\.java?$", Pattern.CASE_INSENSITIVE);

    public static boolean xmlFile(Path path) {
        return Files.isRegularFile(path) && XML_PATTERN.matcher(path.toString()).matches();
    }

    public static boolean xmlFile(File file) {
        return nonNull(file) && file.isFile() && XML_PATTERN.matcher(file.getName()).matches();
    }

    public static boolean htmlFile(Path path) {
        return Files.isRegularFile(path) && HTML_PATTERN.matcher(path.toString()).matches();
    }

    public static boolean javaFile(Path path) {
        return Files.isRegularFile(path) && JAVA_PATTERN.matcher(path.toString()).matches();
    }
}
