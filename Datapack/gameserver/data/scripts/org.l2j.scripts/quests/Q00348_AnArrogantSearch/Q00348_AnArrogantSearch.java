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
package quests.Q00348_AnArrogantSearch;

import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.serverpackets.RadarControl;

/**
 * @author SanyaDC
 */
public class Q00348_AnArrogantSearch extends Quest
{
	// NPCs
	public final int HANELLIN = 30864;
	public final int CLAUDIA_ATHEBALT = 31001;
	public final int TABLE_OF_VISION = 31646;
	// Monsters
	public final int CRIMSON_DRAKE = 20670;
	public final int KADIOS = 20671;
	public final int PLATINUM_TRIBE_SHAMAN = 20828;
	public final int PLATINUM_TRIBE_PREFECT = 20829;
	public final int GUARDIAN_ANGEL = 20830;
	public final int SEAL_ANGEL = 20831;
	public final int STONE_WATCHMAN_EZEKIEL = 27296;
	// Items
	public final int SHELL_OF_MONSTERS = 14857;
	public final int BOOK_OF_SAINT = 4397;
	public final int HEALING_POTION = 1061;
	public final int WHITE_CLOTH_PLATINUM = 4294;
	public final int WHITE_CLOTH_ANGLE = 4400;
	private static final int BLOODED_FABRIC = 4295;
	
	public Q00348_AnArrogantSearch()
	{
		super(348);
		addStartNpc(HANELLIN);
		addTalkId(HANELLIN, CLAUDIA_ATHEBALT, TABLE_OF_VISION);
		addKillId(CRIMSON_DRAKE, KADIOS, PLATINUM_TRIBE_SHAMAN, PLATINUM_TRIBE_PREFECT, GUARDIAN_ANGEL, SEAL_ANGEL, STONE_WATCHMAN_EZEKIEL);
		registerQuestItems(SHELL_OF_MONSTERS, BOOK_OF_SAINT, HEALING_POTION, WHITE_CLOTH_PLATINUM, WHITE_CLOTH_ANGLE);
		addCondMinLevel(60, "lvl.htm");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		String htmltext = event;
		switch (event)
		{
			case "30864.htm":
			case "30864-01.htm":
			case "30864-02.htm":
			{
				htmltext = event;
				break;
			}
			case "30864-03.htm":
			{
				if (player.getLevel() >= 60)
				{
					qs.startQuest();
					qs.setCond(2);
				}
				break;
			}
			case "30864-04.htm":
			{
				if (qs.isCond(3))
				{
					qs.setCond(4);
					takeItems(player, SHELL_OF_MONSTERS, -1);
				}
				break;
			}
			case "30864-05.htm":
			{
				if (qs.isCond(4))
				{
					qs.setCond(5);
				}
				break;
			}
			case "31001-01.htm":
			{
				if (qs.isCond(5))
				{
					addRadar(player, 120112, 30912, -3616);
				}
				break;
			}
			case "31646-01.htm":
			{
				if (qs.isCond(5))
				{
					addSpawn(STONE_WATCHMAN_EZEKIEL, npc, true, 0, true);
					qs.getPlayer().sendPacket(new RadarControl(2, 2, 0, 0, 0));
				}
				break;
			}
			case "30864-06.htm":
			{
				if (qs.isCond(6))
				{
					qs.setCond(7);
				}
				break;
			}
			case "30864-07.htm":
			{
				if (qs.isCond(7))
				{
					takeItems(player, HEALING_POTION, 1);
					qs.setCond(8);
				}
				break;
			}
			case "30864-08.htm":
			{
				if (qs.isCond(7))
				{
					takeItems(player, HEALING_POTION, 1);
					qs.setCond(9);
				}
				break;
			}
			case "end.htm":
			{
				if ((qs.getCond() == 10) || (qs.getCond() == 11))
				{
					takeItems(player, WHITE_CLOTH_PLATINUM, -1);
					takeItems(player, WHITE_CLOTH_ANGLE, -1);
					rewardItems(player, BLOODED_FABRIC, 1);
					qs.exitQuest(true, true);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, Player talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == HANELLIN)
				{
					htmltext = "30864.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case HANELLIN:
					{
						switch (qs.getCond())
						{
							case 2:
							{
								htmltext = "30864-09.htm";
								break;
							}
							case 3:
							{
								htmltext = "30864-10.htm";
								break;
							}
							case 4:
							{
								htmltext = "30864-04.htm";
								break;
							}
							case 5:
							{
								htmltext = "30864-05.htm";
								break;
							}
							case 6:
							{
								htmltext = "30864-11.htm";
								break;
							}
							case 7:
							{
								if (getQuestItemsCount(talker, HEALING_POTION) > 0)
								{
									htmltext = "30864-12.htm";
								}
								else
								{
									htmltext = "noz.htm";
								}
								break;
							}
							case 9:
							{
								htmltext = "30864-07.htm";
								break;
							}
							case 10:
							{
								htmltext = "30864-13.htm";
								break;
							}
							case 11:
							{
								htmltext = "30864-13.htm";
								break;
							}
						}
						break;
					}
					case CLAUDIA_ATHEBALT:
					{
						if (qs.isCond(5))
						{
							htmltext = "31001.htm";
						}
						break;
					}
					case TABLE_OF_VISION:
					{
						if (qs.isCond(5))
						{
							htmltext = "31646.htm";
						}
						break;
					}
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if (qs != null)
		{
			switch (npc.getId())
			{
				case CRIMSON_DRAKE:
				case KADIOS:
				{
					if (qs.isCond(2) && getRandomBoolean())
					{
						giveItems(killer, SHELL_OF_MONSTERS, 1, true);
						qs.setCond(3);
					}
					break;
				}
				case PLATINUM_TRIBE_SHAMAN:
				case PLATINUM_TRIBE_PREFECT:
				{
					if (qs.isCond(8))
					{
						if (giveItemRandomly(killer, npc, WHITE_CLOTH_PLATINUM, 1, 100, 0.5, true))
						{
							qs.setCond(10);
						}
					}
					break;
				}
				case GUARDIAN_ANGEL:
				case SEAL_ANGEL:
				{
					if (qs.isCond(9))
					{
						if (giveItemRandomly(killer, npc, WHITE_CLOTH_ANGLE, 1, 1000, 0.5, true))
						{
							qs.setCond(11);
						}
					}
					break;
				}
				case STONE_WATCHMAN_EZEKIEL:
				{
					if (qs.isCond(5))
					{
						giveItems(killer, BOOK_OF_SAINT, 1);
						qs.setCond(6);
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}