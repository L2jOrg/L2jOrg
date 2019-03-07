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

import com.l2jmobius.gameserver.model.skills.targets.AffectScope;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nik
 */
public class AffectScopeHandler implements IHandler<IAffectScopeHandler, Enum<AffectScope>>
{
	private final Map<Enum<AffectScope>, IAffectScopeHandler> _datatable;
	
	protected AffectScopeHandler()
	{
		_datatable = new HashMap<>();
	}
	
	@Override
	public void registerHandler(IAffectScopeHandler handler)
	{
		_datatable.put(handler.getAffectScopeType(), handler);
	}
	
	@Override
	public synchronized void removeHandler(IAffectScopeHandler handler)
	{
		_datatable.remove(handler.getAffectScopeType());
	}
	
	@Override
	public IAffectScopeHandler getHandler(Enum<AffectScope> affectScope)
	{
		return _datatable.get(affectScope);
	}
	
	@Override
	public int size()
	{
		return _datatable.size();
	}
	
	public static AffectScopeHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final AffectScopeHandler _instance = new AffectScopeHandler();
	}
}
