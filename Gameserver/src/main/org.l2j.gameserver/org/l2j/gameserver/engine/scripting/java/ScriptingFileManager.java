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
