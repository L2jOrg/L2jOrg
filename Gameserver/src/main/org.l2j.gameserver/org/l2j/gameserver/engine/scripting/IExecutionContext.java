/*
 * Copyright © 2019 L2J Mobius
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

import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author HorridoJoho
 */
public interface IExecutionContext {
    /**
     * Properties set here override the settings from the IScriptEngine<br>
     * this class was created from.
     *
     * @param key   the key
     * @param value the value
     * @return the previous value, or null when this key was not present before
     */
    String setProperty(String key, String value);

    /**
     * Executes all script in the iterable.
     *
     * @param sourcePaths the scripts to execute
     * @return map of failed executions, Path=source file Throwable=thrown exception
     * @throws Exception preparation for script execution failed
     */
    Map<Path, Throwable> executeScripts(Iterable<Path> sourcePaths) throws Exception;

    /**
     * Executes a single file.
     *
     * @param sourcePath the script to execute
     * @return entry of failed execution, Path=source file Throwable=thrown exception
     * @throws Exception preparation for script execution failed
     */
    Entry<Path, Throwable> executeScript(Path sourcePath) throws Exception;

    /**
     * Method to get the specified property value.
     *
     * @param key the key
     * @return the value, or null if the key is not present
     */
    String getProperty(String key);

    /**
     * Method to get the current executing script file.
     *
     * @return the currently executing script file, null if non
     */
    Path getCurrentExecutingScript();

    /**
     * Method to get the script engine this execution context belongs to.
     *
     * @return the script engine this execution context belongs to
     */
    IScriptingEngine getScriptingEngine();
}
