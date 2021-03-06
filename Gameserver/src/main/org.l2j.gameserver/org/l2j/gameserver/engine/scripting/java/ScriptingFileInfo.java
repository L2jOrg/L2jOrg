/*
 * Copyright © 2019-2021 L2JOrg
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

import javax.tools.JavaFileManager.Location;
import java.nio.file.Path;

final class ScriptingFileInfo {
    private final Path _sourcePath;
    private final String _javaName;
    private final String moduleName;
    private final Location location;

    public ScriptingFileInfo(Path scriptPath, String javaName, String moduleName, Location location) {
        _sourcePath = scriptPath;
        _javaName = javaName;
        this.moduleName = moduleName;
        this.location = location;
    }

    public Path getSourcePath() {
        return _sourcePath;
    }

    public String getJavaName() {
        return _javaName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public Location getLocation() {
        return location;
    }
}
