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
import org.l2j.gameserver.mobius.gameserver.model.ActionDataHolder;
import org.l2j.gameserver.mobius.gameserver.model.StatsSet;
import org.w3c.dom.Document;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author UnAfraid
 */
public class ActionData implements IGameXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(ActionData.class.getName());
	
	private final Map<Integer, ActionDataHolder> _actionData = new HashMap<>();
	private final Map<Integer, Integer> _actionSkillsData = new HashMap<>(); // skillId, actionId
	
	protected ActionData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_actionData.clear();
		_actionSkillsData.clear();
		parseDatapackFile("data/ActionData.xml");
		_actionData.values().stream().filter(h -> h.getHandler().equals("PetSkillUse") || h.getHandler().equals("ServitorSkillUse")).forEach(h -> _actionSkillsData.put(h.getOptionId(), h.getId()));
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _actionData.size() + " player actions.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "action", actionNode ->
		{
			final ActionDataHolder holder = new ActionDataHolder(new StatsSet(parseAttributes(actionNode)));
			_actionData.put(holder.getId(), holder);
		}));
	}
	
	/**
	 * @param id
	 * @return the ActionDataHolder for specified id
	 */
	public ActionDataHolder getActionData(int id)
	{
		return _actionData.get(id);
	}
	
	/**
	 * @param skillId
	 * @return the actionId corresponding to the skillId or -1 if no actionId is found for the specified skill.
	 */
	public int getSkillActionId(int skillId)
	{
		return _actionSkillsData.getOrDefault(skillId, -1);
	}
	
	/**
	 * Gets the single instance of ActionData.
	 * @return single instance of ActionData
	 */
	public static ActionData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ActionData _instance = new ActionData();
	}
}
