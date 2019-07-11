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
package quests.Q00407_PathOfTheElvenScout;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.GameUtils;

/**
 * Path of the Elven Scout (407)
 * @author ivantotov
 */
public final class Q00407_PathOfTheElvenScout extends Quest
{
	// NPCs
	private static final int MASTER_REORIA = 30328;
	private static final int GUARD_BABENCO = 30334;
	private static final int GUARD_MORETTI = 30337;
	private static final int PRIAS = 30426;
	// Items
	private static final int REISAS_LETTER = 1207;
	private static final int PRIASS_1ND_TORN_LETTER = 1208;
	private static final int PRIASS_2ND_TORN_LETTER = 1209;
	private static final int PRIASS_3ND_TORN_LETTER = 1210;
	private static final int PRIASS_4ND_TORN_LETTER = 1211;
	private static final int MORETTIES_HERB = 1212;
	private static final int MORETTIS_LETTER = 1214;
	private static final int PRIASS_LETTER = 1215;
	private static final int HONORARY_GUARD = 1216;
	private static final int REISAS_RECOMMENDATION = 1217;
	private static final int RUSTED_KEY = 1293;
	// Monster
	private static final int OL_MAHUM_PATROL = 20053;
	// Quest Monster
	private static final int OL_MAHUM_SENTRY = 27031;
	// Misc
	private static final int MIN_LEVEL = 19;
	
	public Q00407_PathOfTheElvenScout()
	{
		super(407);
		addStartNpc(MASTER_REORIA);
		addTalkId(MASTER_REORIA, GUARD_BABENCO, GUARD_MORETTI, PRIAS);
		addKillId(OL_MAHUM_PATROL, OL_MAHUM_SENTRY);
		addAttackId(OL_MAHUM_PATROL, OL_MAHUM_SENTRY);
		registerQuestItems(REISAS_LETTER, PRIASS_1ND_TORN_LETTER, PRIASS_2ND_TORN_LETTER, PRIASS_3ND_TORN_LETTER, PRIASS_4ND_TORN_LETTER, MORETTIES_HERB, MORETTIS_LETTER, PRIASS_LETTER, HONORARY_GUARD, RUSTED_KEY);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, Player player)
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
				if (player.getClassId() == ClassId.ELVEN_FIGHTER)
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (hasQuestItems(player, REISAS_RECOMMENDATION))
						{
							htmltext = "30328-04.htm";
						}
						else
						{
							qs.startQuest();
							qs.unset("variable");
							giveItems(player, REISAS_LETTER, 1);
							htmltext = "30328-05.htm";
						}
					}
					else
					{
						htmltext = "30328-03.htm";
					}
				}
				else if (player.getClassId() == ClassId.ELVEN_SCOUT)
				{
					htmltext = "30328-02a.htm";
				}
				else
				{
					htmltext = "30328-02.htm";
				}
				break;
			}
			case "30337-02.html":
			{
				htmltext = event;
				break;
			}
			case "30337-03.html":
			{
				if (hasQuestItems(player, REISAS_LETTER))
				{
					takeItems(player, REISAS_LETTER, -1);
					qs.set("variable", 1);
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(L2Npc npc, Player attacker, int damage, boolean isSummon)
	{
		final QuestState qs = getQuestState(attacker, false);
		
		if ((qs != null) && qs.isStarted())
		{
			npc.setScriptValue(attacker.getObjectId());
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, Player killer, boolean isSummon)
	{
		if (npc.isScriptValue(killer.getObjectId()) && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, false))
		{
			final QuestState qs = getQuestState(killer, false);
			if (qs == null)
			{
				return null;
			}
			
			if (npc.getId() == OL_MAHUM_SENTRY)
			{
				if (qs.isCond(5) && (getRandom(10) < 6))
				{
					if (hasQuestItems(qs.getPlayer(), MORETTIES_HERB, MORETTIS_LETTER) && !hasQuestItems(qs.getPlayer(), RUSTED_KEY))
					{
						giveItems(qs.getPlayer(), RUSTED_KEY, 1);
						qs.setCond(6, true);
					}
				}
			}
			else if (qs.isCond(2))
			{
				final boolean has1stLetter = hasQuestItems(qs.getPlayer(), PRIASS_1ND_TORN_LETTER);
				final boolean has2ndLetter = hasQuestItems(qs.getPlayer(), PRIASS_2ND_TORN_LETTER);
				final boolean has3rdLetter = hasQuestItems(qs.getPlayer(), PRIASS_3ND_TORN_LETTER);
				final boolean has4thLetter = hasQuestItems(qs.getPlayer(), PRIASS_4ND_TORN_LETTER);
				
				if (!(has1stLetter && has2ndLetter && has3rdLetter && has4thLetter))
				{
					if (!has1stLetter)
					{
						giveLetterAndCheckState(PRIASS_1ND_TORN_LETTER, qs);
					}
					else if (!has2ndLetter)
					{
						giveLetterAndCheckState(PRIASS_2ND_TORN_LETTER, qs);
					}
					else if (!has3rdLetter)
					{
						giveLetterAndCheckState(PRIASS_3ND_TORN_LETTER, qs);
					}
					else if (!has4thLetter)
					{
						giveLetterAndCheckState(PRIASS_4ND_TORN_LETTER, qs);
					}
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private void giveLetterAndCheckState(int letterId, QuestState qs)
	{
		giveItems(qs.getPlayer(), letterId, 1);
		
		if (getQuestItemsCount(qs.getPlayer(), PRIASS_1ND_TORN_LETTER, PRIASS_2ND_TORN_LETTER, PRIASS_3ND_TORN_LETTER, PRIASS_4ND_TORN_LETTER) >= 4)
		{
			qs.setCond(3, true);
		}
		else
		{
			playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			if (npc.getId() == MASTER_REORIA)
			{
				htmltext = "30328-01.htm";
			}
		}
		if (qs.isCompleted())
		{
			if (npc.getId() == MASTER_REORIA)
			{
				return htmltext;
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case MASTER_REORIA:
				{
					if (hasQuestItems(player, REISAS_LETTER))
					{
						htmltext = "30328-06.html";
					}
					else if ((qs.getInt("variable") == 1) && !hasAtLeastOneQuestItem(player, REISAS_LETTER, HONORARY_GUARD))
					{
						htmltext = "30328-08.html";
					}
					else if (hasQuestItems(player, HONORARY_GUARD))
					{
						takeItems(player, HONORARY_GUARD, -1);
						giveItems(player, REISAS_RECOMMENDATION, 1);
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
						htmltext = "30328-07.html";
					}
					break;
				}
				case GUARD_BABENCO:
				{
					if (qs.getInt("variable") == 1)
					{
						htmltext = "30334-01.html";
					}
					break;
				}
				case GUARD_MORETTI:
				{
					final long letterCount = getQuestItemsCount(player, PRIASS_1ND_TORN_LETTER, PRIASS_2ND_TORN_LETTER, PRIASS_3ND_TORN_LETTER, PRIASS_4ND_TORN_LETTER);
					if (hasQuestItems(player, REISAS_LETTER) && (letterCount == 0))
					{
						htmltext = "30337-01.html";
					}
					else if ((qs.getInt("variable") == 1) && !hasAtLeastOneQuestItem(player, MORETTIS_LETTER, PRIASS_LETTER, HONORARY_GUARD))
					{
						if (letterCount == 0)
						{
							htmltext = "30337-04.html";
						}
						else if (letterCount < 4)
						{
							htmltext = "30337-05.html";
						}
						else
						{
							takeItems(player, -1, PRIASS_1ND_TORN_LETTER, PRIASS_2ND_TORN_LETTER, PRIASS_3ND_TORN_LETTER, PRIASS_4ND_TORN_LETTER);
							giveItems(player, MORETTIES_HERB, 1);
							giveItems(player, MORETTIS_LETTER, 1);
							qs.setCond(4, true);
							htmltext = "30337-06.html";
						}
					}
					else if (hasQuestItems(player, PRIASS_LETTER))
					{
						takeItems(player, PRIASS_LETTER, -1);
						giveItems(player, HONORARY_GUARD, 1);
						qs.setCond(8, true);
						htmltext = "30337-07.html";
					}
					else if (hasQuestItems(player, MORETTIES_HERB, MORETTIS_LETTER))
					{
						htmltext = "30337-09.html";
					}
					else if (hasQuestItems(player, HONORARY_GUARD))
					{
						htmltext = "30337-08.html";
					}
					break;
				}
				case PRIAS:
				{
					if (hasQuestItems(player, MORETTIS_LETTER, MORETTIES_HERB))
					{
						if (!hasQuestItems(player, RUSTED_KEY))
						{
							qs.setCond(5, true);
							htmltext = "30426-01.html";
						}
						else
						{
							takeItems(player, -1, RUSTED_KEY, MORETTIES_HERB, MORETTIS_LETTER);
							giveItems(player, PRIASS_LETTER, 1);
							qs.setCond(7, true);
							htmltext = "30426-02.html";
						}
					}
					else if (hasQuestItems(player, PRIASS_LETTER))
					{
						htmltext = "30426-04.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
}