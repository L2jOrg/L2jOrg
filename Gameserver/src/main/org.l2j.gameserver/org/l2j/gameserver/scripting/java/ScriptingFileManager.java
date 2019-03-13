package org.l2j.gameserver.scripting.java;

import org.l2j.commons.util.Util;

import javax.tools.*;
import javax.tools.JavaFileObject.Kind;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

final class ScriptingFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    private final Map<Path, ScriptingFileInfo> scriptsFileInfo = new HashMap<>();
    private final Set<String> moduleNames = new HashSet<>();

    public ScriptingFileManager(StandardJavaFileManager fileManager) {
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

    public Set<String> getModuleNames() {
        return moduleNames;
    }

    public ScriptingFileInfo getScriptInfo(Path scriptPath) {
        return scriptsFileInfo.get(scriptPath);
    }

    public Iterable<? extends JavaFileObject> getJavaFileObjectsFromPaths(Iterable<Path> paths) {
        return fileManager.getJavaFileObjectsFromPaths(paths);
    }
}
