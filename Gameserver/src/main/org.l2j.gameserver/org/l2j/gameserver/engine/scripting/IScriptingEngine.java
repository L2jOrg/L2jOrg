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

/**
 * @author HorridoJoho
 */
public interface IScriptingEngine {

    /**
     * Sets script engine properties. The script values will be available<br>
     * to the the insatnces created {@link IExecutionContext} implementation.
     *
     * @param key   the key
     * @param value the value
     * @return the previous value, or null when this key was not present before
     */
    String setProperty(String key, String value);

    /**
     * Creates an execution context.
     *
     * @return the created execution context.
     */
    IExecutionContext createExecutionContext();

    /**
     * Method to get the specified property value.
     *
     * @param key the key
     * @return the value,or null if the key is not present
     */
    String getProperty(String key);

    /**
     * Method to get the engine name.
     *
     * @return the engine name
     */
    String getEngineName();

    /**
     * Method to get the engine version.
     *
     * @return the engine version
     */
    String getEngineVersion();

    /**
     * Method to get the scripting language name.
     *
     * @return the scripting engine name
     */
    String getLanguageName();

    /**
     * Method to get the the language version.
     *
     * @return the language version
     */
    String getLanguageVersion();

    /**
     * Method to retrive the commonly used file extensions for the language.
     *
     * @return the commonly used file extensions for the language
     */
    String[] getCommonFileExtensions();
}
