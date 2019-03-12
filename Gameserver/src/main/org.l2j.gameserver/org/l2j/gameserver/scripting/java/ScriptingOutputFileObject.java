package org.l2j.gameserver.scripting.java;

import javax.tools.FileObject;
import java.nio.file.Path;

/**
 * @author HorridoJoho
 */
final class ScriptingOutputFileObject  {
    private final Path _sourcePath;
    private final String _javaName;
    private final String moduleName;

    public ScriptingOutputFileObject(FileObject sibling, String javaName, String moduleName) {
        _sourcePath = sibling != null ? Path.of(sibling.getName()) : null;
        _javaName = javaName;
        this.moduleName = moduleName;
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
}
