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

import org.l2j.gameserver.settings.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.Map.Entry;

import static java.util.Objects.requireNonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * Caches script engines and provides functionality for executing and managing scripts.
 *
 * @author KenM, HorridoJoho
 * @author JoeAlisson
 */
public final class ScriptEngineManager  {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptEngineManager.class);

    public static final Path SCRIPT_FOLDER = getSettings(ServerSettings.class).dataPackDirectory().resolve("data/scripts");

    private final Map<String, IExecutionContext> extEngines = new HashMap<>();
    private IExecutionContext currentExecutionContext = null;

    private ScriptEngineManager() {
        // no public instances
    }

    public static void init() {
        var instance = getInstance();
        instance.loadEngines();
        try {
            instance.executeScriptInit();
        } catch (Exception e) {
            LOGGER.error("Could not execute Scripts Init", e);
        }

    }

    private void loadEngines() {
        final var props = loadProperties();
        ServiceLoader.load(IScriptingEngine.class).forEach(engine -> registerEngine(engine, props));
    }

    private Properties loadProperties() {
        var props = new Properties();
        try (FileInputStream fis = new FileInputStream("config/ScriptEngine.properties")) {
            props.load(fis);
        } catch (Exception e) {
            LOGGER.warn("Couldn't load ScriptEngine.properties", e);
        }
        return props;
    }

    private void registerEngine(IScriptingEngine engine, Properties props) {
        maybeSetProperties(engine.getLanguageName().toLowerCase(), props, engine);
        LOGGER.info("{} {} ({} {})", engine.getEngineName(), engine.getEngineVersion(), engine.getLanguageName(), engine.getLanguageVersion());
        final IExecutionContext context = engine.createExecutionContext();
        for (String commonExtension : engine.getCommonFileExtensions()) {
            extEngines.put(commonExtension, context);
        }
    }

    private void maybeSetProperties(String language, Properties props, IScriptingEngine engine) {
        for (var prop : props.entrySet()) {
            String key = (String) prop.getKey();
            String value = (String) prop.getValue();

            if (key.startsWith(language)) {
                key = key.substring(language.length() + 1);
                if (value.startsWith("%") && value.endsWith("%")) {
                    value = System.getProperty(value.substring(1, value.length() - 1));
                }
                engine.setProperty(key, value);
            }
        }
    }

    private IExecutionContext getEngineByExtension(String ext) {
        return extEngines.get(ext);
    }

    private String getFileExtension(Path p) {
        final String name = p.getFileName().toString();
        final int lastDotIdx = name.lastIndexOf('.');
        if (lastDotIdx == -1) {
            return null;
        }

        final String extension = name.substring(lastDotIdx + 1);
        if (extension.isEmpty()) {
            return null;
        }

        return extension;
    }

    private void checkExistingFile(Path filePath) throws Exception {
        if (!Files.exists(filePath)) {
            throw new Exception("ScriptFile: " + filePath + " does not exists!");
        } else if (!Files.isRegularFile(filePath)) {
            throw new Exception("ScriptFile: " + filePath + " is not a file!");
        }
    }

    public void executeScriptInit() throws Exception {
        executeScripts("Init");
    }

    private void executeScripts(String scriptsName) throws Exception {
        for (Entry<IExecutionContext, List<Path>> entry : parseScriptDirectory(scriptsName).entrySet()) {
            currentExecutionContext = entry.getKey();
            try {
                final Map<Path, Throwable> invokationErrors = currentExecutionContext.executeScripts(entry.getValue());
                for (Entry<Path, Throwable> entry2 : invokationErrors.entrySet()) {
                    LOGGER.warn("{} failed execution!", entry2.getKey(), entry2.getValue());
                }
            } finally {
                currentExecutionContext = null;
            }
        }
    }

    public void executeScriptLoader() throws Exception {
        executeScripts("Loader");
    }

    private Map<IExecutionContext, List<Path>> parseScriptDirectory(String names) throws IOException {
        Map<IExecutionContext, List<Path>> files = new HashMap<>();

        Files.walkFileTree(SCRIPT_FOLDER, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                var fileName = file.getFileName().toString();

                if(attrs.isRegularFile() && fileName.startsWith(names)) {
                    var ext = fileName.substring(fileName.lastIndexOf(".") +1);

                    if(ext.equals(fileName)) {
                        LOGGER.warn("ScriptFile: {} does not have an extension to determine the script engine!", file);
                        return FileVisitResult.CONTINUE;
                    }

                    var engine = getEngineByExtension(ext);
                    if(engine == null) {
                        return FileVisitResult.CONTINUE;
                    }

                    files.computeIfAbsent(engine, k -> new LinkedList<>()).add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return files;
    }

    public void executeScript(Path sourceFile) throws Exception {
        requireNonNull(sourceFile);

        if (sourceFile.isAbsolute()) {
            sourceFile = SCRIPT_FOLDER.toAbsolutePath().relativize(sourceFile);
        }

        // throws exception if not exists or not file
        checkExistingFile(sourceFile);

        final String ext = getFileExtension(sourceFile);
        requireNonNull(sourceFile, "ScriptFile: " + sourceFile + " does not have an extension to determine the script engine!");

        final IExecutionContext engine = getEngineByExtension(ext);
        requireNonNull(engine, "ScriptEngine: No engine registered for extension " + ext + "!");

        currentExecutionContext = engine;
        try {
            final Entry<Path, Throwable> error = engine.executeScript(sourceFile);
            if (error != null) {
                throw new Exception("ScriptEngine: " + error.getKey() + " failed execution!", error.getValue());
            }
        } finally {
            currentExecutionContext = null;
        }
    }

    public Path getCurrentLoadingScript() {
        return currentExecutionContext != null ? currentExecutionContext.getCurrentExecutingScript() : null;
    }


    public static ScriptEngineManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final ScriptEngineManager INSTANCE = new ScriptEngineManager();
    }
}