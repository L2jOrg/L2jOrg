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

import org.l2j.commons.cache.CacheFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class Configurator {

    private static final Logger logger = LoggerFactory.getLogger(Configurator.class);
    private static final String CONFIGURATOR_PROPERTIES = "./config/configurator.properties";
    private static Configurator configurator;
    private LazyConfiguratorLoader loader;

    private Cache<Class<? extends Settings>, Settings> settingsMap;

    private Configurator() {
        settingsMap = CacheFactory.getInstance().getCache("settings");
        loader = new LazyConfiguratorLoader();
        load();
    }

    private void load() {
        logger.debug("Loading Configurations from {}", CONFIGURATOR_PROPERTIES);
        SettingsFile settings = new SettingsFile(CONFIGURATOR_PROPERTIES);
        if(settings.isEmpty()) {
            logger.warn("Configurations not found on file {}. No Settings has been loaded", CONFIGURATOR_PROPERTIES);
        } else {
            loader.load(settings);
        }
    }

    public void addSettingsClass(String className, String fileConfigurationPath) {
        loader.addSettingsClass(className, fileConfigurationPath);
    }

    public static <T extends Settings> T getSettings(Class<T> settingsClass) {
        return getSettings(settingsClass, false);
    }

    public static <T extends Settings> T getSettings(final Class<T> settingsClass, boolean forceReload) {
        if(isNull(settingsClass)) {
            throw new IllegalArgumentException("Can't load settings from Null class");
        }

        var instance = getInstance();

        synchronized (settingsClass) {
            if (!forceReload && instance.hasSettings(settingsClass)) {
                return instance.get(settingsClass);
            }
            return instance.getFromLoader(settingsClass);
        }
    }

    private <T extends Settings> T getFromLoader(Class<T> settingsClass) {
        T settings = loader.getSettings(settingsClass);
        if(nonNull(settings)) {
            settingsMap.put(settingsClass, settings);
        }
        return settings;
    }

    @SuppressWarnings("unchecked")
    private <T extends Settings> T get(Class<T> settingsClass){
        return (T) settingsMap.get(settingsClass);
    }

    private  boolean hasSettings(Class<? extends Settings> settingsClass) {
        return settingsMap.containsKey(settingsClass);
    }

    public static void reloadAll() {
        logger.debug("Reloading all settings");
        getInstance().reload();
    }

    private void reload() {
        settingsMap.clear();
        load();
    }

    public static void reloadSettings(Class<? extends Settings>  settingsClass) {
        logger.debug("Reloading settings " + settingsClass.getName());
        getInstance().removeSettings(settingsClass);
    }


    private void removeSettings(Class<? extends Settings> settingsClass) {
        settingsMap.remove(settingsClass);
    }

    private static Configurator getInstance() {
        if(isNull(configurator)) {
            configurator = new Configurator();
        }
        return configurator;
    }

}