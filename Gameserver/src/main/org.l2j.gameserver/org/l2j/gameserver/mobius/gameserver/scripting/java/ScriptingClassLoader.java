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

import java.util.logging.Logger;

/**
 * @author HorridoJoho
 */
public final class ScriptingClassLoader extends ClassLoader
{
	public static final Logger LOGGER = Logger.getLogger(ScriptingClassLoader.class.getName());
	
	private Iterable<ScriptingOutputFileObject> _compiledClasses;
	
	ScriptingClassLoader(ClassLoader parent, Iterable<ScriptingOutputFileObject> compiledClasses)
	{
		super(parent);
		_compiledClasses = compiledClasses;
	}
	
	void removeCompiledClasses()
	{
		_compiledClasses = null;
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException
	{
		for (ScriptingOutputFileObject compiledClass : _compiledClasses)
		{
			if (compiledClass.getJavaName().equals(name))
			{
				final byte[] classBytes = compiledClass.getJavaData();
				return defineClass(name, classBytes, 0, classBytes.length);
			}
		}
		
		return super.findClass(name);
	}
}
