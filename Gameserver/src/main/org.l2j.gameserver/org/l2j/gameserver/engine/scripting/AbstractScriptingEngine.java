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
