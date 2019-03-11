package org.l2j.gameserver.scripting.java;

import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author HorridoJoho
 */
final class ScriptingFileManager implements StandardJavaFileManager {
    private final StandardJavaFileManager _wrapped;
    private final LinkedList<ScriptingOutputFileObject> _classOutputs = new LinkedList<>();

    public ScriptingFileManager(StandardJavaFileManager wrapped) {
        _wrapped = wrapped;
    }

    Iterable<ScriptingOutputFileObject> getCompiledClasses() {
        return Collections.unmodifiableCollection(_classOutputs);
    }

    @Override
    public int isSupportedOption(String option) {
        return _wrapped.isSupportedOption(option);
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return _wrapped.getClassLoader(location);
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException {
        return _wrapped.list(location, packageName, kinds, recurse);
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        return _wrapped.inferBinaryName(location, file);
    }

    @Override
    public boolean isSameFile(FileObject a, FileObject b) {
        return _wrapped.isSameFile(a, b);
    }

    @Override
    public boolean handleOption(String current, Iterator<String> remaining) {
        return _wrapped.handleOption(current, remaining);
    }

    @Override
    public boolean hasLocation(Location location) {
        return _wrapped.hasLocation(location);
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException {
        return _wrapped.getJavaFileForInput(location, className, kind);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
        if (kind != Kind.CLASS) {
            return _wrapped.getJavaFileForOutput(location, className, kind, sibling);
        }

        if (className.contains("/")) {
            className = className.replace('/', '.');
        }

        ScriptingOutputFileObject fileObject;
        if (sibling != null) {
            fileObject = new ScriptingOutputFileObject(Paths.get(sibling.getName()), className, className.substring(className.lastIndexOf('.') + 1));
        } else {
            fileObject = new ScriptingOutputFileObject(null, className, className.substring(className.lastIndexOf('.') + 1));
        }

        _classOutputs.add(fileObject);
        return fileObject;
    }

    @Override
    public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
        return _wrapped.getFileForInput(location, packageName, relativeName);
    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
        return _wrapped.getFileForOutput(location, packageName, relativeName, sibling);
    }

    @Override
    public void flush() throws IOException {
        _wrapped.flush();
    }

    @Override
    public void close() throws IOException {
        _wrapped.close();
    }

    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(Iterable<? extends File> files) {
        return _wrapped.getJavaFileObjectsFromFiles(files);
    }

    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjects(File... files) {
        return _wrapped.getJavaFileObjects(files);
    }

    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjectsFromStrings(Iterable<String> names) {
        return _wrapped.getJavaFileObjectsFromStrings(names);
    }

    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjects(String... names) {
        return _wrapped.getJavaFileObjects(names);
    }

    @Override
    public void setLocation(Location location, Iterable<? extends File> path) throws IOException {
        _wrapped.setLocation(location, path);

    }

    @Override
    public Iterable<? extends File> getLocation(Location location) {
        return _wrapped.getLocation(location);
    }
}
