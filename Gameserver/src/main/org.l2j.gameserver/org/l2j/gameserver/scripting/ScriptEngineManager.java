package org.l2j.gameserver.scripting;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.settings.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.Map.Entry;

import static java.util.Objects.isNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * Caches script engines and provides functionality for executing and managing scripts.
 *
 * @author KenM, HorridoJoho
 */
public final class ScriptEngineManager  {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptEngineManager.class);

    public static final Path SCRIPT_FOLDER = getSettings(ServerSettings.class).dataPackDirectory().resolve("data/scripts");
    private static final Path MASTER_HANDLER_FILE = Paths.get(SCRIPT_FOLDER.toString(), "org.l2j.scripts", "handlers", "MasterHandler.java");
    private static final Path EFFECT_MASTER_HANDLER_FILE = Paths.get(SCRIPT_FOLDER.toString(), "org.l2j.scripts", "handlers", "EffectMasterHandler.java");
    private static final Path SKILL_CONDITION_HANDLER_FILE = Paths.get(SCRIPT_FOLDER.toString(), "org.l2j.scripts", "handlers", "SkillConditionMasterHandler.java");
    private static final Path CONDITION_HANDLER_FILE = Paths.get(SCRIPT_FOLDER.toString(), "org.l2j.scripts", "handlers", "ConditionMasterHandler.java");
    private static final Path ONE_DAY_REWARD_MASTER_HANDLER = Paths.get(SCRIPT_FOLDER.toString(), "org.l2j.scripts", "handlers", "DailyMissionMasterHandler.java");

    private final Map<String, IExecutionContext> _extEngines = new HashMap<>();
    private IExecutionContext _currentExecutionContext = null;

    private ScriptEngineManager() {
        final var props = loadProperties();
        ServiceLoader.load(IScriptingEngine.class).forEach(engine -> registerEngine(engine, props));
    }

    private Properties loadProperties() {
        var props = new Properties();
        try (FileInputStream fis = new FileInputStream("config/ScriptEngine.ini")) {
            props.load(fis);
        } catch (Exception e) {
            LOGGER.warn("Couldn't load ScriptEngine.ini", e);
        }
        return props;
    }

    private void registerEngine(IScriptingEngine engine, Properties props) {
        maybeSetProperties("language." + engine.getLanguageName() + ".", props, engine);
        final IExecutionContext context = engine.createExecutionContext();
        for (String commonExtension : engine.getCommonFileExtensions()) {
            _extEngines.put(commonExtension, context);
        }

        LOGGER.info("{} {} ({} {})", engine.getEngineName(), engine.getEngineVersion(), engine.getLanguageName(), engine.getLanguageVersion());
    }

    private void maybeSetProperties(String propPrefix, Properties props, IScriptingEngine engine) {
        if (isNull(props)) {
            return;
        }

        for (Entry<Object, Object> prop : props.entrySet()) {
            String key = (String) prop.getKey();
            String value = (String) prop.getValue();

            if (key.startsWith(propPrefix)) {
                key = key.substring(propPrefix.length());
                if (value.startsWith("%") && value.endsWith("%")) {
                    value = System.getProperty(value.substring(1, value.length() - 1));
                }
                engine.setProperty(key, value);
            }
        }
    }

    private IExecutionContext getEngineByExtension(String ext) {
        return _extEngines.get(ext);
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

    public void executeMasterHandler() throws Exception {
        executeScript(MASTER_HANDLER_FILE);
    }

    public void executeEffectMasterHandler() throws Exception {
        executeScript(EFFECT_MASTER_HANDLER_FILE);
    }

    public void executeSkillConditionMasterHandler() throws Exception {
        executeScript(SKILL_CONDITION_HANDLER_FILE);
    }

    public void executeConditionMasterHandler() throws Exception {
        executeScript(CONDITION_HANDLER_FILE);
    }

    public void executeDailyMissionMasterHandler() throws Exception {
        executeScript(ONE_DAY_REWARD_MASTER_HANDLER);
    }

    public void executeScriptInitList() throws Exception {
        if (Config.ALT_DEV_NO_QUESTS) {
            return;
        }

        for (Entry<IExecutionContext, List<Path>> entry : parseScriptDirectory().entrySet()) {
            _currentExecutionContext = entry.getKey();
            try {
                final Map<Path, Throwable> invokationErrors = _currentExecutionContext.executeScripts(entry.getValue());
                for (Entry<Path, Throwable> entry2 : invokationErrors.entrySet()) {
                    LOGGER.warn("{} failed execution! {}", entry2.getKey(), entry2.getValue());
                }
            } finally {
                _currentExecutionContext = null;
            }
        }
    }

    private Map<IExecutionContext, List<Path>> parseScriptDirectory() throws IOException {
        Map<IExecutionContext, List<Path>> files = new HashMap<>();

        Files.walkFileTree(SCRIPT_FOLDER, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                var fileName = file.getFileName().toString();

                if(attrs.isRegularFile() && fileName.startsWith("Init")) {
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
        Objects.requireNonNull(sourceFile);

        if (sourceFile.isAbsolute()) {
            sourceFile = SCRIPT_FOLDER.toAbsolutePath().relativize(sourceFile);
        }

        // throws exception if not exists or not file
        checkExistingFile(sourceFile);

        final String ext = getFileExtension(sourceFile);
        Objects.requireNonNull(sourceFile, "ScriptFile: " + sourceFile + " does not have an extension to determine the script engine!");

        final IExecutionContext engine = getEngineByExtension(ext);
        Objects.requireNonNull(engine, "ScriptEngine: No engine registered for extension " + ext + "!");

        _currentExecutionContext = engine;
        try {
            final Entry<Path, Throwable> error = engine.executeScript(sourceFile);
            if (error != null) {
                throw new Exception("ScriptEngine: " + error.getKey() + " failed execution!", error.getValue());
            }
        } finally {
            _currentExecutionContext = null;
        }
    }

    public Path getCurrentLoadingScript() {
        return _currentExecutionContext != null ? _currentExecutionContext.getCurrentExecutingScript() : null;
    }

    public static ScriptEngineManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final ScriptEngineManager INSTANCE = new ScriptEngineManager();
    }
}