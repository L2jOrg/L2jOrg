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
import com.l2jmobius.commons.util.IXmlReader;
import com.l2jmobius.gameserver.enums.ClanRewardType;
import com.l2jmobius.gameserver.model.holders.ItemHolder;
import com.l2jmobius.gameserver.model.holders.SkillHolder;
import com.l2jmobius.gameserver.model.pledge.ClanRewardBonus;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author UnAfraid
 */
public class ClanRewardData implements IGameXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(ClanRewardData.class.getName());
	private final Map<ClanRewardType, List<ClanRewardBonus>> _clanRewards = new ConcurrentHashMap<>();
	
	protected ClanRewardData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDatapackFile("config/ClanReward.xml");
		for (ClanRewardType type : ClanRewardType.values())
		{
			LOGGER.info(getClass().getSimpleName() + ": Loaded: " + (_clanRewards.containsKey(type) ? _clanRewards.get(type).size() : 0) + " rewards for " + type);
		}
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		forEach(doc.getFirstChild(), IXmlReader::isNode, listNode ->
		{
			switch (listNode.getNodeName())
			{
				case "membersOnline":
				{
					parseMembersOnline(listNode);
					break;
				}
				case "huntingBonus":
				{
					parseHuntingBonus(listNode);
					break;
				}
			}
		});
	}
	
	private void parseMembersOnline(Node node)
	{
		forEach(node, IXmlReader::isNode, memberNode ->
		{
			if ("players".equalsIgnoreCase(memberNode.getNodeName()))
			{
				final NamedNodeMap attrs = memberNode.getAttributes();
				final int requiredAmount = parseInteger(attrs, "size");
				final int level = parseInteger(attrs, "level");
				final ClanRewardBonus bonus = new ClanRewardBonus(ClanRewardType.MEMBERS_ONLINE, level, requiredAmount);
				forEach(memberNode, IXmlReader::isNode, skillNode ->
				{
					if ("skill".equalsIgnoreCase(skillNode.getNodeName()))
					{
						final NamedNodeMap skillAttr = skillNode.getAttributes();
						final int skillId = parseInteger(skillAttr, "id");
						final int skillLevel = parseInteger(skillAttr, "level");
						bonus.setSkillReward(new SkillHolder(skillId, skillLevel));
					}
				});
				_clanRewards.computeIfAbsent(bonus.getType(), key -> new ArrayList<>()).add(bonus);
			}
		});
	}
	
	private void parseHuntingBonus(Node node)
	{
		forEach(node, IXmlReader::isNode, memberNode ->
		{
			if ("hunting".equalsIgnoreCase(memberNode.getNodeName()))
			{
				final NamedNodeMap attrs = memberNode.getAttributes();
				final int requiredAmount = parseInteger(attrs, "points");
				final int level = parseInteger(attrs, "level");
				final ClanRewardBonus bonus = new ClanRewardBonus(ClanRewardType.HUNTING_MONSTERS, level, requiredAmount);
				forEach(memberNode, IXmlReader::isNode, itemsNode ->
				{
					if ("item".equalsIgnoreCase(itemsNode.getNodeName()))
					{
						final NamedNodeMap itemsAttr = itemsNode.getAttributes();
						final int id = parseInteger(itemsAttr, "id");
						final int count = parseInteger(itemsAttr, "count");
						bonus.setItemReward(new ItemHolder(id, count));
					}
				});
				_clanRewards.computeIfAbsent(bonus.getType(), key -> new ArrayList<>()).add(bonus);
			}
		});
	}
	
	public List<ClanRewardBonus> getClanRewardBonuses(ClanRewardType type)
	{
		return _clanRewards.get(type);
	}
	
	public ClanRewardBonus getHighestReward(ClanRewardType type)
	{
		ClanRewardBonus selectedBonus = null;
		for (ClanRewardBonus currentBonus : _clanRewards.get(type))
		{
			if ((selectedBonus == null) || (selectedBonus.getLevel() < currentBonus.getLevel()))
			{
				selectedBonus = currentBonus;
			}
		}
		return selectedBonus;
	}
	
	public Collection<List<ClanRewardBonus>> getClanRewardBonuses()
	{
		return _clanRewards.values();
	}
	
	/**
	 * Gets the single instance of ClanRewardData.
	 * @return single instance of ClanRewardData
	 */
	public static ClanRewardData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ClanRewardData INSTANCE = new ClanRewardData();
	}
}
