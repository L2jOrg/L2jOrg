package org.l2j.gameserver.engine.scripting.java;

import javax.tools.JavaFileManager.Location;
import java.nio.file.Path;

final class ScriptingFileInfo {
    private final Path _sourcePath;
    private final String _javaName;
    private final String moduleName;
    private final Location location;

    public ScriptingFileInfo(Path scriptPath, String javaName, String moduleName, Location location) {
        _sourcePath = scriptPath;
        _javaName = javaName;
        this.moduleName = moduleName;
        this.location = location;
    }

    public Path getSourcePath() {
        return _sourcePath;
    }

    public String getJavaName() {
        return _javaName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public Location getLocation() {
        return location;
    }
}
