/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.scripting.java;

import com.l2jmobius.gameserver.scripting.AbstractExecutionContext;
import com.l2jmobius.gameserver.scripting.annotations.Disabled;
import org.openjavac.tools.Diagnostic;
import org.openjavac.tools.DiagnosticCollector;
import org.openjavac.tools.JavaFileObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * @author HorridoJoho
 */
public final class JavaExecutionContext extends AbstractExecutionContext<JavaScriptingEngine>
{
	private static final Logger LOGGER = Logger.getLogger(JavaExecutionContext.class.getName());
	
	private static final List<String> _options = new LinkedList<>();
	
	JavaExecutionContext(JavaScriptingEngine engine)
	{
		super(engine);
		
		// Set options.
		addOptionIfNotNull(_options, getProperty("source"), "-source");
		addOptionIfNotNull(_options, getProperty("sourcepath"), "-sourcepath");
		if (!addOptionIfNotNull(_options, getProperty("cp"), "-cp") && !addOptionIfNotNull(_options, getProperty("classpath"), "-classpath"))
		{
			addOptionIfNotNull(_options, System.getProperty("java.class.path"), "-cp");
		}
		addOptionIfNotNull(_options, getProperty("g"), "-g:");
		
		// We always set the target JVM to the current running version.
		final String targetVersion = System.getProperty("java.specification.version");
		if (!targetVersion.contains("."))
		{
			_options.add("-target");
			_options.add(targetVersion);
		}
		else
		{
			final String[] versionSplit = targetVersion.split("\\.");
			if (versionSplit.length > 1)
			{
				_options.add("-target");
				_options.add(versionSplit[0] + '.' + versionSplit[1]);
			}
			else
			{
				throw new JavaCompilerException("Could not determine target version!");
			}
		}
	}
	
	private boolean addOptionIfNotNull(List<String> list, String nullChecked, String before)
	{
		if (nullChecked == null)
		{
			return false;
		}
		
		if (before.endsWith(":"))
		{
			list.add(before + nullChecked);
		}
		else
		{
			list.add(before);
			list.add(nullChecked);
		}
		
		return true;
	}
	
	private ClassLoader determineScriptParentClassloader()
	{
		final String classloader = getProperty("classloader");
		if (classloader == null)
		{
			return ClassLoader.getSystemClassLoader();
		}
		
		switch (classloader)
		{
			case "ThreadContext":
			{
				return Thread.currentThread().getContextClassLoader();
			}
			case "System":
			{
				return ClassLoader.getSystemClassLoader();
			}
			default:
			{
				try
				{
					return Class.forName(classloader).getClassLoader();
				}
				catch (ClassNotFoundException e)
				{
					return ClassLoader.getSystemClassLoader();
				}
			}
		}
	}
	
	@Override
	public Map<Path, Throwable> executeScripts(Iterable<Path> sourcePaths) throws Exception
	{
		final DiagnosticCollector<JavaFileObject> fileManagerDiagnostics = new DiagnosticCollector<>();
		final DiagnosticCollector<JavaFileObject> compilationDiagnostics = new DiagnosticCollector<>();
		
		try (ScriptingFileManager fileManager = new ScriptingFileManager(getScriptingEngine().getCompiler().getStandardFileManager(fileManagerDiagnostics, null, StandardCharsets.UTF_8)))
		{
			// We really need an iterable of files or strings.
			final List<String> sourcePathStrings = new LinkedList<>();
			for (Path sourcePath : sourcePaths)
			{
				sourcePathStrings.add(sourcePath.toString());
			}
			
			final StringWriter strOut = new StringWriter();
			final PrintWriter out = new PrintWriter(strOut);
			final boolean compilationSuccess = getScriptingEngine().getCompiler().getTask(out, fileManager, compilationDiagnostics, _options, null, fileManager.getJavaFileObjectsFromStrings(sourcePathStrings)).call();
			if (!compilationSuccess)
			{
				out.println();
				out.println("----------------");
				out.println("File diagnostics");
				out.println("----------------");
				for (Diagnostic<? extends JavaFileObject> diagnostic : fileManagerDiagnostics.getDiagnostics())
				{
					out.println("\t" + diagnostic.getKind() + ": " + diagnostic.getSource().getName() + ", Line " + diagnostic.getLineNumber() + ", Column " + diagnostic.getColumnNumber());
					out.println("\t\tcode: " + diagnostic.getCode());
					out.println("\t\tmessage: " + diagnostic.getMessage(null));
				}
				
				out.println();
				out.println("-----------------------");
				out.println("Compilation diagnostics");
				out.println("-----------------------");
				for (Diagnostic<? extends JavaFileObject> diagnostic : compilationDiagnostics.getDiagnostics())
				{
					out.println("\t" + diagnostic.getKind() + ": " + diagnostic.getSource().getName() + ", Line " + diagnostic.getLineNumber() + ", Column " + diagnostic.getColumnNumber());
					out.println("\t\tcode: " + diagnostic.getCode());
					out.println("\t\tmessage: " + diagnostic.getMessage(null));
				}
				
				throw new JavaCompilerException(strOut.toString());
			}
			
			final ClassLoader parentClassLoader = determineScriptParentClassloader();
			
			final Map<Path, Throwable> executionFailures = new LinkedHashMap<>();
			final Iterable<ScriptingOutputFileObject> compiledClasses = fileManager.getCompiledClasses();
			for (Path sourcePath : sourcePaths)
			{
				boolean found = false;
				
				for (ScriptingOutputFileObject compiledClass : compiledClasses)
				{
					final Path compiledSourcePath = compiledClass.getSourcePath();
					// sourePath can be relative, so we have to use endsWith
					if ((compiledSourcePath != null) && (compiledSourcePath.equals(sourcePath) || compiledSourcePath.endsWith(sourcePath)))
					{
						final String javaName = compiledClass.getJavaName();
						if (javaName.indexOf('$') != -1)
						{
							continue;
						}
						
						found = true;
						setCurrentExecutingScript(compiledSourcePath);
						try
						{
							final ScriptingClassLoader loader = new ScriptingClassLoader(parentClassLoader, compiledClasses);
							final Class<?> javaClass = loader.loadClass(javaName);
							Method mainMethod = null;
							for (Method m : javaClass.getMethods())
							{
								if (m.getName().equals("main") && Modifier.isStatic(m.getModifiers()) && (m.getParameterCount() == 1) && (m.getParameterTypes()[0] == String[].class))
								{
									mainMethod = m;
									break;
								}
							}
							if ((mainMethod != null) && !javaClass.isAnnotationPresent(Disabled.class))
							{
								mainMethod.invoke(null, (Object) new String[]
								{
									compiledSourcePath.toString()
								});
							}
						}
						catch (Exception e)
						{
							executionFailures.put(compiledSourcePath, e);
						}
						finally
						{
							setCurrentExecutingScript(null);
						}
						
						break;
					}
				}
				
				if (!found)
				{
					LOGGER.severe("Compilation successfull, but class coresponding to " + sourcePath.toString() + " not found!");
				}
			}
			
			return executionFailures;
		}
	}
	
	@Override
	public Entry<Path, Throwable> executeScript(Path sourcePath) throws Exception
	{
		final Map<Path, Throwable> executionFailures = executeScripts(Arrays.asList(sourcePath));
		if (!executionFailures.isEmpty())
		{
			return executionFailures.entrySet().iterator().next();
		}
		return null;
	}
}
