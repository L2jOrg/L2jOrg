package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Element;
import l2s.gameserver.data.xml.holder.ResidenceFunctionsHolder;
import l2s.gameserver.model.base.ResidenceFunctionType;
import l2s.gameserver.templates.residence.ResidenceFunctionTemplate;
import l2s.commons.data.xml.AbstractParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.entity.residence.Residence;
import l2s.gameserver.model.entity.residence.ResidenceFunction;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.item.support.MerchantGuard;
import l2s.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 0:18/12.02.2011
 */
public final class ResidenceParser extends AbstractParser<ResidenceHolder>
{
	private static ResidenceParser _instance = new ResidenceParser();

	public static ResidenceParser getInstance()
	{
		return _instance;
	}

	private ResidenceParser()
	{
		super(ResidenceHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/residences/");
	}

	@Override
	public String getDTDFileName()
	{
		return "residence.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();

			String impl = element.attributeValue("impl");
			Class<?> clazz = null;

			StatsSet set = new StatsSet();
			for(Iterator<Attribute> subIterator = element.attributeIterator(); subIterator.hasNext();)
			{
				Attribute subElement = subIterator.next();
				set.set(subElement.getName(), subElement.getValue());
			}

			Residence residence = null;
			try
			{
				clazz = Class.forName("l2s.gameserver.model.entity.residence." + impl);
				Constructor<?> constructor = clazz.getConstructor(StatsSet.class);
				residence = (Residence) constructor.newInstance(set);
				getHolder().addResidence(residence);
			}
			catch(Exception e)
			{
				error("fail to init: " + getCurrentFileName(), e);
				return;
			}

			if(element.getName().equalsIgnoreCase("residence"))
			{
				for(Iterator<Element> subIterator = element.elementIterator(); subIterator.hasNext();)
				{
					Element subElement = subIterator.next();
					String nodeName = subElement.getName();

					if(nodeName.equalsIgnoreCase("available_functions"))
					{
						for(Iterator<Element> nextIterator = subElement.elementIterator(); nextIterator.hasNext();)
						{
							Element nextElement = nextIterator.next();

							ResidenceFunctionType type = ResidenceFunctionType.valueOf(nextElement.attributeValue("type").toUpperCase());
							int level = Integer.parseInt(nextElement.attributeValue("level"));

							ResidenceFunctionTemplate template = ResidenceFunctionsHolder.getInstance().getTemplate(type, level);
							if(template == null)
								continue;
							residence.addAvailableFunction(template.getId());
						}
					}
					else if(nodeName.equalsIgnoreCase("skills"))
					{
						for(Iterator<Element> nextIterator = subElement.elementIterator(); nextIterator.hasNext();)
						{
							Element nextElement = nextIterator.next();
							int id2 = Integer.parseInt(nextElement.attributeValue("id"));
							int level2 = Integer.parseInt(nextElement.attributeValue("level"));

							Skill skill = SkillHolder.getInstance().getSkill(id2, level2);
							if(skill != null)
								residence.addSkill(skill);
						}
					}
					else if(nodeName.equalsIgnoreCase("banish_points"))
					{
						for(Iterator<Element> banishPointsIterator = subElement.elementIterator(); banishPointsIterator.hasNext();)
						{
							Location loc = Location.parse(banishPointsIterator.next());

							residence.addBanishPoint(loc);
						}
					}
					else if(nodeName.equalsIgnoreCase("owner_restart_points"))
					{
						for(Iterator<Element> ownerRestartPointsIterator = subElement.elementIterator(); ownerRestartPointsIterator.hasNext();)
						{
							Location loc = Location.parse(ownerRestartPointsIterator.next());

							residence.addOwnerRestartPoint(loc);
						}
					}
					else if(nodeName.equalsIgnoreCase("other_restart_points"))
					{
						for(Iterator<Element> otherRestartPointsIterator = subElement.elementIterator(); otherRestartPointsIterator.hasNext();)
						{
							Location loc = Location.parse(otherRestartPointsIterator.next());

							residence.addOtherRestartPoint(loc);
						}
					}
					else if(nodeName.equalsIgnoreCase("chaos_restart_points"))
					{
						for(Iterator<Element> chaosRestartPointsIterator = subElement.elementIterator(); chaosRestartPointsIterator.hasNext();)
						{
							Location loc = Location.parse(chaosRestartPointsIterator.next());

							residence.addChaosRestartPoint(loc);
						}
					}
					else if(nodeName.equalsIgnoreCase("merchant_guards"))
					{
						for(Iterator<Element> thirdElementIterator = subElement.elementIterator(); thirdElementIterator.hasNext();)
						{
							Element thirdElement = thirdElementIterator.next();

							int itemId = Integer.parseInt(thirdElement.attributeValue("item_id"));
							int npcId2 = Integer.parseInt(thirdElement.attributeValue("npc_id"));
							int maxGuard = Integer.parseInt(thirdElement.attributeValue("max"));

							((Castle) residence).addMerchantGuard(new MerchantGuard(itemId, npcId2, maxGuard));
						}
					}
				}
			}
			else if(element.getName().equalsIgnoreCase("instant_residence"))
			{
				for(Iterator<Element> subIterator = element.elementIterator(); subIterator.hasNext();)
				{
					Element subElement = subIterator.next();
					String nodeName = subElement.getName();

					if(nodeName.equalsIgnoreCase("functions"))
					{
						for(Iterator<Element> nextIterator = subElement.elementIterator("function"); nextIterator.hasNext();)
						{
							Element nextElement = nextIterator.next();

							ResidenceFunctionType type = ResidenceFunctionType.valueOf(nextElement.attributeValue("type").toUpperCase());
							int level = Integer.parseInt(nextElement.attributeValue("level"));

							ResidenceFunctionTemplate template = ResidenceFunctionsHolder.getInstance().getTemplate(type, level);
							if(template == null)
								continue;
							residence.addActiveFunction(new ResidenceFunction(template, residence.getId()));
						}
					}
				}
			}
		}
	}
}