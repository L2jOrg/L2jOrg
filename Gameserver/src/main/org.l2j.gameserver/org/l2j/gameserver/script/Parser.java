package org.l2j.gameserver.script;

import org.w3c.dom.Node;

import javax.script.ScriptContext;

/**
 * @author Luis Arias
 */
public abstract class Parser {
    public abstract void parseScript(Node node, ScriptContext context);
}
