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

import org.openjavac.tools.FileObject;
import org.openjavac.tools.JavaFileObject;
import org.openjavac.tools.JavaFileObject.Kind;
import org.openjavac.tools.StandardJavaFileManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author HorridoJoho
 */
final class ScriptingFileManager implements StandardJavaFileManager
{
	private final StandardJavaFileManager _wrapped;
	private final LinkedList<ScriptingOutputFileObject> _classOutputs = new LinkedList<>();
	
	public ScriptingFileManager(StandardJavaFileManager wrapped)
	{
		_wrapped = wrapped;
	}
	
	Iterable<ScriptingOutputFileObject> getCompiledClasses()
	{
		return Collections.unmodifiableCollection(_classOutputs);
	}
	
	@Override
	public int isSupportedOption(String option)
	{
		return _wrapped.isSupportedOption(option);
	}
	
	@Override
	public ClassLoader getClassLoader(Location location)
	{
		return _wrapped.getClassLoader(location);
	}
	
	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException
	{
		return _wrapped.list(location, packageName, kinds, recurse);
	}
	
	@Override
	public String inferBinaryName(Location location, JavaFileObject file)
	{
		return _wrapped.inferBinaryName(location, file);
	}
	
	@Override
	public boolean isSameFile(FileObject a, FileObject b)
	{
		return _wrapped.isSameFile(a, b);
	}
	
	@Override
	public boolean handleOption(String current, Iterator<String> remaining)
	{
		return _wrapped.handleOption(current, remaining);
	}
	
	@Override
	public boolean hasLocation(Location location)
	{
		return _wrapped.hasLocation(location);
	}
	
	@Override
	public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException
	{
		return _wrapped.getJavaFileForInput(location, className, kind);
	}
	
	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException
	{
		if (kind != Kind.CLASS)
		{
			return _wrapped.getJavaFileForOutput(location, className, kind, sibling);
		}
		
		if (className.contains("/"))
		{
			className = className.replace('/', '.');
		}
		
		ScriptingOutputFileObject fileObject;
		if (sibling != null)
		{
			fileObject = new ScriptingOutputFileObject(Paths.get(sibling.getName()), className, className.substring(className.lastIndexOf('.') + 1));
		}
		else
		{
			fileObject = new ScriptingOutputFileObject(null, className, className.substring(className.lastIndexOf('.') + 1));
		}
		
		_classOutputs.add(fileObject);
		return fileObject;
	}
	
	@Override
	public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException
	{
		return _wrapped.getFileForInput(location, packageName, relativeName);
	}
	
	@Override
	public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException
	{
		return _wrapped.getFileForOutput(location, packageName, relativeName, sibling);
	}
	
	@Override
	public void flush() throws IOException
	{
		_wrapped.flush();
	}
	
	@Override
	public void close() throws IOException
	{
		_wrapped.close();
	}
	
	@Override
	public Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(Iterable<? extends File> files)
	{
		return _wrapped.getJavaFileObjectsFromFiles(files);
	}
	
	@Override
	public Iterable<? extends JavaFileObject> getJavaFileObjects(File... files)
	{
		return _wrapped.getJavaFileObjects(files);
	}
	
	@Override
	public Iterable<? extends JavaFileObject> getJavaFileObjectsFromStrings(Iterable<String> names)
	{
		return _wrapped.getJavaFileObjectsFromStrings(names);
	}
	
	@Override
	public Iterable<? extends JavaFileObject> getJavaFileObjects(String... names)
	{
		return _wrapped.getJavaFileObjects(names);
	}
	
	@Override
	public void setLocation(Location location, Iterable<? extends File> path) throws IOException
	{
		_wrapped.setLocation(location, path);
		
	}
	
	@Override
	public Iterable<? extends File> getLocation(Location location)
	{
		return _wrapped.getLocation(location);
	}
}
