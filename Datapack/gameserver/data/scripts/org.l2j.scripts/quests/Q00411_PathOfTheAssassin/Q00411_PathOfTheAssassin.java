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
package quests.Q00411_PathOfTheAssassin;

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
 * Path Of The Assassin (411)
 * @author ivantotov
 */
public final class Q00411_PathOfTheAssassin extends Quest
{
	// NPCs
	private static final int TRISKEL = 30416;
	private static final int GUARD_LEIKAN = 30382;
	private static final int ARKENIA = 30419;
	// Items
	private static final int SHILENS_CALL = 1245;
	private static final int ARKENIAS_LETTER = 1246;
	private static final int LEIKANS_NOTE = 1247;
	private static final int MOONSTONE_BEASTS_MOLAR = 1248;
	private static final int SHILENS_TEARS = 1250;
	private static final int ARKENIAS_RECOMMENDATION = 1251;
	// Reward
	private static final int IRON_HEART = 1252;
	// Monster
	private static final int MOONSTONE_BEAST = 20369;
	// Quest Monster
	private static final int CALPICO = 27036;
	// Misc
	private static final int MIN_LEVEL = 19;
	
	public Q00411_PathOfTheAssassin()
	{
		super(411);
		addStartNpc(TRISKEL);
		addTalkId(TRISKEL, GUARD_LEIKAN, ARKENIA);
		addKillId(MOONSTONE_BEAST, CALPICO);
		registerQuestItems(SHILENS_CALL, ARKENIAS_LETTER, LEIKANS_NOTE, MOONSTONE_BEASTS_MOLAR, SHILENS_TEARS, ARKENIAS_RECOMMENDATION);
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
				if (player.getClassId() == ClassId.DARK_FIGHTER)
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (hasQuestItems(player, IRON_HEART))
						{
							htmltext = "30416-04.htm";
						}
						else
						{
							qs.startQuest();
							giveItems(player, SHILENS_CALL, 1);
							htmltext = "30416-05.htm";
						}
					}
					else
					{
						htmltext = "30416-03.htm";
					}
				}
				else if (player.getClassId() == ClassId.ASSASSIN)
				{
					htmltext = "30416-02a.htm";
				}
				else
				{
					htmltext = "30416-02.htm";
				}
				break;
			}
			case "30382-02.html":
			case "30382-04.html":
			{
				htmltext = event;
				break;
			}
			case "30382-03.html":
			{
				if (hasQuestItems(player, ARKENIAS_LETTER))
				{
					takeItems(player, ARKENIAS_LETTER, 1);
					giveItems(player, LEIKANS_NOTE, 1);
					qs.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "30419-02.html":
			case "30419-03.html":
			case "30419-04.html":
			case "30419-06.html":
			{
				htmltext = event;
				break;
			}
			case "30419-05.html":
			{
				if (hasQuestItems(player, SHILENS_CALL))
				{
					takeItems(player, SHILENS_CALL, 1);
					giveItems(player, ARKENIAS_LETTER, 1);
					qs.setCond(2, true);
					htmltext = event;
				}
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
				case MOONSTONE_BEAST:
				{
					if (hasQuestItems(killer, LEIKANS_NOTE) && (getQuestItemsCount(killer, MOONSTONE_BEASTS_MOLAR) < 10))
					{
						giveItems(killer, MOONSTONE_BEASTS_MOLAR, 1);
						if (getQuestItemsCount(killer, MOONSTONE_BEASTS_MOLAR) == 10)
						{
							qs.setCond(4, true);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case CALPICO:
				{
					if (!hasQuestItems(killer, SHILENS_TEARS))
					{
						giveItems(killer, SHILENS_TEARS, 1);
						qs.setCond(6, true);
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
			if (npc.getId() == TRISKEL)
			{
				if (!hasQuestItems(player, IRON_HEART))
				{
					htmltext = "30416-01.htm";
				}
				else
				{
					htmltext = "30416-04.htm";
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == TRISKEL)
			{
				return htmltext;
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case TRISKEL:
				{
					if (!hasAtLeastOneQuestItem(player, ARKENIAS_LETTER, LEIKANS_NOTE, SHILENS_TEARS, IRON_HEART) && hasQuestItems(player, ARKENIAS_RECOMMENDATION))
					{
						giveItems(player, IRON_HEART, 1);
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
						htmltext = "30416-06.html";
					}
					else if (!hasAtLeastOneQuestItem(player, LEIKANS_NOTE, SHILENS_TEARS, ARKENIAS_RECOMMENDATION, IRON_HEART, SHILENS_CALL) && hasQuestItems(player, ARKENIAS_LETTER))
					{
						htmltext = "30416-07.html";
					}
					else if (!hasAtLeastOneQuestItem(player, ARKENIAS_LETTER, SHILENS_TEARS, ARKENIAS_RECOMMENDATION, IRON_HEART, SHILENS_CALL) && hasQuestItems(player, LEIKANS_NOTE))
					{
						htmltext = "30416-08.html";
					}
					else if (!hasAtLeastOneQuestItem(player, ARKENIAS_LETTER, LEIKANS_NOTE, SHILENS_TEARS, ARKENIAS_RECOMMENDATION, IRON_HEART, SHILENS_CALL))
					{
						htmltext = "30416-09.html";
					}
					else if (!hasAtLeastOneQuestItem(player, ARKENIAS_LETTER, LEIKANS_NOTE, ARKENIAS_RECOMMENDATION, IRON_HEART, SHILENS_CALL) && hasQuestItems(player, SHILENS_TEARS))
					{
						htmltext = "30416-10.html";
					}
					else if (!hasAtLeastOneQuestItem(player, ARKENIAS_LETTER, LEIKANS_NOTE, SHILENS_TEARS, ARKENIAS_RECOMMENDATION, IRON_HEART) && hasQuestItems(player, SHILENS_CALL))
					{
						htmltext = "30416-11.html";
					}
					break;
				}
				case GUARD_LEIKAN:
				{
					if (!hasAtLeastOneQuestItem(player, LEIKANS_NOTE, SHILENS_TEARS, ARKENIAS_RECOMMENDATION, IRON_HEART, SHILENS_CALL, MOONSTONE_BEASTS_MOLAR) && hasQuestItems(player, ARKENIAS_LETTER))
					{
						htmltext = "30382-01.html";
					}
					else if (!hasAtLeastOneQuestItem(player, ARKENIAS_LETTER, SHILENS_TEARS, ARKENIAS_RECOMMENDATION, IRON_HEART, SHILENS_CALL, MOONSTONE_BEASTS_MOLAR) && hasQuestItems(player, LEIKANS_NOTE))
					{
						htmltext = "30382-05.html";
					}
					else if (!hasAtLeastOneQuestItem(player, ARKENIAS_LETTER, SHILENS_TEARS, ARKENIAS_RECOMMENDATION, IRON_HEART, SHILENS_CALL) && hasQuestItems(player, LEIKANS_NOTE))
					{
						if (hasQuestItems(player, MOONSTONE_BEASTS_MOLAR) && (getQuestItemsCount(player, MOONSTONE_BEASTS_MOLAR) < 10))
						{
							htmltext = "30382-06.html";
						}
						else
						{
							takeItems(player, LEIKANS_NOTE, 1);
							takeItems(player, MOONSTONE_BEASTS_MOLAR, -1);
							qs.setCond(5, true);
							htmltext = "30382-07.html";
						}
					}
					else if (hasQuestItems(player, SHILENS_TEARS))
					{
						htmltext = "30382-08.html";
					}
					else if (!hasAtLeastOneQuestItem(player, ARKENIAS_LETTER, LEIKANS_NOTE, SHILENS_TEARS, ARKENIAS_RECOMMENDATION, IRON_HEART, SHILENS_CALL, MOONSTONE_BEASTS_MOLAR))
					{
						htmltext = "30382-09.html";
					}
					break;
				}
				case ARKENIA:
				{
					if (!hasAtLeastOneQuestItem(player, ARKENIAS_LETTER, LEIKANS_NOTE, SHILENS_TEARS, ARKENIAS_RECOMMENDATION, IRON_HEART) && hasQuestItems(player, SHILENS_CALL))
					{
						htmltext = "30419-01.html";
					}
					else if (!hasAtLeastOneQuestItem(player, LEIKANS_NOTE, SHILENS_TEARS, ARKENIAS_RECOMMENDATION, IRON_HEART, SHILENS_CALL) && hasQuestItems(player, ARKENIAS_LETTER))
					{
						htmltext = "30419-07.html";
					}
					else if (!hasAtLeastOneQuestItem(player, ARKENIAS_LETTER, LEIKANS_NOTE, ARKENIAS_RECOMMENDATION, IRON_HEART, SHILENS_CALL) && hasQuestItems(player, SHILENS_TEARS))
					{
						takeItems(player, SHILENS_TEARS, 1);
						giveItems(player, ARKENIAS_RECOMMENDATION, 1);
						qs.setCond(7, true);
						htmltext = "30419-08.html";
					}
					else if (!hasAtLeastOneQuestItem(player, ARKENIAS_LETTER, LEIKANS_NOTE, SHILENS_TEARS, IRON_HEART, SHILENS_CALL) && hasQuestItems(player, ARKENIAS_RECOMMENDATION))
					{
						htmltext = "30419-09.html";
					}
					else if (!hasAtLeastOneQuestItem(player, ARKENIAS_LETTER, SHILENS_TEARS, ARKENIAS_RECOMMENDATION, IRON_HEART, SHILENS_CALL) && hasQuestItems(player, LEIKANS_NOTE))
					{
						htmltext = "30419-10.html";
					}
					else if (!hasAtLeastOneQuestItem(player, ARKENIAS_LETTER, LEIKANS_NOTE, SHILENS_TEARS, ARKENIAS_RECOMMENDATION, IRON_HEART, SHILENS_CALL))
					{
						htmltext = "30419-11.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
}