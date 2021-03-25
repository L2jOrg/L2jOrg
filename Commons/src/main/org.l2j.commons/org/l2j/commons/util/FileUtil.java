/*
 * Copyright © 2019-2021 L2JOrg
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.newBufferedReader;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlison
 */
public class FileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    public static URL readToURL(String filePath) {
        URL url = pathToURL(filePath);
        return nonNull(url) ? url : readURLFromResources(filePath);
    }

    private static URL pathToURL(String filePath) {
        try {
            var path = Path.of(filePath);
            if (Files.isRegularFile(path)) {
                return path.toUri().toURL();
            }
        } catch (MalformedURLException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    private static URL readURLFromResources(String filePath) {
        var resource = ClassLoader.getSystemResource(filePath);
        return nonNull(resource) ? resource : FileUtil.class.getResource(filePath);
    }

    public static URI readToURI(String filePath) {
        var path = Path.of(filePath);
        if (Files.isRegularFile(path)) {
            return path.toUri();
        }
        return readURIFromResources(filePath);
    }

    private static URI readURIFromResources(String filePath) {
        var resource = UrlToURI(ClassLoader.getSystemResource(filePath));
        return nonNull(resource) ? resource :  UrlToURI(FileUtil.class.getResource(filePath));
    }

    private static URI UrlToURI(URL url) {
        try {
            return nonNull(url) ? url.toURI() : null;
        } catch (URISyntaxException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    public static String resolveFilePath(String filePath) {
        var path = Path.of(filePath);
        if(isRegularFile(path)) {
            return filePath;
        }
        return resolvePathFromResources(filePath);
    }

    private static String resolvePathFromResources(String filePath) {
        var resource = toPath(ClassLoader.getSystemResource(filePath));
        if(isNull(resource)) {
            resource = toPath(FileUtil.class.getResource(filePath));
        }
        return isNull(resource) ? filePath : resource;
    }

    private static String toPath(URL url) {
        return nonNull(url) ? url.getPath() : null;
    }

    public static BufferedReader reader(String filePath) throws IOException {
        var path = Path.of(filePath);
        if(Files.isRegularFile(path)) {
            return newBufferedReader(path);
        }
        return readerFromResources(filePath);
    }

    private static BufferedReader readerFromResources(String filePath) {
        var input = ClassLoader.getSystemResourceAsStream(filePath);
        if(isNull(input)) {
            input = FileUtil.class.getResourceAsStream(filePath);
        }
        return nonNull(input) ? new BufferedReader(new InputStreamReader(input)) : null;
    }

    public static InputStream stream(String filePath) {
        var stream = filePathToStream(filePath);
        return nonNull(stream) ? stream : resourceStream(filePath);
    }

    private static InputStream filePathToStream(String filePath) {
        try {
            var path = Path.of(filePath);
            if (Files.isRegularFile(path)) {
                return Files.newInputStream(path);
            }
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    private static InputStream resourceStream(String filePath) {
        var resource = ClassLoader.getSystemResourceAsStream(filePath);
        return nonNull(resource) ? resource : FileUtil.class.getResourceAsStream(filePath);
    }

    public static Path resolvePath(String filePath) {
        var path = Path.of(filePath);
        if(Files.isRegularFile(path)) {
            return path;
        }
        return pathFromResources(filePath);
    }

    private static Path pathFromResources(String filePath) {
        return Path.of(resolveFilePath(filePath));
    }
}