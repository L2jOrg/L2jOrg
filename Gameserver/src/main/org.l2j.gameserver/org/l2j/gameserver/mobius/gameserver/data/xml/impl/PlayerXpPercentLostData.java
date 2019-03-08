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
package org.l2j.gameserver.mobius.gameserver.data.xml.impl;

import org.l2j.commons.util.IGameXmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * This class holds the Player Xp Percent Lost Data for each level for players.
 * @author Zealar
 */
public final class PlayerXpPercentLostData implements IGameXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(PlayerXpPercentLostData.class.getName());
	
	private final int _maxlevel = ExperienceData.getInstance().getMaxLevel();
	private final double[] _playerXpPercentLost = new double[_maxlevel + 1];
	
	protected PlayerXpPercentLostData()
	{
		Arrays.fill(_playerXpPercentLost, 1.);
		load();
	}
	
	@Override
	public void load()
	{
		parseDatapackFile("data/stats/chars/playerXpPercentLost.xml");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("xpLost".equalsIgnoreCase(d.getNodeName()))
					{
						final NamedNodeMap attrs = d.getAttributes();
						final Integer level = parseInteger(attrs, "level");
						if (level > _maxlevel)
						{
							break;
						}
						_playerXpPercentLost[level] = parseDouble(attrs, "val");
					}
				}
			}
		}
	}
	
	public double getXpPercent(int level)
	{
		if (level > _maxlevel)
		{
			LOGGER.warning("Require to high level inside PlayerXpPercentLostData (" + level + ")");
			return _playerXpPercentLost[_maxlevel];
		}
		return _playerXpPercentLost[level];
	}
	
	/**
	 * Gets the single instance of PlayerXpPercentLostData.
	 * @return single instance of PlayerXpPercentLostData.
	 */
	public static PlayerXpPercentLostData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PlayerXpPercentLostData _instance = new PlayerXpPercentLostData();
	}
}
