package org.l2j.gameserver.scripting;

import org.l2j.commons.util.Util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HorridoJoho
 */
public abstract class AbstractScriptingEngine implements IScriptingEngine {
    private final String _engineName;
    private final String _engineVersion;
    private final String[] _commonFileExtensions;
    private final Map<String, String> _properties;

    protected AbstractScriptingEngine(String engineName, String engineVersion, String... commonFileExtensions) {
        if (Util.isNullOrEmpty(engineName) || Util.isNullOrEmpty(engineVersion) || (commonFileExtensions == null) || (commonFileExtensions.length == 0)) {
            throw new IllegalArgumentException();
        }
        _engineName = engineName;
        _engineVersion = engineVersion;
        _commonFileExtensions = commonFileExtensions;
        _properties = new HashMap<>();
    }

    @Override
    public final String setProperty(String key, String value) {
        return _properties.put(key, value);
    }

    @Override
    public final String getProperty(String key) {
        return _properties.get(key);
    }

    @Override
    public final String getEngineName() {
        return _engineName;
    }

    @Override
    public final String getEngineVersion() {
        return _engineVersion;
    }

    @Override
    public final String[] getCommonFileExtensions() {
        return Arrays.copyOf(_commonFileExtensions, _commonFileExtensions.length);
    }
}
