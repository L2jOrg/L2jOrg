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
package org.l2j.gameserver.engine.scripting.java;

import org.l2j.commons.util.Util;

import javax.tools.*;
import javax.tools.JavaFileObject.Kind;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
final class ScriptingFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    private final Map<Path, ScriptingFileInfo> scriptsFileInfo = new HashMap<>();
    private final Set<String> moduleNames = new HashSet<>();

    ScriptingFileManager(StandardJavaFileManager fileManager) {
        super(fileManager);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
        var javaFileObject = super.getJavaFileForOutput(location, className, kind, sibling);

        if (kind == Kind.CLASS) {
            if (className.contains("/")) {
                className = className.replace('/', '.');
            }

            var scriptPath = Path.of(sibling.getName());
            var moduleName = inferModuleName(location);
            var scriptFileInfo = new ScriptingFileInfo(scriptPath, className, moduleName, location);

            scriptsFileInfo.put(scriptPath, scriptFileInfo);
            if(!Util.isNullOrEmpty(moduleName)) {
                moduleNames.add(moduleName);
            }
        }
        return javaFileObject;
    }

    Set<String> getModuleNames() {
        return moduleNames;
    }

    ScriptingFileInfo getScriptInfo(Path scriptPath) {
        return scriptsFileInfo.get(scriptPath);
    }

    Iterable<? extends JavaFileObject> getJavaFileObjectsFromPaths(Collection<Path> paths) {
        return fileManager.getJavaFileObjectsFromPaths(paths);
    }

    boolean beAwareOfObjectFile(Path path, Path compiled) throws IOException {
        var filesObject = getJavaFileObjectsFromPaths(Collections.singletonList(compiled));
        var it = filesObject.iterator();
        if(it.hasNext()) {
            var javaFileObject = it.next();
            var classLocation = getLocationForModule(StandardLocation.CLASS_OUTPUT, javaFileObject);
            var module = inferModuleName(classLocation);
            var parentPath = compiled.getParent();

            while (nonNull(parentPath) && !parentPath.getFileName().toString().equals(module)) {
                parentPath = parentPath.getParent();
            }

            if(isNull(parentPath)) {
                return false;
            }

            var className = parentPath.relativize(compiled).toString().replace(".class", "").replace(File.separator, ".");
            scriptsFileInfo.putIfAbsent(path, new ScriptingFileInfo(path, className, module, classLocation));
            return true;
        }
        return false;
    }
}
