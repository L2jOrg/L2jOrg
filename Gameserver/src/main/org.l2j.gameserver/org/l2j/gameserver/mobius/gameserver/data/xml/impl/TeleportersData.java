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
import com.l2jmobius.gameserver.enums.TeleportType;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.teleporter.TeleportHolder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author UnAfraid
 */
public class TeleportersData implements IGameXmlReader
{
	// Logger instance
	private static final Logger LOGGER = Logger.getLogger(TeleportersData.class.getName());
	// Teleporter data
	private final Map<Integer, Map<String, TeleportHolder>> _teleporters = new HashMap<>();
	
	protected TeleportersData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_teleporters.clear();
		parseDatapackDirectory("data/teleporters", true);
		LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _teleporters.size() + " npc teleporters.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		forEach(doc, "list", (list) ->
		{
			forEach(list, "npc", (npc) ->
			{
				final Map<String, TeleportHolder> teleList = new HashMap<>();
				// Parse npc node child
				final int npcId = parseInteger(npc.getAttributes(), "id");
				forEach(npc, (node) ->
				{
					switch (node.getNodeName())
					{
						case "teleport":
						{
							final NamedNodeMap nodeAttrs = node.getAttributes();
							// Parse attributes
							final TeleportType type = parseEnum(nodeAttrs, TeleportType.class, "type");
							final String name = parseString(nodeAttrs, "name", type.name());
							// Parse locations
							final TeleportHolder holder = new TeleportHolder(name, type);
							forEach(node, "location", (location) -> holder.registerLocation(new StatsSet(parseAttributes(location))));
							// Register holder
							if (teleList.putIfAbsent(name, holder) != null)
							{
								LOGGER.warning("Duplicate teleport list (" + name + ") has been found for NPC: " + npcId);
							}
							break;
						}
						case "npcs":
						{
							forEach(node, "npc", (npcNode) ->
							{
								final int id = parseInteger(npcNode.getAttributes(), "id");
								registerTeleportList(id, teleList);
							});
							break;
						}
					}
				});
				registerTeleportList(npcId, teleList);
			});
		});
	}
	
	public int getTeleporterCount()
	{
		return _teleporters.size();
	}
	
	/**
	 * Register teleport data to global teleport list holder. Also show warning when any duplicate occurs.
	 * @param npcId template id of teleporter
	 * @param teleList teleport data to register
	 */
	private void registerTeleportList(int npcId, Map<String, TeleportHolder> teleList)
	{
		_teleporters.put(npcId, teleList);
	}
	
	/**
	 * Gets teleport data for specified NPC and list name
	 * @param npcId template id of teleporter
	 * @param listName name of teleport list
	 * @return {@link TeleportHolder} if found otherwise {@code null}
	 */
	public TeleportHolder getHolder(int npcId, String listName)
	{
		return _teleporters.getOrDefault(npcId, Collections.emptyMap()).get(listName);
	}
	
	/**
	 * Gets the single instance of TeleportersData.
	 * @return single instance of TeleportersData
	 */
	public static TeleportersData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final TeleportersData _instance = new TeleportersData();
	}
}
