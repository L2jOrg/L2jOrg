/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.others.ClassMaster;

import ai.AbstractNpcAI;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.data.xml.impl.ClassListData;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.impl.character.player.*;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.spawns.SpawnTemplate;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.network.serverpackets.TutorialCloseHtml;
import org.l2j.gameserver.network.serverpackets.classchange.ExRequestClassChangeUi;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.*;


/**
 * 	Class Master AI.
 * @author Nik
 */
public final class ClassMaster extends AbstractNpcAI
{
	// NPCs
	private static final List<Integer> CLASS_MASTERS = new ArrayList<>();
	static {
		CLASS_MASTERS.add(31756); // Mr. Cat
		CLASS_MASTERS.add(31757); // Queen of Hearts
	}
	// Misc
	private boolean _isEnabled;
	private boolean _spawnClassMasters;
	private boolean _showPopupWindow;
	private static final Logger LOGGER = LoggerFactory.getLogger(ClassMaster.class);
	private final List<ClassChangeData> _classChangeData = new LinkedList<>();
	
	public ClassMaster() {
		new DataLoader().load();
		addStartNpc(CLASS_MASTERS);
		addTalkId(CLASS_MASTERS);
		addFirstTalkId(CLASS_MASTERS);
	}
	
	@Override
	public void onSpawnActivate(SpawnTemplate template)
	{
		if (_spawnClassMasters)
		{
			template.spawnAllIncludingNotDefault(null);
		}
	}
	
	@Override
	public void onSpawnDeactivate(SpawnTemplate template)
	{
		template.despawnAll();
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return "test_server_helper001.html";
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if (!_isEnabled)
		{
			return null;
		}
		
		String htmltext = null;
		final StringTokenizer st = new StringTokenizer(event);
		event = st.nextToken();
		switch (event)
		{
			case "buyitems":
			{
				htmltext = npc.getId() == CLASS_MASTERS.get(0) ? "test_server_helper001a.html" : "test_server_helper001b.html";
				break;
			}
			/*
			 * case "setnoble": { if (player.isNoble()) { htmltext = "test_server_helper025b.html"; } else if (player.getLevel() < 75) { htmltext = "test_server_helper025a.html"; } else { player.setNoble(true); player.broadcastUserInfo(); // TODO: SetOneTimeQuestFlag(talker, 10385, 1); htmltext =
			 * "test_server_helper025.html"; } break; }
			 */
			case "firstclass":
			{
				htmltext = getFirstOccupationChangeHtml(player);
				break;
			}
			case "secondclass":
			{
				htmltext = getSecondOccupationChangeHtml(player);
				break;
			}
			case "thirdclass":
			{
				if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (player.getLevel() > 75))
				{
					if (changeToNextClass(player))
					{
						player.sendPacket(new PlaySound("ItemSound.quest_fanfare_2"));
						player.broadcastUserInfo();
						htmltext = "test_server_helper021.html";
					}
				}
				else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP))
				{
					htmltext = "test_server_helper011.html";
				}
				// else if (player.isInCategory(CategoryType.AWAKEN_GROUP))
				// {
				// htmltext = "test_server_helper011a.html";
				// }
				else
				{
					htmltext = "test_server_helper024.html";
				}
				break;
			}
			/*
			 * case "awaken": { if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.getLevel() > 84)) { if (changeToNextClass(player)) { player.sendPacket(new PlaySound("ItemSound.quest_fanfare_2")); player.broadcastUserInfo(); player.store(false); // Save player cause if server
			 * crashes before this char is saved, he will lose class and the money payed for class change. htmltext = "test_server_helper021.html"; } } else if (player.isInCategory(CategoryType.AWAKEN_GROUP)) { htmltext = "test_server_helper011a.html"; } else { htmltext =
			 * "test_server_helper011b.html"; } break; }
			 */
			case "setclass":
			{
				if (!st.hasMoreTokens())
				{
					return null;
				}
				
				final int classId = Integer.parseInt(st.nextToken());
				boolean canChange = false;
				if ((player.isInCategory(CategoryType.SECOND_CLASS_GROUP) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP)) && (player.getLevel() >= 40)) // In retail you can skip first occupation
				{
					canChange = CategoryManager.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, classId) || (player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && CategoryManager.getInstance().isInCategory(CategoryType.SECOND_CLASS_GROUP, classId));
				}
				else if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && (player.getLevel() >= 20))
				{
					canChange = CategoryManager.getInstance().isInCategory(CategoryType.SECOND_CLASS_GROUP, classId);
				}
				else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (player.getLevel() >= 76))
				{
					canChange = CategoryManager.getInstance().isInCategory(CategoryType.FOURTH_CLASS_GROUP, classId);
				}
				// else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.getLevel() >= 85)) // 9
				// {
				// canChange = CategoryData.getInstance().isInCategory(CategoryType.AWAKEN_GROUP, classId); // 11
				// }
				
				if (canChange)
				{
					int classDataIndex = -1;
					if (st.hasMoreTokens())
					{
						classDataIndex = Integer.parseInt(st.nextToken());
					}
					
					if (checkIfClassChangeHasOptions(player))
					{
						if (classDataIndex == -1)
						{
							htmltext = getHtm(player, "cc_options.html");
							htmltext = htmltext.replace("%name%", ClassListData.getInstance().getClass(classId).getClassName()); // getEscapedClientCode());
							htmltext = htmltext.replace("%options%", getClassChangeOptions(player, classId));
							return htmltext;
						}
					}
					
					final ClassChangeData data = getClassChangeData(classDataIndex);
					if (data != null)
					{
						// Required items.
						if (!data.getItemsRequired().isEmpty())
						{
							for (ItemHolder ri : data.getItemsRequired())
							{
								if (player.getInventory().getInventoryItemCount(ri.getId(), -1) < ri.getCount())
								{
									player.sendMessage("You do not have enough items.");
									return null; // No class change if payment failed.
								}
							}
							for (ItemHolder ri : data.getItemsRequired())
							{
								player.destroyItemByItemId(getClass().getSimpleName(), ri.getId(), ri.getCount(), npc, true);
							}
						}
						// Give possible rewards.
						if (!data.getItemsRewarded().isEmpty())
						{
							for (ItemHolder ri : data.getItemsRewarded())
							{
								giveItems(player, ri);
							}
						}
						// Give possible nobless status reward.
						if (data.isRewardNoblesse())
						{
							player.setNoble(true);
						}
						// Give possible hero status reward.
						if (data.isRewardHero())
						{
							player.setHero(true);
						}
					}
					
					player.setClassId(classId);
					if (player.isSubClassActive())
					{
						player.getSubClasses().get(player.getClassIndex()).setClassId(player.getActiveClass());
					}
					else
					{
						player.setBaseClass(player.getActiveClass());
					}
					if (Config.AUTO_LEARN_SKILLS)
					{
						player.giveAvailableSkills(Config.AUTO_LEARN_FS_SKILLS, true);
					}
					player.store(false); // Save player cause if server crashes before this char is saved, he will lose class and the money payed for class change.
					player.broadcastUserInfo();
					player.sendSkillList();
					player.sendPacket(new PlaySound("ItemSound.quest_fanfare_2"));
					return "test_server_helper021.html";
				}
				break;
			}
			case "clanlevel":
			{
				htmltext = player.isClanLeader() ? "test_server_helper022.html" : "pl014.html";
				break;
			}
			case "learnskills":
			{
				player.giveAvailableSkills(true, true);
				break;
			}
			case "clanlevelup":
			{
				if ((player.getClan() == null) || !player.isClanLeader())
				{
					return null;
				}
				
				if (player.getClan().getLevel() >= 10)
				{
					htmltext = "test_server_helper022a.html";
				}
				else
				{
					player.getClan().setLevel(player.getClan().getLevel() + 1);
					player.getClan().broadcastClanStatus();
				}
				break;
			}
			case "test_server_helper001.html":
			{
				if (CLASS_MASTERS.contains(npc.getId()))
				{
					htmltext = event;
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	private String getFirstOccupationChangeHtml(Player player)
	{
		String htmltext = null;
		if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP))
		{

			if (player.getLevel() < 20)
			{
				htmltext = "test_server_helper027.html";
			}
			else
			{
				switch (player.getClassId())
				{
					case FIGHTER:
					{
						htmltext = "test_server_helper026a.html";
						break;
					}
					case MAGE:
					{
						htmltext = "test_server_helper026b.html";
						break;
					}
					case ELVEN_FIGHTER:
					{
						htmltext = "test_server_helper026c.html";
						break;
					}
					case ELVEN_MAGE:
					{
						htmltext = "test_server_helper026d.html";
						break;
					}
					case DARK_FIGHTER:
					{
						htmltext = "test_server_helper026e.html";
						break;
					}
					case DARK_MAGE:
					{
						htmltext = "test_server_helper026f.html";
						break;
					}
					case ORC_FIGHTER:
					{
						htmltext = "test_server_helper026g.html";
						break;
					}
					case ORC_MAGE:
					{
						htmltext = "test_server_helper026h.html";
						break;
					}
					case DWARVEN_FIGHTER:
					{
						htmltext = "test_server_helper026i.html";
						break;
					}
					case JIN_KAMAEL_SOLDIER:
					{
						htmltext = "test_server_helper026j.html";
						break;
					}
				}
			}
		}
		else if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP))
		{
			htmltext = "test_server_helper028.html";
		}
		else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP))
		{
			htmltext = "test_server_helper010.html";
		}
		else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP))
		{
			htmltext = "test_server_helper011.html";
		}
		// else if (player.isInCategory(CategoryType.AWAKEN_GROUP))
		// {
		// htmltext = "test_server_helper011a.html";
		// }
		return htmltext;
	}
	
	private String getSecondOccupationChangeHtml(Player player)
	{
		String htmltext = null;
		if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP))
		{
			if (player.getLevel() < 40)
			{
				htmltext = "test_server_helper023.html";
			}
			else
			{
				switch (player.getClassId())
				{
					case FIGHTER:
					{
						htmltext = "test_server_helper012.html";
						break;
					}
					case WARRIOR:
					{
						htmltext = "test_server_helper012a.html";
						break;
					}
					case KNIGHT:
					{
						htmltext = "test_server_helper012b.html";
						break;
					}
					case ROGUE:
					{
						htmltext = "test_server_helper012c.html";
						break;
					}
					case MAGE:
					{
						htmltext = "test_server_helper013.html";
						break;
					}
					case WIZARD:
					{
						htmltext = "test_server_helper013a.html";
						break;
					}
					case CLERIC:
					{
						htmltext = "test_server_helper013b.html";
						break;
					}
					case ELVEN_FIGHTER:
					{
						htmltext = "test_server_helper014.html";
						break;
					}
					case ELVEN_KNIGHT:
					{
						htmltext = "test_server_helper014a.html";
						break;
					}
					case ELVEN_SCOUT:
					{
						htmltext = "test_server_helper014b.html";
						break;
					}
					case ELVEN_MAGE:
					{
						htmltext = "test_server_helper015.html";
						break;
					}
					case ELVEN_WIZARD:
					{
						htmltext = "test_server_helper015a.html";
						break;
					}
					case ORACLE:
					{
						htmltext = "test_server_helper015b.html";
						break;
					}
					case DARK_FIGHTER:
					{
						htmltext = "test_server_helper016.html";
						break;
					}
					case PALUS_KNIGHT:
					{
						htmltext = "test_server_helper016a.html";
						break;
					}
					case ASSASSIN:
					{
						htmltext = "test_server_helper016b.html";
						break;
					}
					case DARK_MAGE:
					{
						htmltext = "test_server_helper017.html";
						break;
					}
					case DARK_WIZARD:
					{
						htmltext = "test_server_helper017a.html";
						break;
					}
					case SHILLIEN_ORACLE:
					{
						htmltext = "test_server_helper017b.html";
						break;
					}
					case ORC_FIGHTER:
					{
						htmltext = "test_server_helper018.html";
						break;
					}
					case ORC_RAIDER:
					{
						htmltext = "test_server_helper018a.html";
						break;
					}
					case ORC_MONK:
					{
						htmltext = "test_server_helper018b.html";
						break;
					}
					case ORC_MAGE:
					case ORC_SHAMAN:
					{
						htmltext = "test_server_helper019.html";
						break;
					}
					case DWARVEN_FIGHTER:
					{
						htmltext = "test_server_helper020.html";
						break;
					}
					case ARTISAN:
					{
						htmltext = "test_server_helper020b.html";
						break;
					}
					case SCAVENGER:
					{
						htmltext = "test_server_helper020a.html";
						break;
					}
					case JIN_KAMAEL_SOLDIER:
					case TROOPER:
					{
						htmltext = "test_server_helper020c.html";
						break;
					}
					case SOUL_FINDER:
					{
						htmltext = "test_server_helper020d.html";
						break;
					}
					case WARDER:
					{
						htmltext = "test_server_helper030c.html";
						break;
					}
				}
			}
		}
		else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP))
		{
			htmltext = "test_server_helper010.html";
		}
		else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP))
		{
			htmltext = "test_server_helper011.html";
		}
		// else if (player.isInCategory(CategoryType.AWAKEN_GROUP))
		// {
		// htmltext = "test_server_helper011a.html";
		// }
		else
		{
			htmltext = "test_server_helper029.html";
		}
		return htmltext;
	}
	
	private boolean changeToNextClass(Player player)
	{
		ClassId newClass = Arrays.stream(ClassId.values()).filter(cid -> player.getClassId() == cid.getParent()).findAny().orElse(null);

		if (newClass == null)
		{
			LOGGER.warn(": No new classId found for player " + player);
			return false;
		}
		else if (newClass == player.getClassId())
		{
			LOGGER.warn(": New classId found for player " + player + " is exactly the same as the one he currently is!");
			return false;
		}
		else if (checkIfClassChangeHasOptions(player))
		{
			String html = getHtm(player, "cc_options.html");
			html = html.replace("%name%", ClassListData.getInstance().getClass(newClass.getId()).getClassName()); // getEscapedClientCode());
			html = html.replace("%options%", getClassChangeOptions(player, newClass.getId()));
			showResult(player, html);
			return false;
		}
		else
		{
			final ClassChangeData data = _classChangeData.stream().filter(ccd -> ccd.isInCategory(player)).findFirst().get();
			if (data != null)
			{
				// Required items.
				if (!data.getItemsRequired().isEmpty())
				{
					for (ItemHolder ri : data.getItemsRequired())
					{
						if (player.getInventory().getInventoryItemCount(ri.getId(), -1) < ri.getCount())
						{
							player.sendMessage("You do not have enough items.");
							return false; // No class change if payment failed.
						}
					}
					for (ItemHolder ri : data.getItemsRequired())
					{
						player.destroyItemByItemId(getClass().getSimpleName(), ri.getId(), ri.getCount(), player, true);
					}
				}
				// Give possible rewards.
				if (!data.getItemsRewarded().isEmpty())
				{
					for (ItemHolder ri : data.getItemsRewarded())
					{
						giveItems(player, ri);
					}
				}
				// Give possible nobless status reward.
				if (data.isRewardNoblesse())
				{
					player.setNoble(true);
				}
				// Give possible hero status reward.
				if (data.isRewardHero())
				{
					player.setHero(true);
				}
			}
			
			player.setClassId(newClass.getId());
			if (player.isSubClassActive())
			{
				player.getSubClasses().get(player.getClassIndex()).setClassId(player.getActiveClass());
			}
			else
			{
				player.setBaseClass(player.getActiveClass());
			}

			if (Config.AUTO_LEARN_SKILLS)
			{
				player.giveAvailableSkills(Config.AUTO_LEARN_FS_SKILLS, true);
			}
			player.store(false); // Save player cause if server crashes before this char is saved, he will lose class and the money payed for class change.
			player.broadcastUserInfo();
			player.sendSkillList();
			return true;
		}
	}
	
	private void showPopupWindow(Player player)
	{
		if (!_showPopupWindow)
		{
			return;
		}

		//@formatter:off
		if ((player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && (player.getLevel() >= 20)) ||
			((player.isInCategory(CategoryType.SECOND_CLASS_GROUP) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP)) && (player.getLevel() >= 40)) ||
			(player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (player.getLevel() >= 76)) /*||
			(player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.getLevel() >= 85))*/)
		//@formatter:on
		{
			player.sendPacket(ExRequestClassChangeUi.STATIC_PACKET); // mark id was 1001 - used 2 for quest text
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PRESS_TUTORIAL_MARK)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerPressTutorialMark(OnPlayerPressTutorialMark event)
	{
		final Player player = event.getPlayer();
		
		if (!_showPopupWindow || (event.getMarkId() != 2)) // mark id was 1001 - used 2 for quest text
		{
			return;
		}
		
		String html = null;
		if ((player.isInCategory(CategoryType.SECOND_CLASS_GROUP) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP)) && (player.getLevel() >= 40)) // In retail you can skip first occupation
		{
			html = getHtm(player, getSecondOccupationChangeHtml(player));
		}
		else if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && (player.getLevel() >= 20))
		{
			html = getHtm(player, getFirstOccupationChangeHtml(player));
		}
		else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (player.getLevel() >= 76))
		{
			html = getHtm(player, "qm_thirdclass.html");
		}
		// else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.getLevel() >= 85)) // 9
		// {
		// html = getHtm(player, "qm_awaken.html");
		// }
		
		if (html != null)
		{
			showResult(event.getPlayer(), html);
			// player.sendPacket(new TutorialShowHtml(html));
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_BYPASS)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerBypass(OnPlayerBypass event)
	{
		if (event.getCommand().startsWith("Quest ClassMaster "))
		{
			final String html = onAdvEvent(event.getCommand().substring(18), null, event.getPlayer());
			event.getPlayer().sendPacket(TutorialCloseHtml.STATIC_PACKET);
			showResult(event.getPlayer(), html);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PROFESSION_CHANGE)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerProfessionChange(OnPlayerProfessionChange event)
	{
		showPopupWindow(event.getActiveChar());
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLevelChanged(OnPlayerLevelChanged event) {
		showPopupWindow(event.getActiveChar());
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLogin(OnPlayerLogin event)
	{
		final Player player = event.getPlayer();
		showPopupWindow(event.getPlayer());
	}
	
	private String getClassChangeOptions(Player player, int selectedClassId)
	{
		final StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < _classChangeData.size(); i++)
		{
			final ClassChangeData option = getClassChangeData(i);
			if ((option == null) || !option.getCategories().stream().anyMatch(ct -> player.isInCategory(ct)))
			{
				continue;
			}
			
			sb.append("<tr><td><img src=L2UI_CT1.ChatBalloon_DF_TopCenter width=276 height=1 /></td></tr>");
			sb.append("<tr><td><table bgcolor=3f3f3f width=100%>");
			sb.append("<tr><td align=center><a action=\"bypass -h Quest ClassMaster setclass " + selectedClassId + " " + i + "\">" + option.getName() + ":</a></td></tr>");
			sb.append("<tr><td><table width=276>");
			sb.append("<tr><td>Requirements:</td></tr>");
			if (option.getItemsRequired().isEmpty())
			{
				sb.append("<tr><td><font color=LEVEL>Free</font></td></tr>");
			}
			else
			{
				option.getItemsRequired().forEach(ih ->
				{
					sb.append("<tr><td><font color=\"LEVEL\">" + ih.getCount() + "</font></td><td>" + ItemEngine.getInstance().getTemplate(ih.getId()).getName() + "</td><td width=30></td></tr>");
				});
			}
			sb.append("<tr><td>Rewards:</td></tr>");
			if (option.getItemsRewarded().isEmpty())
			{
				if (option.isRewardNoblesse())
				{
					sb.append("<tr><td><font color=\"LEVEL\">Noblesse status.</font></td></tr>");
				}
				
				if (option.isRewardHero())
				{
					sb.append("<tr><td><font color=\"LEVEL\">Hero status.</font></td></tr>");
				}
				
				if (!option.isRewardNoblesse() && !option.isRewardHero())
				{
					sb.append("<tr><td><font color=LEVEL>none</font></td></tr>");
				}
			}
			else
			{
				option.getItemsRewarded().forEach(ih ->
				{
					sb.append("<tr><td><font color=\"LEVEL\">" + ih.getCount() + "</font></td><td>" + ItemEngine.getInstance().getTemplate(ih.getId()).getName() + "</td><td width=30></td></tr>");
				});
				
				if (option.isRewardNoblesse())
				{
					sb.append("<tr><td><font color=\"LEVEL\">Noblesse status.</font></td></tr>");
				}
				if (option.isRewardHero())
				{
					sb.append("<tr><td><font color=\"LEVEL\">Hero status.</font></td></tr>");
				}
			}
			sb.append("</table></td></tr>");
			sb.append("</table></td></tr>");
			sb.append("<tr><td><img src=L2UI_CT1.ChatBalloon_DF_TopCenter width=276 height=1 /></td></tr>");
		}
		
		return sb.toString();
	}
	
	private static class ClassChangeData
	{
		private final String _name;
		private final List<CategoryType> _appliedCategories;
		private boolean _rewardNoblesse;
		private boolean _rewardHero;
		private List<ItemHolder> _itemsRequired;
		private List<ItemHolder> _itemsRewarded;
		
		public ClassChangeData(String name, List<CategoryType> appliedCategories)
		{
			_name = name;
			_appliedCategories = appliedCategories != null ? appliedCategories : Collections.emptyList();
		}
		
		public String getName()
		{
			return _name;
		}
		
		public List<CategoryType> getCategories()
		{
			return _appliedCategories != null ? _appliedCategories : Collections.emptyList();
		}
		
		public boolean isInCategory(Player player)
		{
			if (_appliedCategories != null)
			{
				for (CategoryType category : _appliedCategories)
				{
					if (player.isInCategory(category))
					{
						return true;
					}
				}
			}
			
			return false;
		}
		
		public boolean isRewardNoblesse()
		{
			return _rewardNoblesse;
		}
		
		public void setRewardNoblesse(boolean rewardNoblesse)
		{
			_rewardNoblesse = rewardNoblesse;
		}
		
		public boolean isRewardHero()
		{
			return _rewardHero;
		}
		
		public void setRewardHero(boolean rewardHero)
		{
			_rewardHero = rewardHero;
		}
		
		void setItemsRequired(List<ItemHolder> itemsRequired)
		{
			_itemsRequired = itemsRequired;
		}
		
		public List<ItemHolder> getItemsRequired()
		{
			return _itemsRequired != null ? _itemsRequired : Collections.emptyList();
		}
		
		void setItemsRewarded(List<ItemHolder> itemsRewarded)
		{
			_itemsRewarded = itemsRewarded;
		}
		
		public List<ItemHolder> getItemsRewarded()
		{
			return _itemsRewarded != null ? _itemsRewarded : Collections.emptyList();
		}
	}
	
	private boolean checkIfClassChangeHasOptions(Player player)
	{
		boolean showOptions = _classChangeData.stream().filter(ccd -> !ccd.getItemsRequired().isEmpty()).anyMatch(ccd -> ccd.isInCategory(player)); // Check if there are requirements
		if (!showOptions)
		{
			showOptions = _classChangeData.stream().filter(ccd -> !ccd.getItemsRewarded().isEmpty()).filter(ccd -> ccd.isInCategory(player)).count() > 1; // Check if there is more than 1 reward to chose.
		}
		
		return showOptions;
	}
	
	private ClassChangeData getClassChangeData(int index)
	{
		if ((index >= 0) && (index < _classChangeData.size()))
		{
			return _classChangeData.get(index);
		}
		
		return null;
	}

	private class DataLoader extends GameXmlReader {

		@Override
		protected Path getSchemaFilePath() {
			return Path.of("config/xsd/classMaster.xsd");
		}

		@Override
		public void load()
		{
			_classChangeData.clear();
			parseFile(new File("config/ClassMaster.xml"));

			LOGGER.info("Loaded {} class change options.", _classChangeData.size());
			releaseResources();
		}

		@Override
		public void parseDocument(Document doc, File f)
		{
			NamedNodeMap attrs;
			for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if ("list".equals(n.getNodeName()))
				{
					for (Node cm = n.getFirstChild(); cm != null; cm = cm.getNextSibling())
					{
						attrs = cm.getAttributes();
						if ("classMaster".equals(cm.getNodeName()))
						{
							_isEnabled = parseBoolean(attrs, "classChangeEnabled", false);
							if (!_isEnabled)
							{
								return;
							}

							_spawnClassMasters = parseBoolean(attrs, "spawnClassMasters", true);
							_showPopupWindow = parseBoolean(attrs, "showPopupWindow", false);

							for (Node c = cm.getFirstChild(); c != null; c = c.getNextSibling())
							{
								attrs = c.getAttributes();
								if ("classChangeOption".equals(c.getNodeName()))
								{
									final List<CategoryType> appliedCategories = new LinkedList<>();
									final List<ItemHolder> requiredItems = new LinkedList<>();
									final List<ItemHolder> rewardedItems = new LinkedList<>();
									boolean setNoble = false;
									boolean setHero = false;
									final String optionName = parseString(attrs, "name", "");
									for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling())
									{
										attrs = b.getAttributes();
										if ("appliesTo".equals(b.getNodeName()))
										{
											for (Node r = b.getFirstChild(); r != null; r = r.getNextSibling())
											{
												attrs = r.getAttributes();
												if ("category".equals(r.getNodeName()))
												{
													final CategoryType category = CategoryType.findByName(r.getTextContent().trim());
													if (category == null)
													{
														LOGGER.error(": Incorrect category type: " + r.getNodeValue());
														continue;
													}

													appliedCategories.add(category);
												}
											}
										}
										if ("rewards".equals(b.getNodeName()))
										{
											for (Node r = b.getFirstChild(); r != null; r = r.getNextSibling())
											{
												attrs = r.getAttributes();
												if ("item".equals(r.getNodeName()))
												{
													final int itemId = parseInteger(attrs, "id");
													final int count = parseInteger(attrs, "count", 1);

													rewardedItems.add(new ItemHolder(itemId, count));
												}
												else if ("setNoble".equals(r.getNodeName()))
												{
													setNoble = true;
												}
												else if ("setHero".equals(r.getNodeName()))
												{
													setHero = true;
												}
											}
										}
										else if ("conditions".equals(b.getNodeName()))
										{
											for (Node r = b.getFirstChild(); r != null; r = r.getNextSibling())
											{
												attrs = r.getAttributes();
												if ("item".equals(r.getNodeName()))
												{
													final int itemId = parseInteger(attrs, "id");
													final int count = parseInteger(attrs, "count", 1);

													requiredItems.add(new ItemHolder(itemId, count));
												}
											}
										}
									}

									if (appliedCategories.isEmpty())
									{
										LOGGER.warn(": Class change option: " + optionName + " has no categories to be applied on. Skipping!");
										continue;
									}

									final ClassChangeData classChangeData = new ClassChangeData(optionName, appliedCategories);
									classChangeData.setItemsRequired(requiredItems);
									classChangeData.setItemsRewarded(rewardedItems);
									classChangeData.setRewardHero(setHero);
									classChangeData.setRewardNoblesse(setNoble);

									_classChangeData.add(classChangeData);
								}
							}
						}
					}
				}
			}
		}
	}


	
	public static AbstractNpcAI provider()
	{
		return new ClassMaster();
	}
}