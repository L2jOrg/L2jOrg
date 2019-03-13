package org.l2j.gameserver.scripting.java;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.scripting.AbstractExecutionContext;
import org.l2j.gameserver.scripting.annotations.Disabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

import static java.util.Objects.nonNull;

/**
 * @author HorridoJoho
 */
public final class JavaExecutionContext extends AbstractExecutionContext<JavaScriptingEngine> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaExecutionContext.class.getName());

    private final List<String> options = new LinkedList<>();
    private final DiagnosticListener<JavaFileObject> listener = new DefaultDiagnosticListener();
    private StandardJavaFileManager fileManager;
    private ScriptingFileManager scriptingFileManager;

    JavaExecutionContext(JavaScriptingEngine engine) {
        super(engine);

        addOptionIfNotNull(options, System.getProperty("jdk.module.path"), "--module-path");
        addOptionIfNotNull(options, new File(Config.DATAPACK_ROOT, getProperty("sourcepath")).getAbsolutePath(), "--module-source-path");
        // Set options.
        addOptionIfNotNull(options, getProperty("g"), "-g:");

        // We always set the target JVM to the current running version.
        final String targetVersion = System.getProperty("java.specification.version");
        if (!targetVersion.contains(".")) {
            options.add("-target");
            options.add(targetVersion);
        } else {
            final String[] versionSplit = targetVersion.split("\\.");
            if (versionSplit.length > 1) {
                options.add("-target");
                options.add(versionSplit[0] + '.' + versionSplit[1]);
            } else {
                throw new JavaCompilerException("Could not determine target version!");
            }
        }

        fileManager = getScriptingEngine().getCompiler().getStandardFileManager(listener, null, StandardCharsets.UTF_8);
        scriptingFileManager = new ScriptingFileManager(fileManager);
    }

    private boolean addOptionIfNotNull(List<String> list, String nullChecked, String before) {
        if (nullChecked == null) {
            return false;
        }

        if (before.endsWith(":")) {
            list.add(before + nullChecked);
        } else {
            list.add(before);
            list.add(nullChecked);
        }

        return true;
    }

    private ClassLoader determineScriptParentClassloader() {
        final String classloader = getProperty("classloader");
        if (classloader == null) {
            return ClassLoader.getSystemClassLoader();
        }

        switch (classloader) {
            case "ThreadContext": {
                return Thread.currentThread().getContextClassLoader();
            }
            case "System": {
                return ClassLoader.getSystemClassLoader();
            }
            default: {
                try {
                    return Class.forName(classloader).getClassLoader();
                } catch (ClassNotFoundException e) {
                    return ClassLoader.getSystemClassLoader();
                }
            }
        }
    }

    @Override
    public Map<Path, Throwable> executeScripts(Iterable<Path> sourcePaths) throws Exception {
        try (var writer = new StringWriter()) {

            var destination = Path.of("compiledScripts");
            Files.createDirectories(destination);
            fileManager.setLocation(StandardLocation.CLASS_PATH, Collections.emptyList());
            fileManager.setLocationFromPaths(StandardLocation.CLASS_OUTPUT,  Collections.singletonList(destination));

            final boolean compilationSuccess = getScriptingEngine().getCompiler().getTask(writer, scriptingFileManager, listener, options, null, fileManager.getJavaFileObjectsFromPaths(sourcePaths)).call();
            if (!compilationSuccess) {
                throw new JavaCompilerException(writer.toString());
            }

            final ClassLoader parentClassLoader = determineScriptParentClassloader();

            final Map<Path, Throwable> executionFailures = new LinkedHashMap<>();
            final Iterable<ScriptingOutputFileObject> compiledClasses = scriptingFileManager.getCompiledClasses();


            for (Path sourcePath : sourcePaths) {
                boolean found = false;
                String lastModuleName = "";
                for (ScriptingOutputFileObject compiledClass : compiledClasses) {
                    final Path compiledSourcePath = compiledClass.getSourcePath();
                    // sourePath can be relative, so we have to use endsWith
                    if ((compiledSourcePath != null) && (compiledSourcePath.equals(sourcePath) || compiledSourcePath.endsWith(sourcePath))) {
                        final String javaName = compiledClass.getJavaName();
                        if (javaName.indexOf('$') != -1) {
                            continue;
                        }

                        found = true;
                        setCurrentExecutingScript(compiledSourcePath);
                        try {

                            final ScriptingClassLoader loader = new ScriptingClassLoader(parentClassLoader, compiledClasses);
                            final Class<?> javaClass = loader.loadClass(javaName);
                            Method mainMethod = null;
                            for (Method m : javaClass.getMethods()) {
                                if (m.getName().equals("main") && Modifier.isStatic(m.getModifiers()) && (m.getParameterCount() == 1) && (m.getParameterTypes()[0] == String[].class)) {
                                    mainMethod = m;
                                    break;
                                }
                            }
                            if ((mainMethod != null) && !javaClass.isAnnotationPresent(Disabled.class)) {
                                mainMethod.invoke(null, (Object) new String[]
                                        {
                                                compiledSourcePath.toString()
                                        });
                            }
                        } catch (Exception e) {
                            executionFailures.put(compiledSourcePath, e);
                        } finally {
                            setCurrentExecutingScript(null);
                        }

                        break;
                    }
                }

                if (!found) {
                    LOGGER.error("Compilation successfull, but class coresponding to " + sourcePath.toString() + " not found!");
                }
            }

            return executionFailures;
        }
    }

    @Override
    public Entry<Path, Throwable> executeScript(Path sourcePath) throws Exception {
        final Map<Path, Throwable> executionFailures = executeScripts(Arrays.asList(sourcePath));
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
