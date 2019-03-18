package org.l2j.gameserver.scripting.java;

import org.l2j.gameserver.scripting.AbstractScriptingEngine;
import org.l2j.gameserver.scripting.IExecutionContext;

import javax.lang.model.SourceVersion;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.util.Arrays;

import static java.util.Objects.isNull;

/**
 * @author HorridoJoho, Mobius
 */
public final class JavaScriptingEngine extends AbstractScriptingEngine {
    private volatile JavaCompiler compiler;

    public JavaScriptingEngine() {
        super("Java Engine", "11", "java");
    }

    private void determineCompilerOrThrow() {
        compiler = ToolProvider.getSystemJavaCompiler();

        if (isNull(compiler)) {
            throw new IllegalStateException("No JavaCompiler service installed!");
        }
    }

    private synchronized void ensureCompilerOrThrow() {
        if (isNull(compiler)) {
            determineCompilerOrThrow();
        }
    }

    JavaCompiler getCompiler() {
        return compiler;
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
        return Arrays.deepToString(compiler.getSourceVersions().toArray(SourceVersion[]::new)).replace("RELEASE_", "");
    }
}