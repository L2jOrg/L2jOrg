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
package org.l2j.gameserver.mobius.gameserver.handler;

import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.conditions.ICondition;
import com.l2jmobius.gameserver.scripting.ScriptEngineManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Sdw
 */
public final class ConditionHandler
{
	private final Map<String, Function<StatsSet, ICondition>> _conditionHandlerFactories = new HashMap<>();
	
	public void registerHandler(String name, Function<StatsSet, ICondition> handlerFactory)
	{
		_conditionHandlerFactories.put(name, handlerFactory);
	}
	
	public Function<StatsSet, ICondition> getHandlerFactory(String name)
	{
		return _conditionHandlerFactories.get(name);
	}
	
	public int size()
	{
		return _conditionHandlerFactories.size();
	}
	
	public void executeScript()
	{
		try
		{
			ScriptEngineManager.getInstance().executeConditionMasterHandler();
		}
		catch (Exception e)
		{
			throw new Error("Problems while running ConditionMasterHandler", e);
		}
	}
	
	private static final class SingletonHolder
	{
		protected static final ConditionHandler _instance = new ConditionHandler();
	}
	
	public static ConditionHandler getInstance()
	{
		return SingletonHolder._instance;
	}
}
