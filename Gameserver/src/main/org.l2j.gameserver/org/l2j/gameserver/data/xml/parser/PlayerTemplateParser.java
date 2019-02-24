package org.l2j.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.dom4j.Element;
import org.l2j.commons.data.xml.AbstractParser;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.holder.PlayerTemplateHolder;
import org.l2j.gameserver.model.base.ClassType;
import org.l2j.gameserver.model.base.Race;
import org.l2j.gameserver.model.base.Sex;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.templates.StatsSet;
import org.l2j.gameserver.templates.item.StartItem;
import org.l2j.gameserver.templates.player.HpMpCpData;
import org.l2j.gameserver.templates.player.PlayerTemplate;
import org.l2j.gameserver.utils.Location;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Bonux
**/
public final class PlayerTemplateParser extends AbstractParser<PlayerTemplateHolder>
{
	private static final PlayerTemplateParser _instance = new PlayerTemplateParser();

	public static PlayerTemplateParser getInstance()
	{
		return _instance;
	}

	private PlayerTemplateParser()
	{
		super(PlayerTemplateHolder.getInstance());
	}

	@Override
	public File getXMLPath() {
		return getSettings(ServerSettings.class).dataPackRootPath().resolve("data/pc_parameters/template_data/").toFile();
	}

	@Override
	public String getDTDFileName()
	{
		return "template_data.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();

			final Race race = Race.valueOf(element.attributeValue("race").toUpperCase());
			final Sex sex = Sex.valueOf(element.attributeValue("sex").toUpperCase());
			final ClassType classtype = ClassType.valueOf(element.attributeValue("type").toUpperCase());

			final StatsSet set = new StatsSet();

			for(Iterator<Element> subIterator = element.elementIterator(); subIterator.hasNext();)
			{
				Element subElement = subIterator.next();

				if("stats_data".equalsIgnoreCase(subElement.getName()))
				{
					for(Element e : subElement.elements())
					{
						if("min_attributes".equalsIgnoreCase(e.getName()) || "max_attributes".equalsIgnoreCase(e.getName()) || "base_attributes".equalsIgnoreCase(e.getName()))
						{
							int _int = Integer.parseInt(e.attributeValue("int"));
							int str = Integer.parseInt(e.attributeValue("str"));
							int con = Integer.parseInt(e.attributeValue("con"));
							int men = Integer.parseInt(e.attributeValue("men"));
							int dex = Integer.parseInt(e.attributeValue("dex"));
							int wit = Integer.parseInt(e.attributeValue("wit"));

							if("min_attributes".equalsIgnoreCase(e.getName()))
							{
								set.set("minINT", _int);
								set.set("minSTR", str);
								set.set("minCON", con);
								set.set("minMEN", men);
								set.set("minDEX", dex);
								set.set("minWIT", wit);
							}
							else if("max_attributes".equalsIgnoreCase(e.getName()))
							{
								set.set("maxINT", _int);
								set.set("maxSTR", str);
								set.set("maxCON", con);
								set.set("maxMEN", men);
								set.set("maxDEX", dex);
								set.set("maxWIT", wit);
							}
							else if("base_attributes".equalsIgnoreCase(e.getName()))
							{
								set.set("baseINT", _int);
								set.set("baseSTR", str);
								set.set("baseCON", con);
								set.set("baseMEN", men);
								set.set("baseDEX", dex);
								set.set("baseWIT", wit);
							}
						}
						else if("armor_defence".equalsIgnoreCase(e.getName()))
						{
							set.set("baseChestDef", e.attributeValue("chest"));
							set.set("baseLegsDef", e.attributeValue("legs"));
							set.set("baseHelmetDef", e.attributeValue("helmet"));
							set.set("baseBootsDef", e.attributeValue("boots"));
							set.set("baseGlovesDef", e.attributeValue("gloves"));
							set.set("basePendantDef", e.attributeValue("pendant"));
							set.set("baseCloakDef", e.attributeValue("cloak"));
						}
						else if("jewel_defence".equalsIgnoreCase(e.getName()))
						{
							set.set("baseREarDef", e.attributeValue("r_earring"));
							set.set("baseLEarDef", e.attributeValue("l_earring"));
							set.set("baseRRingDef", e.attributeValue("r_ring"));
							set.set("baseLRingDef", e.attributeValue("l_ring"));
							set.set("baseNecklaceDef", e.attributeValue("necklace"));
						}
						else if("base_stats".equalsIgnoreCase(e.getName()))
						{
							for(Element e2 : e.elements())
							{
								if("set".equalsIgnoreCase(e2.getName()))
								{
									set.set(e2.attributeValue("name"), e2.attributeValue("value"));
								}
							}
						}
					}
				}
			}

			final PlayerTemplate template = new PlayerTemplate(set, race, sex);

			for(Iterator<Element> subIterator = element.elementIterator(); subIterator.hasNext();)
			{
				Element subElement = subIterator.next();

				if("creation_data".equalsIgnoreCase(subElement.getName()))
				{
					for(Element e : subElement.elements())
					{
						if("start_equipments".equalsIgnoreCase(e.getName()))
						{
							for(Element e2 : e.elements())
							{
								if("equipment".equalsIgnoreCase(e2.getName()))
								{
									int item_id = Integer.parseInt(e2.attributeValue("item_id"));
									long count = Long.parseLong(e2.attributeValue("count"));
									boolean equiped = Boolean.parseBoolean(e2.attributeValue("equiped"));
									int enchant_level = e2.attributeValue("enchant_level") == null ? 0 : Integer.parseInt(e2.attributeValue("enchant_level"));

									template.addStartItem(new StartItem(item_id, count, equiped, enchant_level));
								}
							}
						}
						else if("start_points".equalsIgnoreCase(e.getName()))
						{
							for(Element e2 : e.elements())
							{
								if("point".equalsIgnoreCase(e2.getName()))
								{
									template.addStartLocation(Location.parse(e2));
								}
							}
						}
					}
				}
				else if("stats_data".equalsIgnoreCase(subElement.getName()))
				{
					for(Element e : subElement.elements())
					{
						if("base_stats".equalsIgnoreCase(e.getName()))
						{
							for(Element e2 : e.elements())
							{
								if("regen_data".equalsIgnoreCase(e2.getName()))
								{
									for(Element e3 : e2.elements())
									{
										if("regen".equalsIgnoreCase(e3.getName()))
										{
											StringTokenizer st = new StringTokenizer(e3.attributeValue("level"), "-");
											int minLevel = Integer.parseInt(st.nextToken());
											int maxLevel = minLevel;
											if(st.hasMoreTokens())
												maxLevel = Integer.parseInt(st.nextToken());

											double hp = Double.parseDouble(e3.attributeValue("hp"));
											double mp = Double.parseDouble(e3.attributeValue("mp"));
											double cp = Double.parseDouble(e3.attributeValue("cp"));

											for(int i = minLevel; i <= maxLevel; i++)
												template.addRegenData(i, new HpMpCpData(hp, mp, cp));
										}
									}
								}
							}
						}
					}
				}
			}

			getHolder().addPlayerTemplate(race, classtype, sex, template);
		}
	}
}