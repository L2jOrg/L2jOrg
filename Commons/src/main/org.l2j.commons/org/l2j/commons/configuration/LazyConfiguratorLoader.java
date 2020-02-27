package org.l2j.commons.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static org.l2j.commons.util.Util.isNullOrEmpty;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
class LazyConfiguratorLoader {

    protected static final Logger logger = LoggerFactory.getLogger(LazyConfiguratorLoader.class);

    private final Map<Class<? extends Settings>, String> settingsClasses = new HashMap<>();

    LazyConfiguratorLoader() { }

    void load(SettingsFile settings)	{
        settingsClasses.clear();
        for(Entry<Object, Object> entry : settings.entrySet()) {
            String className = (String) entry.getKey();
            String fileConfigurationPath = (String) entry.getValue();

            addSettingsClass(className, fileConfigurationPath);
        }
        logger.debug("Settings classes loaded: {}", settingsClasses.size());
    }

    void addSettingsClass(String className, String fileConfigurationPath) {
        if(isNullOrEmpty(className)) {
            return;
        }

        Class<? extends Settings> settings = createSettings(className);
        if(nonNull(settings)) {
            settingsClasses.put(settings, fileConfigurationPath);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Settings> createSettings(String className)	{
        try {
            Class<?> clazz = Class.forName(className);
            if(Settings.class.isAssignableFrom(clazz)) {
                return (Class<? extends Settings>) clazz;
            }
        } catch (ClassNotFoundException e)  {
            logger.error("The class {} was not found!", className);
        }
        return null;
    }

    <T extends Settings> T getSettings(Class<T> settingsClass) {
        String configurationFile = null;
        if(settingsClasses.containsKey(settingsClass)) {
            configurationFile = settingsClasses.get(settingsClass);
        }
        return loadSettings(settingsClass, configurationFile);
    }

    private <T extends Settings> T loadSettings(Class<T> settingsClass, String configurationFile) {
        try {
            return newSettingsInstance(settingsClass, configurationFile);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            logger.error("Error loading Settings {}: {}", settingsClass, e.getLocalizedMessage());
        }
        return null;
    }

    private <T extends Settings> T newSettingsInstance(Class<T> settingsClass, String configurationFile)
            throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        T settings = settingsClass.getDeclaredConstructor().newInstance();
        SettingsFile settingsFile = isNullOrEmpty(configurationFile) ?  new SettingsFile() : new SettingsFile(configurationFile);

        logger.debug("Lazy Initialization : {}", settingsClass.getName());
        settings.load(settingsFile);
        return settings;
    }
}

