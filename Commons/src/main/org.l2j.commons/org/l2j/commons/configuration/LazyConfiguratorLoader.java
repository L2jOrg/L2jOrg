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
package org.l2j.commons.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.isNullOrEmpty;

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

