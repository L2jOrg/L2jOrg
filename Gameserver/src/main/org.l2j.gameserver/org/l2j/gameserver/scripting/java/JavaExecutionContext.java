package org.l2j.gameserver.scripting.java;

import org.l2j.commons.util.filter.JavaFilter;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.scripting.AbstractExecutionContext;
import org.l2j.gameserver.scripting.annotations.Disabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public final class JavaExecutionContext extends AbstractExecutionContext<JavaScriptingEngine> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaExecutionContext.class.getName());
    private final Path destination = Path.of("compiledScripts");
    private final DiagnosticListener<JavaFileObject> listener = new DefaultDiagnosticListener();
    private final Path sourcePath;

    private ScriptingFileManager scriptingFileManager;
    private ModuleLayer layer;

    JavaExecutionContext(JavaScriptingEngine engine) {
        super(engine);

        sourcePath = Path.of(Config.DATAPACK_ROOT.getAbsolutePath(), getProperty("source.path"));
        try {
            compileScripts();
        } catch (Exception e) {
            LOGGER.error("Could not compile Java Scripts", e);
        }
    }

    private void compileScripts() throws Exception {
        Files.createDirectories(destination);
        initializeScriptingFileManager();
        var paths = Files.walk(sourcePath).filter(path -> path.toString().endsWith("module-info.java")).collect(Collectors.toList());
        for (Path path : paths) {
            compile(path);
        }
    }

    private void compile(Path sourcePath) throws JavaCompilerException, IOException {
        var sources = Files.walk(sourcePath).filter(JavaFilter::accept).collect(Collectors.toList());
        var options = parseOptions(sourcePath);
        var writer = new StringWriter();
        final boolean compilationSuccess = getScriptingEngine().getCompiler().getTask(writer, scriptingFileManager, listener, options, null, scriptingFileManager.getJavaFileObjectsFromPaths(sources)).call();
        if (!compilationSuccess) {
            throw new JavaCompilerException(writer.toString());
        }
        tryConfigureModuleLayer();
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


    private void initializeScriptingFileManager() throws IOException {
        var fileManager = getScriptingEngine().getCompiler().getStandardFileManager(listener, null, StandardCharsets.UTF_8);
        fileManager.setLocation(StandardLocation.CLASS_PATH, Collections.emptyList());
        fileManager.setLocationFromPaths(StandardLocation.CLASS_OUTPUT, Collections.singletonList(destination));
        scriptingFileManager = new ScriptingFileManager(fileManager);
    }

    private List<String> parseOptions(Path sourcePath) {
        String moduleSourcePath;
        if(sourcePath.equals(this.sourcePath)) {
            moduleSourcePath = sourcePath.toString();
        } else {
            moduleSourcePath =  this.sourcePath.toString() + File.pathSeparatorChar +  sourcePath.toString();
        }
        return List.of("--module-path", System.getProperty("jdk.module.path"),  "--module-source-path", moduleSourcePath,
                "-g:"  + getProperty("g"), "-target", System.getProperty("java.specification.version"), "-implicit:class"
        );
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
                LOGGER.error("Compilation successfull, but class coresponding to " + sourcePath.toString() + " not found!");
            }

            var javaName = scriptFileInfo.getJavaName();

            if(javaName.contains("$") || javaName.equals("module-info")) {
                continue;
            }

            setCurrentExecutingScript(sourcePath);
            try {
                var classLoader = getClassLoaderOfScript(scriptFileInfo);
                final Class<?> javaClass = classLoader.loadClass(scriptFileInfo.getJavaName());
                Method mainMethod = javaClass.getMethod("main", String[].class);

                if ((mainMethod != null) && Modifier.isStatic(mainMethod.getModifiers()) && !javaClass.isAnnotationPresent(Disabled.class)) {
                    mainMethod.invoke(null, (Object) new String[] { scriptFileInfo.getSourcePath().toString() });
                }

            } catch (Exception e) {
                executionFailures.put(sourcePath, e);
            } finally {
                setCurrentExecutingScript(null);
            }
        }
        return executionFailures;
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
            if(nonNull(diagnostic.getSource())) {
                LOGGER.error("Error on {} {}:{} - {}", diagnostic.getSource().getName(), diagnostic.getLineNumber(), diagnostic.getColumnNumber(), diagnostic.getMessage(Locale.getDefault()));
            } else {
                LOGGER.error("Error {}", diagnostic.getMessage(Locale.getDefault()));
            }
        }
    }
}
