package org.l2j.gameserver.engine.scripting;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @param <T>
 * @author HorridoJoho
 */
public abstract class AbstractExecutionContext<T extends IScriptingEngine> implements IExecutionContext {
    private final T _engine;
    private final Map<String, String> _properties;
    private volatile Path _currentExecutingScipt;

    protected AbstractExecutionContext(T engine) {
        if (engine == null) {
            throw new IllegalArgumentException();
        }
        _engine = engine;
        _properties = new HashMap<>();
    }

    @Override
    public final String setProperty(String key, String value) {
        return _properties.put(key, value);
    }

    @Override
    public final String getProperty(String key) {
        if (!_properties.containsKey(key)) {
            return _engine.getProperty(key);
        }
        return _properties.get(key);
    }

    @Override
    public final Path getCurrentExecutingScript() {
        return _currentExecutingScipt;
    }

    protected final void setCurrentExecutingScript(Path currentExecutingScript) {
        _currentExecutingScipt = currentExecutingScript;
    }

    @Override
    public final T getScriptingEngine() {
        return _engine;
    }
}
