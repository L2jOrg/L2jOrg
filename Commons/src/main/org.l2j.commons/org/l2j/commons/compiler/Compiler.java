package org.l2j.commons.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class Compiler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Compiler.class);
    private static final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
    private static final DiagnosticListener<JavaFileObject> listener = new DefaultDiagnosticListener();

    private final StandardJavaFileManager fileManager =  javac.getStandardFileManager(listener, Locale.getDefault(), Charset.defaultCharset());
    private final MemoryJavaFileManager memFileManager = new MemoryJavaFileManager(fileManager);

    public boolean compile(Path source, Path destination, String... options) throws IOException {
        Files.createDirectories(destination);
        var sources = Files.walk(source).filter(file -> file.toString().endsWith(".java")).collect(Collectors.toList());
        fileManager.setLocation(StandardLocation.CLASS_PATH, Collections.emptyList());
        fileManager.setLocationFromPaths(StandardLocation.CLASS_OUTPUT,  Collections.singletonList(destination));

        Writer writer = new StringWriter();
        JavaCompiler.CompilationTask compile = javac.getTask(writer, memFileManager, listener, Arrays.asList(options), null, fileManager.getJavaFileObjectsFromPaths(sources));

        if(compile.call())
            return true;

        LOGGER.warn(writer.toString());
        return false;
    }

    public Set<String> getLoadedClasses() {
        return memFileManager.getLoadedClasses();
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