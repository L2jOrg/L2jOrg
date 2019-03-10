/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.scripting;

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
        if ((engineName == null) || engineName.isEmpty() || (engineVersion == null) || engineVersion.isEmpty() || (commonFileExtensions == null) || (commonFileExtensions.length == 0)) {
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
