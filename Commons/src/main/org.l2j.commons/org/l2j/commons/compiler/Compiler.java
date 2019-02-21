package org.l2j.commons.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Класс компиляции внешних Java файлов<br>
 * В качестве компилятора используется Eclipse Java Compiler
 * 
 * @author G1ta0
 */
public class Compiler
{
	private static final Logger _log = LoggerFactory.getLogger(Compiler.class);

	private static final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
	private static final DiagnosticListener<JavaFileObject> listener = new DefaultDiagnosticListener();
	private final StandardJavaFileManager fileManager =  javac.getStandardFileManager(listener, Locale.getDefault(), Charset.defaultCharset());
	private final MemoryClassLoader memClassLoader = new MemoryClassLoader();
	private final MemoryJavaFileManager memFileManager = new MemoryJavaFileManager(fileManager, memClassLoader);

	public boolean compile(File... files) {

	    List<String> options =  List.of("--module-path", System.getProperty("jdk.module.path"));

		Writer writer = new StringWriter();
		JavaCompiler.CompilationTask compile = javac.getTask(writer, memFileManager, listener, options, null, fileManager.getJavaFileObjects(files));

		if(compile.call())
			return true;

		_log.warn(writer.toString());

		return false;
	}

	public boolean compile(Collection<File> files)
	{
		return compile(files.toArray(new File[files.size()]));
	}

	public MemoryClassLoader getClassLoader()
	{
		return memClassLoader;
	}

	private static class DefaultDiagnosticListener implements DiagnosticListener<JavaFileObject>
	{
		@Override
		public void report(Diagnostic<? extends JavaFileObject> diagnostic)
		{
			_log.error(diagnostic.getSource().getName() + (diagnostic.getPosition() == Diagnostic.NOPOS ? "" : ":" + diagnostic.getLineNumber() + "," + diagnostic.getColumnNumber()) + ": " + diagnostic.getMessage(Locale.getDefault()));
		}
	}
}