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
package quests.Q00413_PathOfTheShillienOracle;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.GameUtils;

/**
 * Path Of The Shillien Oracle (413)
 * @author ivantotov
 */
public final class Q00413_PathOfTheShillienOracle extends Quest
{
	// NPCs
	private static final int MAGISTER_SIDRA = 30330;
	private static final int PRIEST_ADONIUS = 30375;
	private static final int MAGISTER_TALBOT = 30377;
	// Items
	private static final int SIDRAS_LETTER = 1262;
	private static final int BLANK_SHEET = 1263;
	private static final int BLOODY_RUNE = 1264;
	private static final int GARMIELS_BOOK = 1265;
	private static final int PRAYER_OF_ADONIUS = 1266;
	private static final int PENITENTS_MARK = 1267;
	private static final int ASHEN_BONES = 1268;
	private static final int ANDARIEL_BOOK = 1269;
	// Reward
	private static final int ORB_OF_ABYSS = 1270;
	// Monster
	private static final int ZOMBIE_SOLDIER = 20457;
	private static final int ZOMBIE_WARRIOR = 20458;
	private static final int SHIELD_SKELETON = 20514;
	private static final int SKELETON_INFANTRYMAN = 20515;
	private static final int DARK_SUCCUBUS = 20776;
	// Misc
	private static final int MIN_LEVEL = 19;
	
	public Q00413_PathOfTheShillienOracle()
	{
		super(413);
		addStartNpc(MAGISTER_SIDRA);
		addTalkId(MAGISTER_SIDRA, PRIEST_ADONIUS, MAGISTER_TALBOT);
		addKillId(ZOMBIE_SOLDIER, ZOMBIE_WARRIOR, SHIELD_SKELETON, SKELETON_INFANTRYMAN, DARK_SUCCUBUS);
		registerQuestItems(SIDRAS_LETTER, BLANK_SHEET, BLOODY_RUNE, GARMIELS_BOOK, PRAYER_OF_ADONIUS, PENITENTS_MARK, ASHEN_BONES, ANDARIEL_BOOK);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "ACCEPT":
			{
				if (player.getClassId() == ClassId.DARK_MAGE)
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (hasQuestItems(player, ORB_OF_ABYSS))
						{
							htmltext = "30330-04.htm";
						}
						else
						{
							htmltext = "30330-05.htm";
						}
					}
					else
					{
						htmltext = "30330-02.htm";
					}
				}
				else if (player.getClassId() == ClassId.SHILLIEN_ORACLE)
				{
					htmltext = "30330-02a.htm";
				}
				else
				{
					htmltext = "30330-03.htm";
				}
				break;
			}
			case "30330-06.htm":
			{
				if (!hasQuestItems(player, SIDRAS_LETTER))
				{
					giveItems(player, SIDRAS_LETTER, 1);
				}
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30330-06a.html":
			case "30375-02.html":
			case "30375-03.html":
			{
				htmltext = event;
				break;
			}
			case "30375-04.html":
			{
				if (hasQuestItems(player, PRAYER_OF_ADONIUS))
				{
					takeItems(player, PRAYER_OF_ADONIUS, 1);
					giveItems(player, PENITENTS_MARK, 1);
					qs.setCond(5, true);
				}
				htmltext = event;
				break;
			}
			case "30377-02.html":
			{
				if (hasQuestItems(player, SIDRAS_LETTER))
				{
					takeItems(player, SIDRAS_LETTER, 1);
					giveItems(player, BLANK_SHEET, 5);
					qs.setCond(2, true);
				}
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			switch (npc.getId())
			{
				case ZOMBIE_SOLDIER:
				case ZOMBIE_WARRIOR:
				case SHIELD_SKELETON:
				case SKELETON_INFANTRYMAN:
				{
					if (hasQuestItems(killer, PENITENTS_MARK) && (getQuestItemsCount(killer, ASHEN_BONES) < 10))
					{
						giveItems(killer, ASHEN_BONES, 1);
						if (getQuestItemsCount(killer, ASHEN_BONES) == 10)
						{
							qs.setCond(6, true);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case DARK_SUCCUBUS:
				{
					if (hasQuestItems(killer, BLANK_SHEET))
					{
						giveItems(killer, BLOODY_RUNE, 1);
						takeItems(killer, BLANK_SHEET, 1);
						if (!hasQuestItems(killer, BLANK_SHEET) && (getQuestItemsCount(killer, BLOODY_RUNE) == 5))
						{
							qs.setCond(3, true);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			if (npc.getId() == MAGISTER_SIDRA)
			{
				htmltext = "30330-01.htm";
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == MAGISTER_SIDRA)
			{
				return htmltext;
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case MAGISTER_SIDRA:
				{
					if (hasQuestItems(player, SIDRAS_LETTER))
					{
						htmltext = "30330-07.html";
					}
					else if (hasAtLeastOneQuestItem(player, BLANK_SHEET, BLOODY_RUNE))
					{
						htmltext = "30330-08.html";
					}
					else if (!hasQuestItems(player, ANDARIEL_BOOK) && hasAtLeastOneQuestItem(player, PRAYER_OF_ADONIUS, GARMIELS_BOOK, PENITENTS_MARK, ASHEN_BONES))
					{
						htmltext = "30330-09.html";
					}
					else if (hasAtLeastOneQuestItem(player, ANDARIEL_BOOK, GARMIELS_BOOK))
					{
						giveItems(player, ORB_OF_ABYSS, 1);
						final int level = player.getLevel();
						if (level >= 20)
						{
							addExpAndSp(player, 80314, 5087);
						}
						else if (level == 19)
						{
							addExpAndSp(player, 80314, 5087);
						}
						else
						{
							addExpAndSp(player, 80314, 5087);
						}
						qs.exitQuest(false, true);
						player.sendPacket(new SocialAction(player.getObjectId(), 3));
						htmltext = "30330-10.html";
					}
					break;
				}
				case PRIEST_ADONIUS:
				{
					if (hasQuestItems(player, PRAYER_OF_ADONIUS))
					{
						htmltext = "30375-01.html";
					}
					else if (hasQuestItems(player, PENITENTS_MARK) && !hasAtLeastOneQuestItem(player, ASHEN_BONES, ANDARIEL_BOOK))
					{
						htmltext = "30375-05.html";
					}
					else if (hasQuestItems(player, PENITENTS_MARK))
					{
						if (hasQuestItems(player, ASHEN_BONES) && (getQuestItemsCount(player, ASHEN_BONES) < 10))
						{
							htmltext = "30375-06.html";
						}
						else
						{
							takeItems(player, PENITENTS_MARK, 1);
							takeItems(player, ASHEN_BONES, -1);
							giveItems(player, ANDARIEL_BOOK, 1);
							qs.setCond(7, true);
							htmltext = "30375-07.html";
						}
					}
					else if (hasQuestItems(player, ANDARIEL_BOOK))
					{
						htmltext = "30375-08.html";
					}
					break;
				}
				case MAGISTER_TALBOT:
				{
					if (hasQuestItems(player, SIDRAS_LETTER))
					{
						htmltext = "30377-01.html";
					}
					else if (!hasQuestItems(player, BLOODY_RUNE) && (getQuestItemsCount(player, BLANK_SHEET) == 5))
					{
						htmltext = "30377-03.html";
					}
					else if (hasQuestItems(player, BLOODY_RUNE) && (getQuestItemsCount(player, BLOODY_RUNE) < 5))
					{
						htmltext = "30377-04.html";
					}
					else if (getQuestItemsCount(player, BLOODY_RUNE) >= 5)
					{
						takeItems(player, BLOODY_RUNE, -1);
						giveItems(player, GARMIELS_BOOK, 1);
						giveItems(player, PRAYER_OF_ADONIUS, 1);
						qs.setCond(4, true);
						htmltext = "30377-05.html";
					}
					else if (hasAtLeastOneQuestItem(player, PRAYER_OF_ADONIUS, PENITENTS_MARK, ASHEN_BONES))
					{
						htmltext = "30377-06.html";
					}
					else if (hasQuestItems(player, ANDARIEL_BOOK, GARMIELS_BOOK))
					{
						htmltext = "30377-07.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
}