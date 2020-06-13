/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
