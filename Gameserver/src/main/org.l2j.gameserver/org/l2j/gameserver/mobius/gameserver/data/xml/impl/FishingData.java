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

import com.l2jmobius.commons.util.IGameXmlReader;
import com.l2jmobius.gameserver.model.FishingBaitData;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This class holds the Fishing information.
 * @author bit
 */
public final class FishingData implements IGameXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(FishingData.class.getName());
	private final Map<Integer, FishingBaitData> _baitData = new HashMap<>();
	private int _baitDistanceMin;
	private int _baitDistanceMax;
	private double _expRateMin;
	private double _expRateMax;
	private double _spRateMin;
	private double _spRateMax;
	
	/**
	 * Instantiates a new fishing data.
	 */
	protected FishingData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_baitData.clear();
		parseDatapackFile("data/Fishing.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded Fishing Data.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node listItem = n.getFirstChild(); listItem != null; listItem = listItem.getNextSibling())
				{
					switch (listItem.getNodeName())
					{
						case "baitDistance":
						{
							_baitDistanceMin = parseInteger(listItem.getAttributes(), "min");
							_baitDistanceMax = parseInteger(listItem.getAttributes(), "max");
							break;
						}
						case "experienceRate":
						{
							_expRateMin = parseDouble(listItem.getAttributes(), "min");
							_expRateMax = parseDouble(listItem.getAttributes(), "max");
							break;
						}
						case "skillPointsRate":
						{
							_spRateMin = parseDouble(listItem.getAttributes(), "min");
							_spRateMax = parseDouble(listItem.getAttributes(), "max");
							break;
						}
						case "baits":
						{
							for (Node bait = listItem.getFirstChild(); bait != null; bait = bait.getNextSibling())
							{
								if ("bait".equalsIgnoreCase(bait.getNodeName()))
								{
									final NamedNodeMap attrs = bait.getAttributes();
									final int itemId = parseInteger(attrs, "itemId");
									final int level = parseInteger(attrs, "level");
									final int minPlayerLevel = parseInteger(attrs, "minPlayerLevel");
									final double chance = parseDouble(attrs, "chance");
									final int timeMin = parseInteger(attrs, "timeMin");
									final int timeMax = parseInteger(attrs, "timeMax");
									final int waitMin = parseInteger(attrs, "waitMin");
									final int waitMax = parseInteger(attrs, "waitMax");
									final FishingBaitData baitData = new FishingBaitData(itemId, level, minPlayerLevel, chance, timeMin, timeMax, waitMin, waitMax);
									
									for (Node c = bait.getFirstChild(); c != null; c = c.getNextSibling())
									{
										if ("catch".equalsIgnoreCase(c.getNodeName()))
										{
											baitData.addReward(parseInteger(c.getAttributes(), "itemId"));
										}
									}
									_baitData.put(baitData.getItemId(), baitData);
								}
							}
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Gets the fishing rod.
	 * @param baitItemId the item id
	 * @return A list of reward item ids
	 */
	public FishingBaitData getBaitData(int baitItemId)
	{
		return _baitData.get(baitItemId);
	}
	
	public int getBaitDistanceMin()
	{
		return _baitDistanceMin;
	}
	
	public int getBaitDistanceMax()
	{
		return _baitDistanceMax;
	}
	
	public double getExpRateMin()
	{
		return _expRateMin;
	}
	
	public double getExpRateMax()
	{
		return _expRateMax;
	}
	
	public double getSpRateMin()
	{
		return _spRateMin;
	}
	
	public double getSpRateMax()
	{
		return _spRateMax;
	}
	
	/**
	 * Gets the single instance of FishingData.
	 * @return single instance of FishingData
	 */
	public static FishingData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final FishingData _instance = new FishingData();
	}
}
