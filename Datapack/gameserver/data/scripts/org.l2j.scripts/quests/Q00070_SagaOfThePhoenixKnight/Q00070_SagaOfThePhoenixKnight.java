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
package quests.Q00070_SagaOfThePhoenixKnight;

import java.util.HashSet;
import java.util.Set;

import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.util.Util;

/**
 * @author QuangNguyen
 */
public class Q00070_SagaOfThePhoenixKnight extends Quest
{
	// NPCs
	public final int SEDRICK = 30849;
	public final int FELIX = 31277;
	public final int RIFKEN = 34268;
	public final int ERIC_RAMSHEART = 31631;
	public final int TABLET_OF_VISION_1 = 31646;
	public final int TABLET_OF_VISION_2 = 31647;
	public final int TABLET_OF_VISION_3 = 31651;
	public final int TABLET_OF_VISION_4 = 31654;
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
	public final int GUARDIAN_OF_FOBIDDEN_KNOWLEDGE = 27214; // check drop item
	public final int FALLEN_KNIGHT_ADHIL = 27286;
	public final int HALISHA_ARCHON = 27219; // blazing spawn
	public final int BELETH_SHADOW = 27278; // blazing spawn
	// Items
	public final int ICE_CRYSTAL_FRAGMENT = 49804;
	public final int HALISHA_BADGE = 7485;
	public final int RESONANCE_AMULET = 7268;
	public final int RESONANCE_AMULET_2 = 7299;
	public final int RESONANCE_AMULET_3 = 7330;
	public final int RESONANCE_AMULET_4 = 7361;
	public final int PURE_ICE = 7534;
	// Reward
	public final int BOOK_GOLD_LION = 90038;
	// Misc
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q00070_SagaOfThePhoenixKnight()
	{
		super(70);
		addStartNpc(SEDRICK);
		addTalkId(SEDRICK, FELIX, RIFKEN, ERIC_RAMSHEART, TABLET_OF_VISION_1, TABLET_OF_VISION_2, TABLET_OF_VISION_3, TABLET_OF_VISION_4);
		addKillId(ICE_MONSTER, SPIRIT_OF_A_DROWNED, SOUL_OF_COLD, GHOST_OF_SOLITUDE, FIEND_OF_COLD, SPIRIT_OF_COLD, SPAMPLAND_WATCHMAN, FLAME_DRAKE, FIERY_IFRIT, IKEDIT, GUARDIAN_OF_FOBIDDEN_KNOWLEDGE, FALLEN_KNIGHT_ADHIL, HALISHA_ARCHON, BELETH_SHADOW);
		registerQuestItems(PURE_ICE, ICE_CRYSTAL_FRAGMENT, HALISHA_BADGE);
		addCondMinLevel(76, "30849-nolvl.htm");
		addCondClassId(ClassId.PALADIN, "30849-checkclass.htm");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		String htmltext = event;
		switch (event)
		{
			case "30849-02a.htm":
			{
				htmltext = "30849-03.htm";
				break;
			}
			case "30849-01a.htm":
			{
				if (qs.isCond(0))
				{
					qs.startQuest();
					qs.setCond(1);
				}
				break;
			}
			case "31277-01.htm":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2);
				}
				break;
			}
			case "34268-01.htm":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3);
				}
				break;
			}
			case "34268-03.htm":
			{
				if (qs.isCond(4))
				{
					qs.setCond(5);
					takeItems(player, ICE_CRYSTAL_FRAGMENT, -1);
					giveItems(player, PURE_ICE, 1);
				}
				break;
			}
			case "31277-03.htm":
			{
				if (qs.isCond(5))
				{
					qs.setCond(6);
					giveItems(player, RESONANCE_AMULET, 1);
					takeItems(player, PURE_ICE, -1);
				}
				break;
			}
			case "31646-01.htm":
			{
				if (qs.isCond(6))
				{
					qs.setCond(7);
				}
				break;
			}
			case "31647-01.htm":
			{
				if (qs.isCond(8))
				{
					addSpawn(FALLEN_KNIGHT_ADHIL, npc, true, 0, true);
					qs.setCond(9);
				}
				break;
			}
			case "31647-04.htm":
			{
				if (qs.isCond(10))
				{
					qs.setCond(11);
				}
				break;
			}
			case "31277-05.htm":
			{
				if (qs.isCond(11))
				{
					qs.setCond(12);
				}
				break;
			}
			case "31651-01.htm":
			{
				if (qs.isCond(14))
				{
					qs.setCond(15);
				}
				break;
			}
			case "31654-01.htm":
			{
				if (qs.isCond(15))
				{
					qs.setCond(16);
					addSpawn(BELETH_SHADOW, npc, true, 0, true);
				}
				break;
			}
			case "31631-01.htm":
			{
				if (qs.isCond(16))
				{
					qs.setCond(17);
					giveItems(player, RESONANCE_AMULET_4, 1);
				}
				break;
			}
			case "31654-03.htm":
			{
				if (qs.isCond(17))
				{
					qs.setCond(18);
				}
				break;
			}
			case "30849-05.htm":
			{
				if (qs.isCond(18))
				{
					if ((player.getLevel() < 76) || (player.getBaseClass() != 5))
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
						player.setClassId(90);
						player.setBaseClass(90);
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
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == SEDRICK)
				{
					htmltext = "30849.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case SEDRICK:
					{
						if (qs.isCond(1))
						{
							htmltext = "30849-01a.htm";
						}
						else if (qs.isCond(18))
						{
							htmltext = "30849-04.htm";
						}
						break;
					}
					case FELIX:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "31277.htm";
								break;
							}
							case 2:
							{
								htmltext = "31277-01.htm";
								break;
							}
							case 5:
							{
								htmltext = "31277-02.htm";
								break;
							}
							case 6:
							{
								htmltext = "31277-03.htm";
								break;
							}
							case 11:
							{
								htmltext = "31277-04.htm";
								break;
							}
							case 12:
							{
								htmltext = "31277-05.htm";
								break;
							}
						}
						break;
					}
					case RIFKEN:
					{
						switch (qs.getCond())
						{
							case 2:
							{
								htmltext = "34268.htm";
								break;
							}
							case 3:
							{
								htmltext = "34268-01.htm";
								break;
							}
							case 4:
							{
								htmltext = "34268-02.htm";
								break;
							}
						}
						break;
					}
					case TABLET_OF_VISION_1:
					{
						if (qs.isCond(6))
						{
							htmltext = "31646.htm";
						}
						else if (qs.isCond(7))
						{
							htmltext = "31646-01.htm";
						}
						break;
					}
					case TABLET_OF_VISION_2:
					{
						switch (qs.getCond())
						{
							case 8:
							{
								htmltext = "31647.htm";
								break;
							}
							case 9:
							{
								htmltext = "31647-02.htm";
								break;
							}
							case 10:
							{
								htmltext = "31647-03.htm";
								break;
							}
							case 11:
							{
								htmltext = "31647-04.htm";
								break;
							}
						}
						break;
					}
					case TABLET_OF_VISION_3:
					{
						if (qs.isCond(14))
						{
							htmltext = "31651.htm";
						}
						else if (qs.isCond(15))
						{
							htmltext = "31651-01.htm";
						}
						break;
					}
					case TABLET_OF_VISION_4:
					{
						switch (qs.getCond())
						{
							case 15:
							{
								htmltext = "31654.htm";
								break;
							}
							case 17:
							{
								htmltext = "31654-02.htm";
								break;
							}
							case 18:
							{
								htmltext = "31654-03.htm";
								break;
							}
						}
						break;
					}
					case ERIC_RAMSHEART:
					{
						if (qs.isCond(16))
						{
							htmltext = "31631.htm";
						}
						else if (qs.isCond(17))
						{
							htmltext = "31631-01.htm";
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
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && Util.checkIfInRange(1500, npc, killer, true))
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
				case FALLEN_KNIGHT_ADHIL:
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
				case BELETH_SHADOW:
				{
					if (qs.isCond(16))
					{
						addSpawn(ERIC_RAMSHEART, npc, true, 20000, true);
					}
					break;
				}
				case GUARDIAN_OF_FOBIDDEN_KNOWLEDGE:
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
	public Set<NpcLogListHolder> getNpcLogList(L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(7))
		{
			final Set<NpcLogListHolder> holder = new HashSet<>(1);
			
			// guardian of forbidden knowledge
			final int guardiancount = qs.getInt(KILL_COUNT_VAR);
			if (guardiancount > 0)
			{
				holder.add(new NpcLogListHolder(GUARDIAN_OF_FOBIDDEN_KNOWLEDGE, false, guardiancount));
			}
			return holder;
		}
		return super.getNpcLogList(player);
	}
}