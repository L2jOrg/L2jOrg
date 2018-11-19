package l2s.gameserver.data.xml.parser;

import l2s.commons.data.xml.AbstractParser;
import l2s.commons.string.StringArrayUtils;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.instances.RaidBossInstance;
import l2s.gameserver.model.reward.RewardData;
import l2s.gameserver.model.reward.RewardGroup;
import l2s.gameserver.model.reward.RewardList;
import l2s.gameserver.model.reward.RewardType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.TeleportLocation;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.npc.*;
import l2s.gameserver.utils.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class NpcParser extends AbstractParser<NpcHolder>
{
	private static final NpcParser _instance = new NpcParser();

	public static NpcParser getInstance()
	{
		return _instance;
	}

	private NpcParser()
	{
		super(NpcHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/npc/");
	}

	@Override
	public File getCustomXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "custom/npc/");
	}

	@Override
	public String getDTDFileName()
	{
		return "npc.dtd";
	}

	@Override
	protected void readData(org.dom4j.Element rootElement) throws Exception
	{
		for(Iterator<org.dom4j.Element> npcIterator = rootElement.elementIterator(); npcIterator.hasNext();)
		{
			org.dom4j.Element npcElement = npcIterator.next();
			int npcId = Integer.parseInt(npcElement.attributeValue("id"));
			int templateId = npcElement.attributeValue("template_id") == null ? 0 : Integer.parseInt(npcElement.attributeValue("template_id"));
			String name = npcElement.attributeValue("name");
			String title = npcElement.attributeValue("title");

			StatsSet set = new StatsSet();
			set.set("npcId", npcId);
			set.set("displayId", templateId);
			set.set("name", name);
			set.set("title", title);
			set.set("baseCpReg", 0);
			set.set("baseCpMax", 0);

			for(Iterator<org.dom4j.Element> firstIterator = npcElement.elementIterator(); firstIterator.hasNext();)
			{
				org.dom4j.Element firstElement = firstIterator.next();
				if(firstElement.getName().equalsIgnoreCase("set"))
				{
					set.set(firstElement.attributeValue("name"), firstElement.attributeValue("value"));
				}
				else if(firstElement.getName().equalsIgnoreCase("equip"))
				{
					for(Iterator<org.dom4j.Element> eIterator = firstElement.elementIterator(); eIterator.hasNext();)
					{
						org.dom4j.Element eElement = eIterator.next();
						set.set(eElement.getName(), eElement.attributeValue("item_id"));
					}
				}
				else if(firstElement.getName().equalsIgnoreCase("ai_params"))
				{
					StatsSet ai = new StatsSet();
					for(Iterator<org.dom4j.Element> eIterator = firstElement.elementIterator(); eIterator.hasNext();)
					{
						org.dom4j.Element eElement = eIterator.next();
						ai.set(eElement.attributeValue("name"), eElement.attributeValue("value"));
					}
					set.set("aiParams", ai);
				}
				else if(firstElement.getName().equalsIgnoreCase("attributes"))
				{
					int[] attributeAttack = new int[6];
					int[] attributeDefence = new int[6];
					for(Iterator<org.dom4j.Element> eIterator = firstElement.elementIterator(); eIterator.hasNext();)
					{
						org.dom4j.Element eElement = eIterator.next();
						Element element;
						if(eElement.getName().equalsIgnoreCase("defence"))
						{
							element = Element.getElementByName(eElement.attributeValue("attribute"));
							attributeDefence[element.getId()] = Integer.parseInt(eElement.attributeValue("value"));
						}
						else if(eElement.getName().equalsIgnoreCase("attack"))
						{
							element = Element.getElementByName(eElement.attributeValue("attribute"));
							attributeAttack[element.getId()] = Integer.parseInt(eElement.attributeValue("value"));
						}
					}

					set.set("baseAttributeAttack", attributeAttack);
					set.set("baseAttributeDefence", attributeDefence);
				}
			}

			NpcTemplate template = new NpcTemplate(set);

			for(Iterator<org.dom4j.Element> secondIterator = npcElement.elementIterator(); secondIterator.hasNext();)
			{
				org.dom4j.Element secondElement = secondIterator.next();
				String nodeName = secondElement.getName();
				if(nodeName.equalsIgnoreCase("faction"))
				{
					String factionNames = secondElement.attributeValue("names");
					int factionRange = Integer.parseInt(secondElement.attributeValue("range"));
					Faction faction = new Faction(factionNames, factionRange);
					for(Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext();)
					{
						final org.dom4j.Element nextElement = nextIterator.next();
						int ignoreId = Integer.parseInt(nextElement.attributeValue("npc_id"));
						faction.addIgnoreNpcId(ignoreId);
					}
					template.setFaction(faction);
				}
				else if(nodeName.equalsIgnoreCase("rewardlist"))
					template.addRewardList(parseRewardList(this, secondElement, String.valueOf(npcId)));
				else if (nodeName.equalsIgnoreCase("client_skills") || nodeName.equalsIgnoreCase("skills"))
				{
					for(Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext();)
					{
						org.dom4j.Element nextElement = nextIterator.next();
						int id = Integer.parseInt(nextElement.attributeValue("id"));
						int level = Integer.parseInt(nextElement.attributeValue("level"));

						// Для определения расы используется скилл 4416
						if(id == 4416)
						{
							template.setRace(level);
						}

						Skill skill = SkillHolder.getInstance().getSkill(id, level);

						//TODO
						//if(skill == null || skill.getSkillType() == L2Skill.SkillType.NOTDONE)
						//	unimpl.add(Integer.valueOf(skillId));
						if(skill == null)
						{
							continue;
						}

						String use_type = nextElement.attributeValue("use_type");
						if(use_type != null)
							template.setAIParam(use_type, id + "-" + level);

						template.addSkill(skill);
					}
				}
				else if(nodeName.equalsIgnoreCase("minions"))
				{
					for(Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext();)
					{
						org.dom4j.Element nextElement = nextIterator.next();
						int id = Integer.parseInt(nextElement.attributeValue("npc_id"));
						String ai = nextElement.attributeValue("ai");
						int count = Integer.parseInt(nextElement.attributeValue("count"));

						int respawn = nextElement.attributeValue("respawn") == null ? -1 : Integer.parseInt(nextElement.attributeValue("respawn"));

						template.addMinion(new MinionData(id, ai, count, respawn));
					}
				}
				else if(nodeName.equalsIgnoreCase("teleportlist"))
				{
					for(Iterator<org.dom4j.Element> sublistIterator = secondElement.elementIterator(); sublistIterator.hasNext();)
					{
						org.dom4j.Element subListElement = sublistIterator.next();
						int id = Integer.parseInt(subListElement.attributeValue("id"));
						boolean prime_hours = subListElement.attributeValue("prime_hours") == null ? true : Boolean.parseBoolean(subListElement.attributeValue("prime_hours"));
						List<TeleportLocation> list = new ArrayList<TeleportLocation>();
						for(Iterator<org.dom4j.Element> targetIterator = subListElement.elementIterator(); targetIterator.hasNext();)
						{
							org.dom4j.Element targetElement = targetIterator.next();
							int itemId = Integer.parseInt(targetElement.attributeValue("item_id", "57"));
							long price = Integer.parseInt(targetElement.attributeValue("price"));
							int npcStringId = Integer.parseInt(targetElement.attributeValue("name"));
							int[] castleIds = StringArrayUtils.stringToIntArray(targetElement.attributeValue("castle_id", "0"), ";");
							int questZoneId = Integer.parseInt(targetElement.attributeValue("quest_zone_id", "-1"));
							TeleportLocation loc = new TeleportLocation(itemId, price, npcStringId, castleIds, prime_hours, questZoneId);
							loc.set(Location.parseLoc(targetElement.attributeValue("loc")));
							list.add(loc);
						}
						template.addTeleportList(id, list);
					}
				}
				else if(nodeName.equalsIgnoreCase("walker_route"))
				{
					int id = Integer.parseInt(secondElement.attributeValue("id"));
					WalkerRouteType type = secondElement.attributeValue("type") == null ? WalkerRouteType.LENGTH : WalkerRouteType.valueOf(secondElement.attributeValue("type").toUpperCase());
					WalkerRoute walkerRoute = new WalkerRoute(id, type);
					for(Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext();)
					{
						org.dom4j.Element nextElement = nextIterator.next();
						Location loc = Location.parse(nextElement);
						int[] phrasesIds = StringArrayUtils.stringToIntArray((nextElement.attributeValue("phrase_id") == null) ? "" : nextElement.attributeValue("phrase_id"), ";");
						NpcString[] phrases = new NpcString[phrasesIds.length];
						for(int i = 0; i < phrasesIds.length; ++i)
							phrases[i] = NpcString.valueOf(phrasesIds[i]);

						int socialActionId = nextElement.attributeValue("social_action_id") == null ? -1 : Integer.parseInt(nextElement.attributeValue("social_action_id"));
						int delay = nextElement.attributeValue("delay") == null ? 0 : Integer.parseInt(nextElement.attributeValue("delay"));
						boolean running = nextElement.attributeValue("running") == null ? false : Boolean.parseBoolean(nextElement.attributeValue("running"));
						boolean teleport = nextElement.attributeValue("teleport") == null ? false : Boolean.parseBoolean(nextElement.attributeValue("teleport"));
						walkerRoute.addPoint(new WalkerRoutePoint(loc, phrases, socialActionId, delay, running, teleport));
					}
					template.addWalkerRoute(walkerRoute);
				}
				else if(nodeName.equalsIgnoreCase("random_actions"))
				{
					boolean random_order = secondElement.attributeValue("random_order") == null ? false : Boolean.parseBoolean(secondElement.attributeValue("random_order"));
					RandomActions randomActions = new RandomActions(random_order);
					for(Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext();)
					{
						org.dom4j.Element nextElement = nextIterator.next();
						int id = Integer.parseInt(nextElement.attributeValue("id"));
						NpcString phrase = nextElement.attributeValue("phrase_id") == null ? null : NpcString.valueOf(Integer.parseInt(nextElement.attributeValue("phrase_id")));
						int socialActionId = nextElement.attributeValue("social_action_id") == null ? -1 : Integer.parseInt(nextElement.attributeValue("social_action_id"));
						int delay = nextElement.attributeValue("delay") == null ? 0 : Integer.parseInt(nextElement.attributeValue("delay"));
						randomActions.addAction(new RandomActions.Action(id, phrase, socialActionId, delay));
					}
					template.setRandomActions(randomActions);
				}
			}

			for(Iterator<org.dom4j.Element> secondIterator = npcElement.elementIterator("database_rewardlist"); secondIterator.hasNext();)
			{
				org.dom4j.Element secondElement = secondIterator.next();

				RewardList list = new RewardList(RewardType.RATED_GROUPED, false);

				if(!template.isInstanceOf(RaidBossInstance.class))
				{
					RewardGroup equipAndPiecesGroup = null;
					RewardGroup etcGroup = null;

					for(Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator("reward"); nextIterator.hasNext();)
					{
						org.dom4j.Element nextElement = nextIterator.next();

						RewardData data = parseReward(nextElement);
						ItemTemplate itemTemplate = data.getItem();
						if(itemTemplate.isAdena())
						{
							RewardGroup adenaGroup = new RewardGroup(data.getChance());
							data.setChance(RewardList.MAX_CHANCE);
							adenaGroup.addData(data);
							list.add(adenaGroup);
						}
						else if(itemTemplate.isArmor() || itemTemplate.isWeapon() || itemTemplate.isAccessory() || itemTemplate.isKeyMatherial())
						{
							if(equipAndPiecesGroup == null)
								equipAndPiecesGroup = new RewardGroup(RewardList.MAX_CHANCE);
							equipAndPiecesGroup.addData(data);
						}
						else
						{
							if(etcGroup == null)
								etcGroup = new RewardGroup(RewardList.MAX_CHANCE);
							etcGroup.addData(data);
						}
					}

					if(equipAndPiecesGroup != null)
					{
						equipAndPiecesGroup.setChance(RewardList.MAX_CHANCE);

						for(RewardData data : equipAndPiecesGroup.getItems())
							data.setChance(data.getChance());

						list.add(equipAndPiecesGroup);
					}

					if(etcGroup != null)
					{
						etcGroup.setChance(RewardList.MAX_CHANCE);

						for(RewardData data : etcGroup.getItems())
							data.setChance(data.getChance());

						list.add(etcGroup);
					}
				}
				else
				{
					for(Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator("reward"); nextIterator.hasNext();)
					{
						org.dom4j.Element nextElement = nextIterator.next();

						RewardGroup group = new RewardGroup(RewardList.MAX_CHANCE);
						group.addData(parseReward(nextElement));
						list.add(group);
					}
				}

				template.addRewardList(list);
			}

			getHolder().addTemplate(template);
		}
	}

	public static RewardList parseRewardList(AbstractParser<?> parser, org.dom4j.Element element, String debugString)
	{
		RewardType type = RewardType.valueOf(element.attributeValue("type"));
		boolean autoLoot = (element.attributeValue("auto_loot") != null) && (Boolean.parseBoolean(element.attributeValue("auto_loot")));
		RewardList list = new RewardList(type, autoLoot);

		for(Iterator<org.dom4j.Element> nextIterator = element.elementIterator(); nextIterator.hasNext();)
		{
			org.dom4j.Element nextElement = nextIterator.next();
			String nextName = nextElement.getName();
			boolean notGroupType = type == RewardType.SWEEP || type == RewardType.NOT_RATED_NOT_GROUPED;
			if(nextName.equalsIgnoreCase("group"))
			{
				double enterChance = nextElement.attributeValue("chance") == null ? RewardList.MAX_CHANCE : Double.parseDouble(nextElement.attributeValue("chance")) * 10000.0;

				RewardGroup group = notGroupType ? null : new RewardGroup(enterChance);
				for(Iterator<org.dom4j.Element> rewardIterator = nextElement.elementIterator(); rewardIterator.hasNext();)
				{
					org.dom4j.Element rewardElement = rewardIterator.next();
					RewardData data = parseReward(rewardElement);
					if(Config.DISABLE_DROP_EXCEPT_ITEM_IDS.isEmpty() || Config.DISABLE_DROP_EXCEPT_ITEM_IDS.contains(data.getItemId()))
					{
						if(notGroupType)
							parser.warn("Can't load rewardlist from group: " + debugString + "; type: " + type);
						else
							group.addData(data);
					}
				}
				if(group != null && !group.getItems().isEmpty())
					list.add(group);
			}
			else if(nextName.equalsIgnoreCase("reward"))
			{
				if(!notGroupType)
					parser.warn("Reward can't be without group(and not grouped): " + debugString + "; type: " + type);
				else
				{
					RewardData data = parseReward(nextElement);
					if(Config.DISABLE_DROP_EXCEPT_ITEM_IDS.isEmpty() || Config.DISABLE_DROP_EXCEPT_ITEM_IDS.contains(data.getItemId()))
					{
						RewardGroup g = new RewardGroup(RewardList.MAX_CHANCE);
						g.addData(data);
						list.add(g);
					}
				}
			}
		}
		return list;
	}

	private static RewardData parseReward(org.dom4j.Element rewardElement)
	{
		int itemId = Integer.parseInt(rewardElement.attributeValue("item_id"));
		int min = Integer.parseInt(rewardElement.attributeValue("min"));
		int max = Integer.parseInt(rewardElement.attributeValue("max"));

		int chance = (int)(Double.parseDouble(rewardElement.attributeValue("chance")) * 10000.0);

		RewardData data = new RewardData(itemId);
		data.setChance(chance);

		data.setMinDrop(min);
		data.setMaxDrop(max);

		return data;
	}
}