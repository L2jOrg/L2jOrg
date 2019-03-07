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

import com.l2jmobius.gameserver.scripting.AbstractScriptingEngine;
import com.l2jmobius.gameserver.scripting.IExecutionContext;
import org.openjavac.tools.JavaCompiler;
import org.openjavac.tools.javac.api.JavacTool;

import javax.lang.model.SourceVersion;
import java.util.Arrays;

/**
 * @author HorridoJoho, Mobius
 */
public final class JavaScriptingEngine extends AbstractScriptingEngine
{
	private volatile JavaCompiler _compiler;
	
	public JavaScriptingEngine()
	{
		super("Java Engine", "10", "java");
	}
	
	private void determineCompilerOrThrow()
	{
		if (_compiler == null)
		{
			_compiler = JavacTool.create();
		}
		
		if (_compiler == null)
		{
			throw new IllegalStateException("No JavaCompiler service installed!");
		}
	}
	
	private void ensureCompilerOrThrow()
	{
		if (_compiler == null)
		{
			synchronized (this)
			{
				if (_compiler == null)
				{
					determineCompilerOrThrow();
				}
			}
		}
	}
	
	JavaCompiler getCompiler()
	{
		return _compiler;
	}
	
	@Override
	public IExecutionContext createExecutionContext()
	{
		ensureCompilerOrThrow();
		return new JavaExecutionContext(this);
	}
	
	@Override
	public String getLanguageName()
	{
		return "Java";
	}
	
	@Override
	public String getLanguageVersion()
	{
		ensureCompilerOrThrow();
		return Arrays.deepToString(_compiler.getSourceVersions().toArray(new SourceVersion[0])).replace("RELEASE_", "");
	}
}