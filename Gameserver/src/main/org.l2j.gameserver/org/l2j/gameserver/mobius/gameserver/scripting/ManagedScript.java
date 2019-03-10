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

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class for classes that are meant to be implemented by scripts.<BR>
 *
 * @author KenM
 */
public abstract class ManagedScript {
    private static final Logger LOGGER = Logger.getLogger(ManagedScript.class.getName());

    private final Path _scriptFile;
    private long _lastLoadTime;
    private boolean _isActive;

    public ManagedScript() {
        _scriptFile = getScriptPath();
        setLastLoadTime(System.currentTimeMillis());
    }

    public abstract Path getScriptPath();

    /**
     * Attempts to reload this script and to refresh the necessary bindings with it ScriptControler.<BR>
     * Subclasses of this class should override this method to properly refresh their bindings when necessary.
     *
     * @return true if and only if the script was reloaded, false otherwise.
     */
    public boolean reload() {
        try {
            ScriptEngineManager.getInstance().executeScript(getScriptFile());
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to reload script!", e);
            return false;
        }
    }

    public abstract boolean unload();

    public boolean isActive() {
        return _isActive;
    }

    public void setActive(boolean status) {
        _isActive = status;
    }

    /**
     * @return Returns the scriptFile.
     */
    public Path getScriptFile() {
        return _scriptFile;
    }

    /**
     * @return Returns the lastLoadTime.
     */
    protected long getLastLoadTime() {
        return _lastLoadTime;
    }

    /**
     * @param lastLoadTime The lastLoadTime to set.
     */
    protected void setLastLoadTime(long lastLoadTime) {
        _lastLoadTime = lastLoadTime;
    }

    public abstract String getScriptName();
}
