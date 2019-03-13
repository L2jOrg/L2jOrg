package org.l2j.gameserver.scripting.java;

/**
 * @author HorridoJoho
 */
public final class JavaCompilerException extends Exception {
    public JavaCompilerException(String diagnostics) {
        super(diagnostics);
    }
}
