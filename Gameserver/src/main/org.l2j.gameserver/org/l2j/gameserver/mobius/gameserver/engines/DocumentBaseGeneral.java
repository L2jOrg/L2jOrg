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
package org.l2j.gameserver.mobius.gameserver.engines;

import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.conditions.Condition;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;

/**
 * A dummy class designed only to parse conditions
 * @author UnAfraid
 */
public class DocumentBaseGeneral extends DocumentBase
{
	protected DocumentBaseGeneral(File file)
	{
		super(file);
	}
	
	@Override
	protected void parseDocument(Document doc)
	{
	}
	
	@Override
	protected StatsSet getStatsSet()
	{
		return null;
	}
	
	@Override
	protected String getTableValue(String name)
	{
		return null;
	}
	
	@Override
	protected String getTableValue(String name, int idx)
	{
		return null;
	}
	
	public Condition parseCondition(Node n)
	{
		return super.parseCondition(n, null);
	}
	
	public static DocumentBaseGeneral getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final DocumentBaseGeneral _instance = new DocumentBaseGeneral(null);
	}
}
