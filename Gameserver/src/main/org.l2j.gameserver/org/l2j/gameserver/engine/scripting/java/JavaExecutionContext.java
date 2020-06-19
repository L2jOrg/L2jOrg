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
package org.l2j.gameserver.engine.scripting.java;

import org.l2j.commons.util.FilterUtil;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.scripting.AbstractExecutionContext;
import org.l2j.gameserver.engine.scripting.annotations.Disabled;
import org.l2j.gameserver.settings.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.Objects.*;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author JoeAlisson
 */
public final class JavaExecutionContext extends AbstractExecutionContext<JavaScriptingEngine> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaExecutionContext.class);

    private final Path destination;
    private final DiagnosticListener<JavaFileObject> listener = new DefaultDiagnosticListener();
    private final Path sourcePath;
    private final boolean forceCompile;

    private ScriptingFileManager scriptingFileManager;
    private ModuleLayer layer;

    JavaExecutionContext(JavaScriptingEngine engine) {
        super(engine);

        sourcePath = getSettings(ServerSettings.class).dataPackDirectory().resolve(requireNonNullElse(getProperty("source.path"), "data/scripts"));
        destination = Path.of(requireNonNullElse(getProperty("compiled.path"), "compiledScripts"));
        forceCompile =  Boolean.parseBoolean(requireNonNullElse(getProperty("force.compile"), "true"));

        try {
            compileModuleInfo();
            compile(sourcePath);
        } catch (Exception e) {
            LOGGER.error("Could not compile Java Scripts", e);
        }
    }

    private void compileModuleInfo() throws Exception {
        Files.createDirectories(destination);
        initializeScriptingFileManager();

        var options = new ArrayList<>(compileOptions());
        options.add("--module-source-path");
        options.add(sourcePath.toString());

        compile(findModuleInfo(), options);
    }

    private void initializeScriptingFileManager() throws IOException {
        var fileManager = getScriptingEngine().getCompiler().getStandardFileManager(listener, null, StandardCharsets.UTF_8);
        fileManager.setLocation(StandardLocation.CLASS_PATH, Collections.emptyList());
        fileManager.setLocationFromPaths(StandardLocation.CLASS_OUTPUT, Collections.singletonList(destination));
        scriptingFileManager = new ScriptingFileManager(fileManager);
    }

    private List<Path> findModuleInfo() throws IOException {
        return Files.find(sourcePath, Integer.MAX_VALUE, (path, attributes) -> "module-info.java".equals(path.getFileName().toString())).collect(Collectors.toList());
    }

    private void tryConfigureModuleLayer() {
        var moduleNames = scriptingFileManager.getModuleNames();
        if(moduleNames.isEmpty()) {
            return;
        }
        try {
            Configuration configuration = ModuleLayer.boot().configuration().resolve(ModuleFinder.of(destination), ModuleFinder.of(), moduleNames);
            layer = ModuleLayer.boot().defineModulesWithOneLoader(configuration, ClassLoader.getSystemClassLoader());
        } catch (Exception e) {
            LOGGER.warn("Couldn't configure module layer of modules {} : {}", moduleNames, e.getMessage());
        }
    }

    private void compile(List<Path> sources, List<String> options) throws JavaCompilerException {
        if(!Util.isNullOrEmpty(sources)) {
            var writer = new StringWriter();
            final boolean compilationSuccess = getScriptingEngine().getCompiler().getTask(writer, scriptingFileManager, listener, options, null, scriptingFileManager.getJavaFileObjectsFromPaths(sources)).call();

            if (!compilationSuccess) {
                throw new JavaCompilerException(writer.toString());
            }
            tryConfigureModuleLayer();
        }
    }

    private void compile(Path sourcePath) throws JavaCompilerException, IOException {
        var paths = Files.walk(sourcePath).filter(this::needCompile).collect(Collectors.toList());
        if(!Util.isNullOrEmpty(paths)) {
            compile(paths, compileOptions());
        }
    }

    private boolean needCompile(Path path) {
        if(!FilterUtil.javaFile(path)) {
            return false;
        }

        if(forceCompile) {
            return true;
        }

        try {
            var compiled = destination.resolve(Path.of(sourcePath.relativize(path).toString().replace(".java", ".class")));

            if(Files.notExists(compiled) || Files.getLastModifiedTime(compiled).compareTo(Files.getLastModifiedTime(path)) < 0) {
                return true;
            }
            return !scriptingFileManager.beAwareOfObjectFile(path, compiled);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
            return true;
        }
    }

    private List<String> compileOptions() {
        var javaVersion = System.getProperty("java.specification.version");
        return List.of("--enable-preview", "--module-path", System.getProperty("jdk.module.path"),
                    "-target", javaVersion, "--source", javaVersion, "-implicit:class");
    }

    @Override
    public Map<Path, Throwable> executeScripts(Iterable<Path> sourcePaths)  {
        final Map<Path, Throwable> executionFailures = new LinkedHashMap<>();

        for (Path sourcePath : sourcePaths) {
            var scriptFileInfo = scriptingFileManager.getScriptInfo(sourcePath);

            if(isNull(scriptFileInfo)) {
                try {
                    compile(sourcePath);
                    scriptFileInfo = scriptingFileManager.getScriptInfo(sourcePath);
                } catch (JavaCompilerException | IOException e) {
                    LOGGER.error(e.getMessage(), e);
                    continue;
                }
            }

            if(isNull(scriptFileInfo)) {
                LOGGER.error("Compilation successful, but class corresponding to {} not found!", sourcePath.toString());
                continue;
            }

            var javaName = scriptFileInfo.getJavaName();

            if(javaName.contains("$") || javaName.equals("module-info") || javaName.equals("package-info")) {
                continue;
            }

            setCurrentExecutingScript(sourcePath);
            try {
                executeMain(scriptFileInfo);

            } catch (NoSuchMethodException e) {
                LOGGER.warn("There is no main method on script {}", sourcePath);
                // the script doesn't have a main method. just ignore it.
            } catch (Exception e) {
                executionFailures.put(sourcePath, e);
            } finally {
                setCurrentExecutingScript(null);
            }
        }
        return executionFailures;
    }

    private void executeMain(ScriptingFileInfo scriptFileInfo) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        var classLoader = getClassLoaderOfScript(scriptFileInfo);
        final Class<?> javaClass = classLoader.loadClass(scriptFileInfo.getJavaName());
        Method mainMethod = javaClass.getMethod("main", String[].class);

        if (Modifier.isStatic(mainMethod.getModifiers()) && !javaClass.isAnnotationPresent(Disabled.class)) {
            mainMethod.invoke(null, (Object) new String[] { scriptFileInfo.getSourcePath().toString() });
        }
    }

    private ClassLoader getClassLoaderOfScript(ScriptingFileInfo scriptFileInfo) {
        if(nonNull(layer)) {
            try{
                return layer.findLoader(scriptFileInfo.getModuleName());
            } catch (Exception e) {
                LOGGER.warn("Could not find class loader of module", e);
            }
        }
        return scriptingFileManager.getClassLoader(scriptFileInfo.getLocation());
    }

    @Override
    public Entry<Path, Throwable> executeScript(Path sourcePath)  {
        final Map<Path, Throwable> executionFailures = executeScripts(Collections.singletonList(sourcePath));
        if (!executionFailures.isEmpty()) {
            return executionFailures.entrySet().iterator().next();
        }
        return null;
    }

    private static class DefaultDiagnosticListener implements DiagnosticListener<JavaFileObject> {
        @Override
        public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
            if (nonNull(diagnostic.getSource())) {
                LOGGER.warn("{} {}:{} - {}", diagnostic.getSource().getName(), diagnostic.getLineNumber(), diagnostic.getColumnNumber(), diagnostic.getMessage(Locale.getDefault()) );
            } else {
                LOGGER.warn(diagnostic.getMessage(Locale.getDefault()), diagnostic.getSource());
            }
        }
    }
}
