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
package quests.Q00095_SagaOfTheHellKnight;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.util.GameUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author QuangNguyen
 */
public class Q00095_SagaOfTheHellKnight extends Quest
{
	// NPCs
	public final int MORDRED = 31582;
	public final int ROO_ROO = 34271;
	public final int LANCER = 30477;
	public final int LANCER1 = 34271;
	public final int TABLET_OF_VISION_1 = 31646;
	public final int TABLET_OF_VISION_2 = 31648;
	public final int TABLET_OF_VISION_3 = 31653;
	public final int TABLET_OF_VISION_4 = 31654;
	public final int WALDSTEIN = 31599;
	// Monsters
	public final int ICE_MONSTER = 27316;
	public final int SPIRIT_OF_A_DROWNED = 27317;
	public final int SOUL_OF_COLD = 27318;
	public final int GHOST_OF_SOLITUDE = 27319;
	public final int FIEND_OF_COLD = 27320;
	public final int SPIRIT_OF_COLD = 27321;
	public final int SPAMPLAND_WATCHMAN = 21650;
	public final int FLAME_DRAKE = 21651;
	public final int FIERY_IFRIT = 21652;
	public final int IKEDIT = 21653;
	public final int KEEPER_OF_THE_HOLY_EDICT = 27215;
	public final int ARHANGEL_ICONOCLASSIS = 27257;
	public final int HALISHA_ARCHON = 27219;
	public final int DEATH_LORD_HALLATE = 27262;
	// Items
	public final int ICE_CRYSTAL_FRAGMENT = 49829;
	public final int HALISHA_BADGE = 7510;
	public final int RESONANCE_AMULET = 7293;
	public final int RESONANCE_AMULET_2 = 7324;
	public final int RESONANCE_AMULET_3 = 7355;
	public final int RESONANCE_AMULET_4 = 7386;
	public final int INVESTIGATIVE_REPORT = 7532;
	// Reward
	public final int BOOK_GOLD_LION = 90038;
	// Misc
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q00095_SagaOfTheHellKnight()
	{
		super(95);
		addStartNpc(MORDRED);
		addTalkId(MORDRED, ROO_ROO, LANCER, LANCER1, WALDSTEIN, TABLET_OF_VISION_1, TABLET_OF_VISION_2, TABLET_OF_VISION_3, TABLET_OF_VISION_4);
		addKillId(ICE_MONSTER, SPIRIT_OF_A_DROWNED, SOUL_OF_COLD, GHOST_OF_SOLITUDE, FIEND_OF_COLD, SPIRIT_OF_COLD, SPAMPLAND_WATCHMAN, FLAME_DRAKE, FIERY_IFRIT, IKEDIT, KEEPER_OF_THE_HOLY_EDICT, ARHANGEL_ICONOCLASSIS, HALISHA_ARCHON, DEATH_LORD_HALLATE);
		registerQuestItems(INVESTIGATIVE_REPORT, ICE_CRYSTAL_FRAGMENT, HALISHA_BADGE);
		addCondMinLevel(76, "mordred_q95_02.htm");
		addCondClassId(ClassId.DARK_AVENGER, "mordred_q95_03.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		String htmltext = event;
		switch (event)
		{
			case "mordred_q95_02a.htm":
			{
				htmltext = "mordred_q95_5.htm";
				break;
			}
			case "mordred_q95_001.htm":
			{
				if (qs.isCond(0))
				{
					qs.startQuest();
					qs.setCond(1);
				}
				break;
			}
			case "ruru2.htm":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2);
				}
				break;
			}
			case "ruru4.htm":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3);
				}
				break;
			}
			case "ruru6.htm":
			{
				if (qs.isCond(4))
				{
					qs.setCond(5);
					takeItems(player, ICE_CRYSTAL_FRAGMENT, -1);
					giveItems(player, INVESTIGATIVE_REPORT, 1);
				}
				break;
			}
			case "lancer6.htm":
			{
				if (qs.isCond(5))
				{
					qs.setCond(6);
					giveItems(player, RESONANCE_AMULET, 1);
					takeItems(player, INVESTIGATIVE_REPORT, -1);
				}
				break;
			}
			case "stone12.htm":
			{
				if (qs.isCond(6))
				{
					qs.setCond(7);
				}
				break;
			}
			case "stone22.htm":
			{
				if (qs.isCond(8))
				{
					addSpawn(ARHANGEL_ICONOCLASSIS, npc, true, 0, true);
					qs.setCond(9);
				}
				break;
			}
			case "stone25.htm":
			{
				if (qs.isCond(10))
				{
					qs.setCond(11);
				}
				break;
			}
			case "lancer12.htm":
			{
				if (qs.isCond(11))
				{
					qs.setCond(12);
				}
				break;
			}
			case "stone32.htm":
			{
				if (qs.isCond(14))
				{
					qs.setCond(15);
				}
				break;
			}
			case "valdwtein2.htm":
			{
				if (qs.isCond(16))
				{
					qs.setCond(17);
					giveItems(player, RESONANCE_AMULET_4, 1);
				}
				break;
			}
			case "stone42.htm":
			{
				if (qs.isCond(17))
				{
					qs.setCond(18);
				}
				break;
			}
			case "mordred_q95_22.htm":
			{
				if (qs.isCond(18))
				{
					if ((player.getLevel() < 76) || (player.getBaseClass() != 6))
					{
						htmltext = "30849-nolvl.htm";
					}
					else
					{
						addExpAndSp(player, 3100000, 103000);
						rewardItems(player, BOOK_GOLD_LION, 1);
						takeItems(player, RESONANCE_AMULET, -1);
						takeItems(player, RESONANCE_AMULET_2, -1);
						takeItems(player, RESONANCE_AMULET_3, -1);
						takeItems(player, RESONANCE_AMULET_4, -1);
						takeItems(player, HALISHA_BADGE, -1);
						qs.exitQuest(false, true);
						player.setClassId(91);
						player.setBaseClass(91);
						player.broadcastUserInfo();
						npc.broadcastPacket(new MagicSkillUse(npc, player, 5103, 1, 1000, 0));
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == MORDRED)
				{
					htmltext = "mordred_q95_01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case MORDRED:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "mordred_q95_001.htm";
								break;
							}
							case 18:
							{
								htmltext = "mordred_q95_011.htm";
								break;
							}
							case 19:
							{
								htmltext = "mordred_q95_012.htm";
								break;
							}
						}
						break;
					}
					case LANCER:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "ruru.htm";
								break;
							}
							case 2:
							{
								htmltext = "ruru2.htm";
								break;
							}
							case 5:
							{
								htmltext = "lancer5.htm";
								break;
							}
							case 6:
							{
								htmltext = "lancer6.htm";
								break;
							}
							case 11:
							{
								htmltext = "lancer11.htm";
								break;
							}
							case 12:
							{
								htmltext = "lancer12.htm";
								break;
							}
						}
						break;
					}
					case ROO_ROO:
					{
						switch (qs.getCond())
						{
							case 2:
							{
								htmltext = "ruru3.htm";
								break;
							}
							case 3:
							{
								htmltext = "ruru4.htm";
								break;
							}
							case 4:
							{
								htmltext = "ruru5.htm";
								break;
							}
							case 5:
							{
								htmltext = "ruru6.htm";
								break;
							}
						}
						break;
					}
					case TABLET_OF_VISION_1:
					{
						if (qs.isCond(6))
						{
							htmltext = "stone11.htm";
						}
						else if (qs.isCond(7))
						{
							htmltext = "stone12.htm";
						}
						break;
					}
					case TABLET_OF_VISION_2:
					{
						switch (qs.getCond())
						{
							case 8:
							{
								htmltext = "stone21.htm";
								break;
							}
							case 9:
							{
								htmltext = "stone23.htm";
								break;
							}
							case 10:
							{
								htmltext = "stone24.htm";
								break;
							}
						}
						break;
					}
					case TABLET_OF_VISION_3:
					{
						if (qs.isCond(14))
						{
							htmltext = "stone31.htm";
						}
						else if (qs.isCond(15))
						{
							htmltext = "stone33.htm";
						}
						break;
					}
					case TABLET_OF_VISION_4:
					{
						switch (qs.getCond())
						{
							case 15:
							{
								htmltext = "stone40.htm";
								break;
							}
							case 17:
							{
								htmltext = "stone41.htm";
								break;
							}
							case 18:
							{
								htmltext = "stone43.htm";
								break;
							}
						}
						break;
					}
					case WALDSTEIN:
					{
						if (qs.isCond(16))
						{
							htmltext = "valdwtein1.htm";
						}
						else if (qs.isCond(17))
						{
							htmltext = "valdwtein2.htm";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(talker);
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
				case ICE_MONSTER:
				case SPIRIT_OF_A_DROWNED:
				case SOUL_OF_COLD:
				case FIEND_OF_COLD:
				case GHOST_OF_SOLITUDE:
				case SPIRIT_OF_COLD:
				{
					if (qs.isCond(3))
					{
						if (giveItemRandomly(killer, npc, ICE_CRYSTAL_FRAGMENT, 1, 50, 0.5, true))
						{
							qs.setCond(4);
						}
					}
					break;
				}
				case ARHANGEL_ICONOCLASSIS:
				{
					if (qs.isCond(9))
					{
						qs.setCond(10);
					}
					break;
				}
				case SPAMPLAND_WATCHMAN:
				case FLAME_DRAKE:
				case FIERY_IFRIT:
				case IKEDIT:
				{
					if (qs.isCond(12))
					{
						if (giveItemRandomly(killer, npc, HALISHA_BADGE, 1, 700, 0.5, true))
						{
							addSpawn(HALISHA_ARCHON, npc, true, 0, true);
							qs.setCond(13);
						}
					}
					break;
				}
				case HALISHA_ARCHON:
				{
					if (qs.isCond(13))
					{
						giveItems(killer, RESONANCE_AMULET_3, 1, true);
						qs.setCond(14);
					}
					break;
				}
				case DEATH_LORD_HALLATE:
				{
					if (qs.isCond(15))
					{
						qs.setCond(16);
					}
					break;
				}
				case KEEPER_OF_THE_HOLY_EDICT:
				{
					if (qs.isCond(7))
					{
						final int count = qs.getInt(KILL_COUNT_VAR);
						if (count < 20)
						{
							qs.set(KILL_COUNT_VAR, count + 1);
						}
						if (count >= 20)
						{
							qs.setCond(8, true);
							giveItems(killer, RESONANCE_AMULET_2, 1);
						}
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(7))
		{
			final Set<NpcLogListHolder> holder = new HashSet<>(1);
			
			// guardian of forbidden knowledge
			final int guardiancount = qs.getInt(KILL_COUNT_VAR);
			if (guardiancount > 0)
			{
				holder.add(new NpcLogListHolder(KEEPER_OF_THE_HOLY_EDICT, false, guardiancount));
			}
			return holder;
		}
		return super.getNpcLogList(player);
	}
}