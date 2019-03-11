package org.l2j.gameserver.scripting.java;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import java.io.*;
import java.net.URI;
import java.nio.file.Path;

/**
 * @author HorridoJoho
 */
final class ScriptingOutputFileObject implements JavaFileObject {
    private final Path _sourcePath;
    private final String _javaName;
    private final String _javaSimpleName;
    private final ByteArrayOutputStream _out;

    public ScriptingOutputFileObject(Path sourcePath, String javaName, String javaSimpleName) {
        _sourcePath = sourcePath;
        _javaName = javaName;
        _javaSimpleName = javaSimpleName;
        _out = new ByteArrayOutputStream();
    }

    public Path getSourcePath() {
        return _sourcePath;
    }

    public String getJavaName() {
        return _javaName;
    }

    public String getJavaSimpleName() {
        return _javaSimpleName;
    }

    public byte[] getJavaData() {
        return _out.toByteArray();
    }

    @Override
    public URI toUri() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public InputStream openInputStream() {
        return null;
    }

    @Override
    public OutputStream openOutputStream() {
        return _out;
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) {
        return null;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return null;
    }

    @Override
    public Writer openWriter() {
        return null;
    }

    @Override
    public long getLastModified() {
        return 0;
    }

    @Override
    public boolean delete() {
        return false;
    }

    @Override
    public Kind getKind() {
        return Kind.CLASS;
    }

    @Override
    public boolean isNameCompatible(String simpleName, Kind kind) {
        return (kind == Kind.CLASS) && (_javaSimpleName == simpleName);
    }

    @Override
    public NestingKind getNestingKind() {
        return NestingKind.TOP_LEVEL;
    }

    @Override
    public Modifier getAccessLevel() {
        return null;
    }

}
