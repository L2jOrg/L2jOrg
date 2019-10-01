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
