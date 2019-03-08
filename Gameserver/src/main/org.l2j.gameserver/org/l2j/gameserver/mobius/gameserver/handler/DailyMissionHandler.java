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

import org.l2j.gameserver.mobius.gameserver.model.DailyMissionDataHolder;
import org.l2j.gameserver.mobius.gameserver.scripting.ScriptEngineManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Sdw
 */
public class DailyMissionHandler
{
	private final Map<String, Function<DailyMissionDataHolder, AbstractDailyMissionHandler>> _handlerFactories = new HashMap<>();
	
	public void registerHandler(String name, Function<DailyMissionDataHolder, AbstractDailyMissionHandler> handlerFactory)
	{
		_handlerFactories.put(name, handlerFactory);
	}
	
	public Function<DailyMissionDataHolder, AbstractDailyMissionHandler> getHandler(String name)
	{
		return _handlerFactories.get(name);
	}
	
	public int size()
	{
		return _handlerFactories.size();
	}
	
	public void executeScript()
	{
		try
		{
			ScriptEngineManager.getInstance().executeDailyMissionMasterHandler();
		}
		catch (Exception e)
		{
			throw new Error("Problems while running DailyMissionMasterHandler", e);
		}
	}
	
	public static DailyMissionHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final DailyMissionHandler _instance = new DailyMissionHandler();
	}
}
