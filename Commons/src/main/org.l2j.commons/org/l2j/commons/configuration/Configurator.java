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
package org.l2j.commons.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * @author JoeAlisson
 */
public class Configurator {

    private static final Logger logger = LoggerFactory.getLogger(Configurator.class);
    private static final String DEFAULT_CONFIGURATOR_FILE = "config/configurator.properties";
    private static final String CONFIGURATOR_FILE = "configurator.file";

    private final Map<Class<? extends Settings>, String> settingsClasses = new HashMap<>();

    private Configurator() {
        load();
    }

    public void load() {
        var configuratorFile = System.getProperty(CONFIGURATOR_FILE);
        if (isNullOrEmpty(configuratorFile)) {
            configuratorFile = DEFAULT_CONFIGURATOR_FILE;
        }
        logger.debug("Loading Configurations from {}", configuratorFile);

        var settings = new SettingsFile(configuratorFile);
        load(settings);
        if (settings.isEmpty()) {
            logger.info("Configurations not found on file {}. No Settings has been loaded", configuratorFile);
        } else {
            load(settings);
        }
    }

    private void load(SettingsFile settings) {
        settingsClasses.clear();
        for(Map.Entry<Object, Object> entry : settings.entrySet()) {
            String className = (String) entry.getKey();
            String fileConfigurationPath = ((String) entry.getValue()).trim();

            addSettingsClass(className, fileConfigurationPath);
        }
        logger.debug("Settings classes loaded: {}", settingsClasses.size());
    }

    private void addSettingsClass(String className, String fileConfigurationPath) {
        if(isNullOrEmpty(className) || isNullOrEmpty(fileConfigurationPath)) {
            return;
        }

        Class<? extends Settings> settings = createSettings(className);
        if(nonNull(settings)) {
            settingsClasses.put(settings, fileConfigurationPath);
            loadSettings(settings, fileConfigurationPath);
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
            logger.warn("The class {} was not found!", className);
        }
        return null;
    }

    private <T extends Settings> void loadSettings(Class<T> settingsClass, String configurationFile) {
        try {
            T settings = settingsClass.getDeclaredConstructor().newInstance();
            SettingsFile settingsFile = new SettingsFile(configurationFile);
            settings.load(settingsFile);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            logger.error("Error loading Settings {}", settingsClass, e);
        }
    }

    public static Configurator getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final Configurator INSTANCE = new Configurator();
    }

}