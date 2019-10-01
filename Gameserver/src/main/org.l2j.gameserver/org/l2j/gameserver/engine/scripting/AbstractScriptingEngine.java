package org.l2j.gameserver.engine.scripting;

import org.l2j.commons.util.Util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HorridoJoho
 */
public abstract class AbstractScriptingEngine implements IScriptingEngine {

    private final String name;
    private final String version;
    private final String[] fileExtension;
    private final Map<String, String> properties;

    protected AbstractScriptingEngine(String engineName, String engineVersion, String... commonFileExtensions) {
        if (Util.isNullOrEmpty(engineName) || Util.isNullOrEmpty(engineVersion) || (commonFileExtensions == null) || (commonFileExtensions.length == 0)) {
            throw new IllegalArgumentException();
        }
        name = engineName;
        version = engineVersion;
        fileExtension = commonFileExtensions;
        properties = new HashMap<>();
    }

    @Override
    public final String setProperty(String key, String value) {
        return properties.put(key, value);
    }

    @Override
    public final String getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public final String getEngineName() {
        return name;
    }

    @Override
    public final String getEngineVersion() {
        return version;
    }

    @Override
    public final String[] getCommonFileExtensions() {
        return Arrays.copyOf(fileExtension, fileExtension.length);
    }
}
