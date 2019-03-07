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
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.templates.L2CubicTemplate;
import com.l2jmobius.gameserver.model.cubic.CubicSkill;
import com.l2jmobius.gameserver.model.cubic.ICubicConditionHolder;
import com.l2jmobius.gameserver.model.cubic.conditions.HealthCondition;
import com.l2jmobius.gameserver.model.cubic.conditions.HpCondition;
import com.l2jmobius.gameserver.model.cubic.conditions.HpCondition.HpConditionType;
import com.l2jmobius.gameserver.model.cubic.conditions.RangeCondition;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author UnAfraid
 */
public class CubicData implements IGameXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(CubicData.class.getName());
	
	private final Map<Integer, Map<Integer, L2CubicTemplate>> _cubics = new HashMap<>();
	
	protected CubicData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_cubics.clear();
		parseDatapackDirectory("data/stats/cubics", true);
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _cubics.size() + " cubics.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "cubic", cubicNode ->
		{
			parseTemplate(cubicNode, new L2CubicTemplate(new StatsSet(parseAttributes(cubicNode))));
		}));
	}
	
	/**
	 * @param cubicNode
	 * @param template
	 */
	private void parseTemplate(Node cubicNode, L2CubicTemplate template)
	{
		forEach(cubicNode, IXmlReader::isNode, innerNode ->
		{
			switch (innerNode.getNodeName())
			{
				case "conditions":
				{
					parseConditions(innerNode, template, template);
					break;
				}
				case "skills":
				{
					parseSkills(innerNode, template);
					break;
				}
			}
		});
		_cubics.computeIfAbsent(template.getId(), key -> new HashMap<>()).put(template.getLevel(), template);
	}
	
	/**
	 * @param cubicNode
	 * @param template
	 * @param holder
	 */
	private void parseConditions(Node cubicNode, L2CubicTemplate template, ICubicConditionHolder holder)
	{
		forEach(cubicNode, IXmlReader::isNode, conditionNode ->
		{
			switch (conditionNode.getNodeName())
			{
				case "hp":
				{
					final HpConditionType type = parseEnum(conditionNode.getAttributes(), HpConditionType.class, "type");
					final int hpPer = parseInteger(conditionNode.getAttributes(), "percent");
					holder.addCondition(new HpCondition(type, hpPer));
					break;
				}
				case "range":
				{
					final int range = parseInteger(conditionNode.getAttributes(), "value");
					holder.addCondition(new RangeCondition(range));
					break;
				}
				case "healthPercent":
				{
					final int min = parseInteger(conditionNode.getAttributes(), "min");
					final int max = parseInteger(conditionNode.getAttributes(), "max");
					holder.addCondition(new HealthCondition(min, max));
					break;
				}
				default:
				{
					LOGGER.warning("Attempting to use not implemented condition: " + conditionNode.getNodeName() + " for cubic id: " + template.getId() + " level: " + template.getLevel());
					break;
				}
			}
		});
	}
	
	/**
	 * @param cubicNode
	 * @param template
	 */
	private void parseSkills(Node cubicNode, L2CubicTemplate template)
	{
		forEach(cubicNode, "skill", skillNode ->
		{
			final CubicSkill skill = new CubicSkill(new StatsSet(parseAttributes(skillNode)));
			forEach(cubicNode, "conditions", conditionNode -> parseConditions(cubicNode, template, skill));
			template.getSkills().add(skill);
		});
	}
	
	/**
	 * @param id
	 * @param level
	 * @return the L2CubicTemplate for specified id and level
	 */
	public L2CubicTemplate getCubicTemplate(int id, int level)
	{
		return _cubics.getOrDefault(id, Collections.emptyMap()).get(level);
	}
	
	/**
	 * Gets the single instance of CubicData.
	 * @return single instance of CubicData
	 */
	public static CubicData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final CubicData _instance = new CubicData();
	}
}
