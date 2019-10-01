package org.l2j.gameserver.engine.scripting.java;

/**
 * @author HorridoJoho
 */
public final class JavaCompilerException extends Exception {
    public JavaCompilerException(String diagnostics) {
        super(diagnostics);
    }
}
