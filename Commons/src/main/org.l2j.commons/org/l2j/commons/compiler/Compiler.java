package org.l2j.commons.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import static java.util.Objects.nonNull;

public class Compiler {

	private static final Logger LOGGER = LoggerFactory.getLogger(Compiler.class);
	private static final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
	private static final DiagnosticListener<JavaFileObject> listener = new DefaultDiagnosticListener();

	private final StandardJavaFileManager fileManager =  javac.getStandardFileManager(listener, Locale.getDefault(), Charset.defaultCharset());
	private final MemoryClassLoader memClassLoader = new MemoryClassLoader();
	private final MemoryJavaFileManager memFileManager = new MemoryJavaFileManager(fileManager, memClassLoader);

	public boolean compile(Path... files) {
	    List<String> options =  List.of("--module-path", System.getProperty("jdk.module.path"));
		Writer writer = new StringWriter();
		JavaCompiler.CompilationTask compile = javac.getTask(writer, memFileManager, listener, options, null, fileManager.getJavaFileObjects(files));

		if(compile.call())
			return true;

		LOGGER.warn(writer.toString());

		return false;
	}

	public MemoryClassLoader getClassLoader() {
		return memClassLoader;
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