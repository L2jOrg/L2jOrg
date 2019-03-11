package org.l2j.gameserver.scripting.java;

import org.l2j.gameserver.scripting.AbstractScriptingEngine;
import org.l2j.gameserver.scripting.IExecutionContext;

import javax.lang.model.SourceVersion;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.util.Arrays;

/**
 * @author HorridoJoho, Mobius
 */
public final class JavaScriptingEngine extends AbstractScriptingEngine {
    private volatile JavaCompiler _compiler;

    public JavaScriptingEngine() {
        super("Java Engine", "10", "java");
    }

    private void determineCompilerOrThrow() {
        if (_compiler == null) {
            _compiler = ToolProvider.getSystemJavaCompiler();
        }

        if (_compiler == null) {
            throw new IllegalStateException("No JavaCompiler service installed!");
        }
    }

    private void ensureCompilerOrThrow() {
        if (_compiler == null) {
            synchronized (this) {
                if (_compiler == null) {
                    determineCompilerOrThrow();
                }
            }
        }
    }

    JavaCompiler getCompiler() {
        return _compiler;
    }

    @Override
    public IExecutionContext createExecutionContext() {
        ensureCompilerOrThrow();
        return new JavaExecutionContext(this);
    }

    @Override
    public String getLanguageName() {
        return "Java";
    }

    @Override
    public String getLanguageVersion() {
        ensureCompilerOrThrow();
        return Arrays.deepToString(_compiler.getSourceVersions().toArray(new SourceVersion[0])).replace("RELEASE_", "");
    }
}