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
package quests.Q00409_PathOfTheElvenOracle;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.NpcSay;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.GameUtils;

/**
 * Path of the Elven Oracle (409)
 * @author ivantotov
 */
public final class Q00409_PathOfTheElvenOracle extends Quest
{
	// NPCs
	private static final int PRIEST_MANUEL = 30293;
	private static final int ALLANA = 30424;
	private static final int PERRIN = 30428;
	// Items
	private static final int CRYSTAL_MEDALLION = 1231;
	private static final int SWINDLERS_MONEY = 1232;
	private static final int ALLANA_OF_DAIRY = 1233;
	private static final int LIZARD_CAPTAIN_ORDER = 1234;
	private static final int HALF_OF_DAIRY = 1236;
	private static final int TAMIL_NECKLACE = 1275;
	// Reward
	private static final int LEAF_OF_ORACLE = 1235;
	// Misc
	private static final int MIN_LEVEL = 19;
	// Quest Monster
	private static final int lIZARDMAN_WARRIOR = 27032;
	private static final int LIZARDMAN_SCOUT = 27033;
	private static final int LIZARDMAN_SOLDIER = 27034;
	private static final int TAMIL = 27035;
	
	public Q00409_PathOfTheElvenOracle()
	{
		super(409);
		addStartNpc(PRIEST_MANUEL);
		addTalkId(PRIEST_MANUEL, ALLANA, PERRIN);
		addKillId(TAMIL, lIZARDMAN_WARRIOR, LIZARDMAN_SCOUT, LIZARDMAN_SOLDIER);
		addAttackId(TAMIL, lIZARDMAN_WARRIOR, LIZARDMAN_SCOUT, LIZARDMAN_SOLDIER);
		registerQuestItems(CRYSTAL_MEDALLION, SWINDLERS_MONEY, ALLANA_OF_DAIRY, LIZARD_CAPTAIN_ORDER, HALF_OF_DAIRY, TAMIL_NECKLACE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
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
				if (player.getClassId() == ClassId.ELVEN_MAGE)
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (hasQuestItems(player, LEAF_OF_ORACLE))
						{
							htmltext = "30293-04.htm";
						}
						else
						{
							qs.startQuest();
							qs.setMemoState(1);
							giveItems(player, CRYSTAL_MEDALLION, 1);
							htmltext = "30293-05.htm";
						}
					}
					else
					{
						htmltext = "30293-03.htm";
					}
				}
				else if (player.getClassId() == ClassId.ORACLE)
				{
					htmltext = "30293-02a.htm";
				}
				else
				{
					htmltext = "30293-02.htm";
				}
				break;
			}
			case "30424-08.html":
			case "30424-09.html":
			{
				htmltext = event;
				break;
			}
			case "30424-07.html":
			{
				if (qs.isMemoState(1))
				{
					htmltext = event;
				}
				break;
			}
			case "replay_1":
			{
				qs.setMemoState(2);
				addAttackPlayerDesire(addSpawn(lIZARDMAN_WARRIOR, npc, true, 0, false), player);
				addAttackPlayerDesire(addSpawn(LIZARDMAN_SCOUT, npc, true, 0, false), player);
				addAttackPlayerDesire(addSpawn(LIZARDMAN_SOLDIER, npc, true, 0, false), player);
				break;
			}
			case "30428-02.html":
			case "30428-03.html":
			{
				if (qs.isMemoState(2))
				{
					htmltext = event;
				}
				break;
			}
			case "replay_2":
			{
				if (qs.isMemoState(2))
				{
					qs.setMemoState(3);
					addAttackPlayerDesire(addSpawn(TAMIL, npc, true, 0, true), player);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (getQuestState(attacker, false) != null)
		{
			switch (npc.getScriptValue())
			{
				case 0:
				{
					switch (npc.getId())
					{
						case lIZARDMAN_WARRIOR:
						{
							npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.THE_SACRED_FLAME_IS_OURS));
							break;
						}
						case LIZARDMAN_SCOUT:
						{
							npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.THE_SACRED_FLAME_IS_OURS));
							break;
						}
						case LIZARDMAN_SOLDIER:
						{
							npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.THE_SACRED_FLAME_IS_OURS));
							break;
						}
						case TAMIL:
						{
							npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.AS_YOU_WISH_MASTER));
							break;
						}
					}
					
					npc.setScriptValue(1);
					npc.getVariables().set("firstAttacker", attacker.getObjectId());
					break;
				}
				case 1:
				{
					if (npc.getVariables().getInt("firstAttacker") != attacker.getObjectId())
					{
						npc.setScriptValue(2);
					}
					break;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && npc.isScriptValue(1) && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			switch (npc.getId())
			{
				case lIZARDMAN_WARRIOR:
				{
					if (!hasQuestItems(killer, LIZARD_CAPTAIN_ORDER))
					{
						npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.ARRGHH_WE_SHALL_NEVER_SURRENDER));
						giveItems(killer, LIZARD_CAPTAIN_ORDER, 1);
						qs.setCond(3, true);
					}
					break;
				}
				case LIZARDMAN_SCOUT:
				case LIZARDMAN_SOLDIER:
				{
					if (!hasQuestItems(killer, LIZARD_CAPTAIN_ORDER))
					{
						giveItems(killer, LIZARD_CAPTAIN_ORDER, 1);
						qs.setCond(3, true);
					}
					break;
				}
				case TAMIL:
				{
					if (!hasQuestItems(killer, TAMIL_NECKLACE))
					{
						giveItems(killer, TAMIL_NECKLACE, 1);
						qs.setCond(5, true);
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			if (npc.getId() == PRIEST_MANUEL)
			{
				if (!hasQuestItems(player, LEAF_OF_ORACLE))
				{
					htmltext = "30293-01.htm";
				}
				else
				{
					htmltext = "30293-04.htm";
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == PRIEST_MANUEL)
			{
				return htmltext;
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case PRIEST_MANUEL:
				{
					if (hasQuestItems(player, CRYSTAL_MEDALLION))
					{
						if (!hasAtLeastOneQuestItem(player, SWINDLERS_MONEY, ALLANA_OF_DAIRY, LIZARD_CAPTAIN_ORDER, HALF_OF_DAIRY))
						{
							if (qs.isMemoState(2))
							{
								qs.setMemoState(1);
								qs.setCond(8);
								htmltext = "30293-09.html";
							}
							else
							{
								qs.setMemoState(1);
								htmltext = "30293-06.html";
							}
						}
						else if (hasQuestItems(player, SWINDLERS_MONEY, ALLANA_OF_DAIRY, LIZARD_CAPTAIN_ORDER))
						{
							if (!hasQuestItems(player, HALF_OF_DAIRY))
							{
								giveItems(player, LEAF_OF_ORACLE, 1);
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
								htmltext = "30293-08.html";
							}
						}
						else
						{
							htmltext = "30293-07.html";
						}
					}
					break;
				}
				case ALLANA:
				{
					if (hasQuestItems(player, CRYSTAL_MEDALLION))
					{
						if (!hasAtLeastOneQuestItem(player, SWINDLERS_MONEY, ALLANA_OF_DAIRY, LIZARD_CAPTAIN_ORDER, HALF_OF_DAIRY))
						{
							if (qs.isMemoState(2))
							{
								htmltext = "30424-05.html";
							}
							else if (qs.isMemoState(1))
							{
								qs.setCond(2, true);
								htmltext = "30424-01.html";
							}
						}
						else if (!hasAtLeastOneQuestItem(player, SWINDLERS_MONEY, ALLANA_OF_DAIRY, HALF_OF_DAIRY) && hasQuestItems(player, LIZARD_CAPTAIN_ORDER))
						{
							qs.setMemoState(2);
							giveItems(player, HALF_OF_DAIRY, 1);
							qs.setCond(4, true);
							htmltext = "30424-02.html";
						}
						else if (!hasAtLeastOneQuestItem(player, SWINDLERS_MONEY, ALLANA_OF_DAIRY) && hasQuestItems(player, LIZARD_CAPTAIN_ORDER, HALF_OF_DAIRY))
						{
							if ((qs.isMemoState(3)) && !hasQuestItems(player, TAMIL_NECKLACE))
							{
								qs.setMemoState(2);
								qs.setCond(4, true);
								htmltext = "30424-06.html";
							}
							else
							{
								htmltext = "30424-03.html";
							}
						}
						else if (hasQuestItems(player, SWINDLERS_MONEY, LIZARD_CAPTAIN_ORDER, HALF_OF_DAIRY) && !hasQuestItems(player, ALLANA_OF_DAIRY))
						{
							giveItems(player, ALLANA_OF_DAIRY, 1);
							takeItems(player, HALF_OF_DAIRY, 1);
							qs.setCond(9, true);
							htmltext = "30424-04.html";
						}
						else if (hasQuestItems(player, SWINDLERS_MONEY, LIZARD_CAPTAIN_ORDER, ALLANA_OF_DAIRY))
						{
							qs.setCond(7, true);
							htmltext = "30424-05.html";
						}
					}
					break;
				}
				case PERRIN:
				{
					if (hasQuestItems(player, CRYSTAL_MEDALLION, LIZARD_CAPTAIN_ORDER, HALF_OF_DAIRY))
					{
						if (hasQuestItems(player, TAMIL_NECKLACE))
						{
							giveItems(player, SWINDLERS_MONEY, 1);
							takeItems(player, TAMIL_NECKLACE, 1);
							qs.setCond(6, true);
							htmltext = "30428-04.html";
						}
						else if (hasQuestItems(player, SWINDLERS_MONEY))
						{
							htmltext = "30428-05.html";
						}
						else if (qs.isMemoState(3))
						{
							htmltext = "30428-06.html";
						}
						else
						{
							htmltext = "30428-01.html";
						}
					}
					break;
				}
			}
		}
		return htmltext;
	}
}