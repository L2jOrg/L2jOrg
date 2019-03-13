package org.l2j.gameserver.scripting.java;

import javax.tools.*;
import javax.tools.JavaFileObject.Kind;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

/**
 * @author HorridoJoho
 */
final class ScriptingFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private final LinkedList<ScriptingOutputFileObject> _classOutputs = new LinkedList<>();

    public ScriptingFileManager(JavaFileManager wrapped) {
        super(wrapped);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
        var javaFileObject = super.getJavaFileForOutput(location, className, kind, sibling);

        if (kind == Kind.CLASS) {
            if (className.contains("/")) {
                className = className.replace('/', '.');
            }

            var scriptFileInfo = new ScriptingOutputFileObject(sibling, className, this.inferModuleName(location));
            _classOutputs.add(scriptFileInfo);
        }
        return javaFileObject;
    }


    Iterable<ScriptingOutputFileObject> getCompiledClasses() {
        return Collections.unmodifiableCollection(_classOutputs);
    }
}
