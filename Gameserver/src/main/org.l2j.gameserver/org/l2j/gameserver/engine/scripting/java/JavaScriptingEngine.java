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

import org.l2j.gameserver.engine.scripting.AbstractScriptingEngine;
import org.l2j.gameserver.engine.scripting.IExecutionContext;

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
        super("Java Engine", System.getProperty("java.specification.version"), "java");
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