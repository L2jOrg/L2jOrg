package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractParser;
import l2s.commons.string.StringArrayUtils;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.TransformTemplateHolder;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.Sex;
import l2s.gameserver.model.base.TransformType;
import l2s.gameserver.model.items.LockType;
import l2s.gameserver.templates.BaseStatsBonus;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.player.HpMpCpData;
import l2s.gameserver.templates.player.transform.TransformTemplate;

/**
 * @author Bonux
**/
public final class TransformTemplateParser extends AbstractParser<TransformTemplateHolder>
{
	private static final TransformTemplateParser _instance = new TransformTemplateParser();

	public static TransformTemplateParser getInstance()
	{
		return _instance;
	}

	private TransformTemplateParser()
	{
		super(TransformTemplateHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/pc_parameters/transform_data/");
	}

	@Override
	public String getDTDFileName()
	{
		return "transform_data.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();

			final int id = Integer.parseInt(element.attributeValue("id"));
			final TransformType type = TransformType.valueOf(element.attributeValue("type").toUpperCase());
			final boolean can_swim = Boolean.parseBoolean(element.attributeValue("can_swim"));
			final int spawn_height = element.attributeValue("spawn_height") == null ? 0 : Integer.parseInt(element.attributeValue("spawn_height"));
			final boolean normal_attackable = Boolean.parseBoolean(element.attributeValue("normal_attackable"));

			for(Iterator<Element> sexIterator = element.elementIterator(); sexIterator.hasNext();)
			{
				Element sexElement = sexIterator.next();

				if("male".equalsIgnoreCase(sexElement.getName()) || "female".equalsIgnoreCase(sexElement.getName()))
				{
					final StatsSet set = TransformTemplate.getEmptyStatsSet();
					final Sex sex = Sex.valueOf(sexElement.getName().toUpperCase());

					set.set("id", id);
					set.set("type", type);
					set.set("can_swim", can_swim);
					set.set("spawn_height", spawn_height);
					set.set("normal_attackable", normal_attackable);
					set.set("sex", sex);

					for(Element e : sexElement.elements())
					{
						if("base_attributes".equalsIgnoreCase(e.getName()))
						{
							set.set("baseINT", Integer.parseInt(e.attributeValue("int")));
							set.set("baseSTR", Integer.parseInt(e.attributeValue("str")));
							set.set("baseCON", Integer.parseInt(e.attributeValue("con")));
							set.set("baseMEN", Integer.parseInt(e.attributeValue("men")));
							set.set("baseDEX", Integer.parseInt(e.attributeValue("dex")));
							set.set("baseWIT", Integer.parseInt(e.attributeValue("wit")));
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
						else if("set".equalsIgnoreCase(e.getName()))
						{
							set.set(e.attributeValue("name"), e.attributeValue("value"));
						}
					}

					final TransformTemplate template = new TransformTemplate(set);

					for(Element e : sexElement.elements())
					{
						if("actions".equalsIgnoreCase(e.getName()))
						{
							String[] actions = e.getText().split(" ");
							for(String action : actions)
								template.addAction(Integer.parseInt(action));
						}
						else if("item_check".equalsIgnoreCase(e.getName()))
						{
							LockType check_action = LockType.valueOf(e.attributeValue("action").toUpperCase());
							int[] check_items = StringArrayUtils.stringToIntArray(e.getText(), " ");

							template.setItemCheck(check_action, check_items);
						}
						else if("skills".equalsIgnoreCase(e.getName()))
						{
							for(Iterator<Element> skillIterator = e.elementIterator("skill"); skillIterator.hasNext();)
							{
								Element skillElement = skillIterator.next();

								int skill_id = Integer.parseInt(skillElement.attributeValue("id"));
								int skill_level = skillElement.attributeValue("level") == null ? 1 : Integer.parseInt(skillElement.attributeValue("level"));

								template.addSkill(new SkillLearn(skill_id, skill_level, 0, 0, 0, 0, false, null));
							}
						}
						else if("additional_skills".equalsIgnoreCase(e.getName()))
						{
							for(Iterator<Element> skillIterator = e.elementIterator("skill"); skillIterator.hasNext();)
							{
								Element skillElement = skillIterator.next();

								int skill_id = Integer.parseInt(skillElement.attributeValue("id"));
								int skill_level = skillElement.attributeValue("level") == null ? 1 : Integer.parseInt(skillElement.attributeValue("level"));
								int skill_min_level = skillElement.attributeValue("min_level") == null ? 1 : Integer.parseInt(skillElement.attributeValue("min_level"));

								template.addAddtionalSkill(new SkillLearn(skill_id, skill_level, skill_min_level, 0, 0, 0, false, null));
							}
						}
						else if("base_stats_bonus".equalsIgnoreCase(e.getName()))
						{
							for(Element e2 : e.elements())
							{
								if("bonus".equalsIgnoreCase(e2.getName()))
								{
									int value = Integer.parseInt(e2.attributeValue("value"));
									int str = Integer.parseInt(e2.attributeValue("str"));
									int dex = Integer.parseInt(e2.attributeValue("dex"));
									int con = Integer.parseInt(e2.attributeValue("con"));
									int _int = Integer.parseInt(e2.attributeValue("int"));
									int men = Integer.parseInt(e2.attributeValue("men"));
									int wit = Integer.parseInt(e2.attributeValue("wit"));

									template.addBaseStatsBonus(value, new BaseStatsBonus(_int, str, con, men, dex, wit));
								}
							}
						}
						else if("level_data".equalsIgnoreCase(e.getName()))
						{
							for(Element e2 : e.elements())
							{
								if("level".equalsIgnoreCase(e2.getName()))
								{
									int value = Integer.parseInt(e2.attributeValue("value"));
									double mod = Double.parseDouble(e2.attributeValue("mod"));
									double hp = Double.parseDouble(e2.attributeValue("hp"));
									double mp = Double.parseDouble(e2.attributeValue("mp"));
									double cp = Double.parseDouble(e2.attributeValue("cp"));
									double hp_regen = Double.parseDouble(e2.attributeValue("hp_regen"));
									double mp_regen = Double.parseDouble(e2.attributeValue("mp_regen"));
									double cp_regen = Double.parseDouble(e2.attributeValue("cp_regen"));

									template.addLevelBonus(value, mod);
									template.addHpMpCpData(value, new HpMpCpData(hp, mp, cp));
									template.addRegenData(value, new HpMpCpData(hp_regen, mp_regen, cp_regen));
								}
							}
						}
					}
					getHolder().addTemplate(sex, template);
				}
			}
		}
	}
}