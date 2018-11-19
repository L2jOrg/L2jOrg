package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Skill;
import l2s.gameserver.skills.EffectUseType;
import l2s.gameserver.stats.StatTemplate;
import l2s.gameserver.stats.conditions.Condition;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.templates.skill.restoration.RestorationGroup;
import l2s.gameserver.templates.skill.restoration.RestorationInfo;
import l2s.gameserver.templates.skill.restoration.RestorationItem;

import org.dom4j.Element;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.TreeIntObjectMap;

/**
 * @author Bonux
 **/
public final class SkillParser extends StatParser<SkillHolder>
{
	private static final SkillParser _instance = new SkillParser();

	private final IntObjectMap<IntObjectMap<StatsSet>> _skillsTables = new TreeIntObjectMap<IntObjectMap<StatsSet>>();

	public static SkillParser getInstance()
	{
		return _instance;
	}

	protected SkillParser()
	{
		super(SkillHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/skills/");
	}

	@Override
	public File getCustomXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "custom/skills/");
	}

	@Override
	public String getDTDFileName()
	{
		return "skill.dtd";
	}

	@Override
	protected void afterParseActions()
	{
		_skillsTables.clear();
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> itemIterator = rootElement.elementIterator(); itemIterator.hasNext();)
		{
			Element skillElement = itemIterator.next();

			final int skillId = Integer.parseInt(skillElement.attributeValue("id"));
			final int levels = Integer.parseInt(skillElement.attributeValue("levels"));
			if(levels > 100)
			{
				warn("Error while parse skill[" + skillId + "] (Max level should be less than or equal to 100)!");
				continue;
			}

			_skillsTables.remove(skillId);

			final StatsSet set = new StatsSet();

			set.set("skill_id", skillId);
			set.set("max_level", levels);
			set.set("name", skillElement.attributeValue("name"));

			final IntObjectMap<RestorationInfo> restorations = new TreeIntObjectMap<RestorationInfo>();

			for(Iterator<Element> subIterator = skillElement.elementIterator(); subIterator.hasNext();)
			{
				Element subElement = subIterator.next();

				final String subName = subElement.getName();
				if(subName.equalsIgnoreCase("set"))
					set.set(subElement.attributeValue("name"), subElement.attributeValue("value"));
				else if(subName.equalsIgnoreCase("table"))
					parseTable(subElement, skillId, 1, levels);
				else if(subName.equalsIgnoreCase("restoration"))
					parseRestoration(subElement, restorations);
			}

			for(int skillLevel = 1; skillLevel <= levels; skillLevel++)
			{
				final StatsSet currentSet = set.clone();
				for(Entry<String, Object> entry : currentSet.entrySet())
					currentSet.set(entry.getKey(), parseValue(entry.getValue(), skillId, skillLevel, levels));

				currentSet.set("level", skillLevel);

				currentSet.set("restoration", restorations.get(skillLevel));

				Skill skill = currentSet.getEnum("skillType", Skill.SkillType.class, Skill.SkillType.EFFECT).makeSkill(currentSet);

				for(Iterator<Element> subIterator = skillElement.elementIterator(); subIterator.hasNext();)
				{
					Element subElement = subIterator.next();

					final String subName = subElement.getName();
					if(subName.equalsIgnoreCase("for"))
						parseFor(subElement, skill, skillId, skillLevel, levels);
					else if(subName.equalsIgnoreCase("cond"))
					{
						Condition condition = parseFirstCond(subElement, skillId, skillLevel, levels);
						if(condition != null)
						{
							if(subElement.attributeValue("msgId") != null)
							{
								int msgId = parseNumber(subElement.attributeValue("msgId")).intValue();
								condition.setSystemMsg(msgId);
							}

							skill.attachCondition(condition);
						}
					}
					else if(subName.equalsIgnoreCase("triggers"))
						parseTriggers(subElement, skill, skillId, skillLevel, levels);
				}
				getHolder().addSkill(skill);
			}
		}
	}

	@Override
	protected Object getTableValue(String name, int... arg)
	{
		if(arg.length < 3)
		{
			warn("Error while read table[" + name + "] value for skill (Bad arg's length)!", new Exception());
			return null;
		}

		final int skillId = arg[0];
		final int skillLevel = arg[1];
		final int skillMaxLevel = arg[2];

		Object result = null;

		IntObjectMap<StatsSet> tables = _skillsTables.get(skillId);
		if(tables == null)
		{
			warn("Error while read table[" + name + "] value for skill ID[" + skillId + "], LEVEL[" + skillLevel + "] (Cannot find tables)!");
			return null;
		}

		StatsSet set = tables.get(skillLevel);
		if(set != null)
			result = set.get(name);

		if(result != null)
		{
			String value = String.valueOf(result);
			if(value.isEmpty())
				warn("Error in table[" + name + "] value[" + value + "] for skill ID[" + skillId + "], LEVEL[" + skillLevel + "] (Empty value)!");

			return result;
		}

		warn("Error while read table[" + name + "] value for skill ID[" + skillId + "], LEVEL[" + skillLevel + "] (Cannot find table set)!");
		return null;
	}

	@Override
	protected void parseFor(Element forElement, StatTemplate template, int... arg)
	{
		super.parseFor(forElement, template, arg);

		if(!(template instanceof Skill))
			return;

		Skill skill = (Skill) template;
		for(Iterator<Element> iterator = forElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();
			final String elementName = element.getName();
			if(elementName.equalsIgnoreCase("start_effect"))
				attachEffect(element, skill, EffectUseType.START, arg);
			else if(elementName.equalsIgnoreCase("tick_effect"))
				attachEffect(element, skill, EffectUseType.TICK, arg);
			else if(elementName.equalsIgnoreCase("self_effect"))
				attachEffect(element, skill, EffectUseType.SELF, arg);
			else if(elementName.equalsIgnoreCase("effect"))
				attachEffect(element, skill, EffectUseType.NORMAL, arg);
			else if(elementName.equalsIgnoreCase("end_effect"))
				attachEffect(element, skill, EffectUseType.END, arg);
		}
	}

	private void attachEffect(Element element, Skill skill, EffectUseType useType, int... arg)
	{
		if(element.attributeValue("enabled") != null)
		{
			if(!parseBoolean(element.attributeValue("enabled"), arg))
				return;
		}

		final StatsSet set = new StatsSet();

		if(element.attributeValue("chance") != null)
		{
			int chance = parseNumber(element.attributeValue("chance"), arg).intValue();
			if(chance <= 0)
				return;

			set.set("chance", chance);
		}

		if(element.attributeValue("name") != null)
			set.set("name", parseValue(element.attributeValue("name"), arg));

		if(element.attributeValue("value") != null)
			set.set("value", parseNumber(element.attributeValue("value"), arg).doubleValue());

		if(element.attributeValue("interval") != null)
			set.set("interval", parseNumber(element.attributeValue("interval"), arg).doubleValue());

		if(element.attributeValue("instant") != null)
			set.set("instant", parseBoolean(element.attributeValue("instant"), arg));

		if(element.attributeValue("type") != null)
			set.set("type", parseValue(element.attributeValue("type"), arg));

		EffectTemplate effectTemplate = new EffectTemplate(set, useType);

		parseFor(element, effectTemplate, arg);

		for(Iterator<Element> subIterator = element.elementIterator(); subIterator.hasNext();)
		{
			Element subElement = subIterator.next();

			final String subElementName = subElement.getName();
			if(subElementName.equalsIgnoreCase("def"))
				set.set(subElement.attributeValue("name"), parseValue(subElement.attributeValue("value"), arg));
			else if(subElementName.equalsIgnoreCase("triggers"))
				parseTriggers(subElement, effectTemplate, arg);
			else
			{
				Condition condition = parseCond(subElement, arg);
				if(condition != null)
					effectTemplate.attachCond(condition);
			}
		}

		skill.attachEffect(effectTemplate);
	}

	private void parseRestoration(Element element, IntObjectMap<RestorationInfo> map)
	{
		int skillLevel = Integer.parseInt(element.attributeValue("level"));
		int consumeItemId = element.attributeValue("consume_item_id") == null ? -1 : Integer.parseInt(element.attributeValue("consume_item_id"));
		int consumeItemCount = element.attributeValue("consume_item_count") == null ? 1 : Integer.parseInt(element.attributeValue("consume_item_count"));
		int onFailMessage = element.attributeValue("on_fail_message") == null ? -1 : Integer.parseInt(element.attributeValue("on_fail_message"));
		RestorationInfo restorationInfo = new RestorationInfo(consumeItemId, consumeItemCount, onFailMessage);
		for(Iterator<Element> groupIterator = element.elementIterator(); groupIterator.hasNext();)
		{
			Element groupElement = groupIterator.next();
			double chance = Double.parseDouble(groupElement.attributeValue("chance"));
			RestorationGroup restorationGroup = new RestorationGroup(chance);
			for(Iterator<Element> itemIterator = groupElement.elementIterator(); itemIterator.hasNext();)
			{
				Element itemElement = itemIterator.next();
				int id = Integer.parseInt(itemElement.attributeValue("id"));
				int minCount = Integer.parseInt(itemElement.attributeValue("min_count"));
				int maxCount = itemElement.attributeValue("max_count") == null ? minCount : Integer.parseInt(itemElement.attributeValue("max_count"));
				int enchantLevel = itemElement.attributeValue("enchant_level") == null ? 0 : Integer.parseInt(itemElement.attributeValue("enchant_level"));
				restorationGroup.addRestorationItem(new RestorationItem(id, minCount, maxCount, enchantLevel));
			}
			restorationInfo.addRestorationGroup(restorationGroup);
		}
		map.put(skillLevel, restorationInfo);
	}

	private void parseTable(Element element, int skillId, int firstLevel, int lastLevel)
	{
		String name = element.attributeValue("name");
		if(name.charAt(0) != '#')
		{
			warn("Error while parse table[" + name + "] value for skill ID[" + skillId + "] (Table name must start with #)!");
			return;
		}

		if(name.lastIndexOf('#') != 0)
		{
			warn("Error while parse table[" + name + "] value for skill ID[" + skillId + "] (Table name should not contain # character, but only start with #)!");
			return;
		}

		if(name.contains(";") || name.contains(":") || name.contains(" ") || name.contains("-"))
		{
			warn("Error while parse table[" + name + "] value for skill ID[" + skillId + "] (Table name should not contain characters: ';' ':' '-' or space)!");
			return;
		}

		StringTokenizer data = new StringTokenizer(element.getText());
		List<String> values = new ArrayList<String>();
		while(data.hasMoreTokens())
			values.add(data.nextToken());

		IntObjectMap<StatsSet> tables = _skillsTables.get(skillId);
		if(tables == null)
		{
			tables = new TreeIntObjectMap<StatsSet>();
			_skillsTables.put(skillId, tables);
		}

		int i = 0;
		for(int lvl = firstLevel; lvl <= lastLevel; lvl++)
		{
			StatsSet set = tables.get(lvl);
			if(set == null)
			{
				set = new StatsSet();
				tables.put(lvl, set);
			}
			else if(set.containsKey(name))
			{
				warn("Error while parse table[" + name + "] value for skill ID[" + skillId + "] (Skill have tables with the same name)!");
				return;
			}

			set.set(name, values.get(Math.min(i, values.size() - 1)));
			i++;
		}
	}
}