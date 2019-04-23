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
package quests.Q00216_TrialOfTheGuildsman;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.items.CommonItem;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.Util;

/**
 * Trial Of The Guildsman (216)
 * @author ivantotov
 */
public final class Q00216_TrialOfTheGuildsman extends Quest
{
	private static final int WAREHOUSE_KEEPER_VALKON = 30103;
	private static final int WAREHOUSE_KEEPER_NORMAN = 30210;
	private static final int BLACKSMITH_ALTRAN = 30283;
	private static final int BLACKSMITH_PINTER = 30298;
	private static final int BLACKSMITH_DUNING = 30688;
	// Items
	private static final int RECIPE_JOURNEYMAN_RING = 3024;
	private static final int RECIPE_AMBER_BEAD = 3025;
	private static final int VALKONS_RECOMMENDATION = 3120;
	private static final int MANDRAGORA_BERRY = 3121;
	private static final int ALLTRANS_INSTRUCTIONS = 3122;
	private static final int ALLTRANS_1ST_RECOMMENDATION = 3123;
	private static final int ALLTRANS_2ND_RECOMMENDATION = 3124;
	private static final int NORMANS_INSTRUCTIONS = 3125;
	private static final int NORMANS_RECEIPT = 3126;
	private static final int DUNINGS_INSTRUCTIONS = 3127;
	private static final int DUNINGS_KEY = 3128;
	private static final int NORMANS_LIST = 3129;
	private static final int GRAY_BONE_POWDER = 3130;
	private static final int GRANITE_WHETSTONE = 3131;
	private static final int RED_PIGMENT = 3132;
	private static final int BRAIDED_YARN = 3133;
	private static final int JOURNEYMAN_GEM = 3134;
	private static final int PINTERS_INSTRUCTIONS = 3135;
	private static final int AMBER_BEAD = 3136;
	private static final int AMBER_LUMP = 3137;
	private static final int JOURNEYMAN_DECO_BEADS = 3138;
	private static final int JOURNEYMAN_RING = 3139;
	// Reward
	private static final int MARK_OF_GUILDSMAN = 3119;
	// Monsters
	private static final int ANT = 20079;
	private static final int ANT_CAPTAIN = 20080;
	private static final int ANT_OVERSEER = 20081;
	private static final int GRANITE_GOLEM = 20083;
	private static final int MANDRAGORA_SPROUT1 = 20154;
	private static final int MANDRAGORA_SAPLONG = 20155;
	private static final int MANDRAGORA_BLOSSOM = 20156;
	private static final int SILENOS = 20168;
	private static final int STRAIN = 20200;
	private static final int GHOUL = 20201;
	private static final int DEAD_SEEKER = 20202;
	private static final int MANDRAGORA_SPROUT2 = 20223;
	private static final int BREKA_ORC = 20267;
	private static final int BREKA_ORC_ARCHER = 20268;
	private static final int BREKA_ORC_SHAMAN = 20269;
	private static final int BREKA_ORC_OVERLORD = 20270;
	private static final int BREKA_ORC_WARRIOR = 20271;
	// Misc
	private static final int MIN_LVL = 35;
	
	public Q00216_TrialOfTheGuildsman()
	{
		super(216);
		addStartNpc(WAREHOUSE_KEEPER_VALKON);
		addTalkId(WAREHOUSE_KEEPER_VALKON, WAREHOUSE_KEEPER_NORMAN, BLACKSMITH_ALTRAN, BLACKSMITH_PINTER, BLACKSMITH_DUNING);
		addKillId(ANT, ANT_CAPTAIN, ANT_OVERSEER, GRANITE_GOLEM, MANDRAGORA_SPROUT1, MANDRAGORA_SAPLONG, MANDRAGORA_BLOSSOM, SILENOS, STRAIN, GHOUL, DEAD_SEEKER, MANDRAGORA_SPROUT2, BREKA_ORC, BREKA_ORC_ARCHER, BREKA_ORC_SHAMAN, BREKA_ORC_OVERLORD, BREKA_ORC_WARRIOR);
		registerQuestItems(RECIPE_JOURNEYMAN_RING, RECIPE_AMBER_BEAD, VALKONS_RECOMMENDATION, MANDRAGORA_BERRY, ALLTRANS_INSTRUCTIONS, ALLTRANS_1ST_RECOMMENDATION, ALLTRANS_2ND_RECOMMENDATION, NORMANS_INSTRUCTIONS, NORMANS_RECEIPT, DUNINGS_INSTRUCTIONS, DUNINGS_KEY, NORMANS_LIST, GRAY_BONE_POWDER, GRANITE_WHETSTONE, RED_PIGMENT, BRAIDED_YARN, JOURNEYMAN_GEM, PINTERS_INSTRUCTIONS, AMBER_BEAD, AMBER_LUMP, JOURNEYMAN_DECO_BEADS, JOURNEYMAN_RING);
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
				if (getQuestItemsCount(player, CommonItem.ADENA) >= 2000)
				{
					qs.startQuest();
					takeItems(player, CommonItem.ADENA, 2000);
					if (!hasQuestItems(player, VALKONS_RECOMMENDATION))
					{
						giveItems(player, VALKONS_RECOMMENDATION, 1);
					}
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				}
				else
				{
					htmltext = "30103-05b.htm";
				}
				break;
			}
			case "30103-04.htm":
			case "30103-05.htm":
			case "30103-05a.html":
			case "30103-06a.html":
			case "30103-06b.html":
			case "30103-06c.html":
			case "30103-07a.html":
			case "30103-07b.html":
			case "30103-07c.html":
			case "30210-02.html":
			case "30210-03.html":
			case "30210-08.html":
			case "30210-09.html":
			case "30210-11a.html":
			case "30283-03a.html":
			case "30283-03b.html":
			case "30283-04.html":
			case "30298-03.html":
			case "30298-05a.html":
			{
				htmltext = event;
				break;
			}
			case "30103-09a.html":
			{
				if (hasQuestItems(player, ALLTRANS_INSTRUCTIONS) && (getQuestItemsCount(player, JOURNEYMAN_RING) >= 7))
				{
					giveAdena(player, 187606, true);
					giveItems(player, MARK_OF_GUILDSMAN, 1);
					addExpAndSp(player, 1029478, 66768);
					qs.exitQuest(false, true);
					player.sendPacket(new SocialAction(player.getObjectId(), 3));
					htmltext = event;
				}
				break;
			}
			case "30103-09b.html":
			{
				if (hasQuestItems(player, ALLTRANS_INSTRUCTIONS) && (getQuestItemsCount(player, JOURNEYMAN_RING) >= 7))
				{
					giveAdena(player, 93803, true);
					giveItems(player, MARK_OF_GUILDSMAN, 1);
					addExpAndSp(player, 514739, 33384);
					qs.exitQuest(false, true);
					player.sendPacket(new SocialAction(player.getObjectId(), 3));
					htmltext = event;
				}
				break;
			}
			case "30210-04.html":
			{
				if (hasQuestItems(player, ALLTRANS_1ST_RECOMMENDATION))
				{
					takeItems(player, ALLTRANS_1ST_RECOMMENDATION, 1);
					giveItems(player, NORMANS_INSTRUCTIONS, 1);
					giveItems(player, NORMANS_RECEIPT, 1);
					htmltext = event;
				}
				break;
			}
			case "30210-10.html":
			{
				if (hasQuestItems(player, NORMANS_INSTRUCTIONS))
				{
					takeItems(player, NORMANS_INSTRUCTIONS, 1);
					takeItems(player, DUNINGS_KEY, -1);
					giveItems(player, NORMANS_LIST, 1);
					htmltext = event;
				}
				break;
			}
			case "30283-03.html":
			{
				if (hasQuestItems(player, VALKONS_RECOMMENDATION, MANDRAGORA_BERRY))
				{
					giveItems(player, RECIPE_JOURNEYMAN_RING, 1);
					takeItems(player, VALKONS_RECOMMENDATION, 1);
					takeItems(player, MANDRAGORA_BERRY, 1);
					giveItems(player, ALLTRANS_INSTRUCTIONS, 1);
					giveItems(player, ALLTRANS_1ST_RECOMMENDATION, 1);
					giveItems(player, ALLTRANS_2ND_RECOMMENDATION, 1);
					qs.setCond(5, true);
					htmltext = event;
				}
				break;
			}
			case "30298-04.html":
			{
				if (player.getClassId() == ClassId.SCAVENGER)
				{
					if (hasQuestItems(player, ALLTRANS_2ND_RECOMMENDATION))
					{
						takeItems(player, ALLTRANS_2ND_RECOMMENDATION, 1);
						giveItems(player, PINTERS_INSTRUCTIONS, 1);
						htmltext = event;
					}
				}
				else if (hasQuestItems(player, ALLTRANS_2ND_RECOMMENDATION))
				{
					giveItems(player, RECIPE_AMBER_BEAD, 1);
					takeItems(player, ALLTRANS_2ND_RECOMMENDATION, 1);
					giveItems(player, PINTERS_INSTRUCTIONS, 1);
					htmltext = "30298-05.html";
				}
				break;
			}
			case "30688-02.html":
			{
				if (hasQuestItems(player, NORMANS_RECEIPT))
				{
					takeItems(player, NORMANS_RECEIPT, 1);
					giveItems(player, DUNINGS_INSTRUCTIONS, 1);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		switch (npc.getId())
		{
			case ANT:
			case ANT_CAPTAIN:
			case ANT_OVERSEER:
			{
				final QuestState qs = getRandomPartyMemberState(killer, -1, 2, npc);
				if (qs != null)
				{
					int count = 0;
					if ((qs.getPlayer().getClassId() == ClassId.SCAVENGER) && npc.isSweepActive())
					{
						count += 5;
					}
					
					if (getRandomBoolean() && (qs.getPlayer().getClassId() == ClassId.ARTISAN))
					{
						giveItems(qs.getPlayer(), AMBER_LUMP, 1);
						playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_MIDDLE);
					}
					
					if ((getQuestItemsCount(qs.getPlayer(), AMBER_BEAD) + count) < 70)
					{
						count += 5;
					}
					
					if (count > 0)
					{
						giveItemRandomly(qs.getPlayer(), npc, AMBER_BEAD, count, 70, 1.0, true);
					}
				}
				break;
			}
			case GRANITE_GOLEM:
			{
				final QuestState qs = getRandomPartyMemberState(killer, -1, 2, npc);
				if (qs != null)
				{
					giveItems(qs.getPlayer(), GRANITE_WHETSTONE, 7);
					if (getQuestItemsCount(qs.getPlayer(), GRANITE_WHETSTONE) == 70)
					{
						playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_MIDDLE);
					}
					else
					{
						playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
			case MANDRAGORA_SPROUT1:
			case MANDRAGORA_SAPLONG:
			case MANDRAGORA_BLOSSOM:
			case MANDRAGORA_SPROUT2:
			{
				final QuestState qs = getQuestState(killer, false);
				if ((qs != null) && qs.isStarted() && Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
				{
					if (hasQuestItems(killer, VALKONS_RECOMMENDATION) && !hasQuestItems(killer, MANDRAGORA_BERRY))
					{
						giveItems(killer, MANDRAGORA_BERRY, 1);
						qs.setCond(4, true);
					}
				}
				break;
			}
			case SILENOS:
			{
				final QuestState qs = getRandomPartyMemberState(killer, -1, 2, npc);
				if (qs != null)
				{
					giveItems(qs.getPlayer(), BRAIDED_YARN, 10);
					if (getQuestItemsCount(qs.getPlayer(), BRAIDED_YARN) == 70)
					{
						playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_MIDDLE);
					}
					else
					{
						playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
			case STRAIN:
			case GHOUL:
			{
				final QuestState qs = getRandomPartyMemberState(killer, -1, 2, npc);
				if (qs != null)
				{
					giveItems(qs.getPlayer(), GRAY_BONE_POWDER, 5);
					if (getQuestItemsCount(qs.getPlayer(), GRAY_BONE_POWDER) == 70)
					{
						playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_MIDDLE);
					}
					else
					{
						playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
			case DEAD_SEEKER:
			{
				final QuestState qs = getRandomPartyMemberState(killer, -1, 2, npc);
				if (qs != null)
				{
					giveItems(qs.getPlayer(), RED_PIGMENT, 7);
					if (getQuestItemsCount(qs.getPlayer(), RED_PIGMENT) == 70)
					{
						playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_MIDDLE);
					}
					else
					{
						playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
			case BREKA_ORC:
			case BREKA_ORC_ARCHER:
			case BREKA_ORC_SHAMAN:
			case BREKA_ORC_OVERLORD:
			case BREKA_ORC_WARRIOR:
			{
				final QuestState qs = getRandomPartyMemberState(killer, -1, 2, npc);
				if (qs != null)
				{
					if (getQuestItemsCount(qs.getPlayer(), DUNINGS_KEY) >= 29)
					{
						giveItems(qs.getPlayer(), DUNINGS_KEY, 1);
						takeItems(qs.getPlayer(), DUNINGS_INSTRUCTIONS, 1);
						playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_MIDDLE);
					}
					else
					{
						giveItems(qs.getPlayer(), DUNINGS_KEY, 1);
						playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
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
			if (npc.getId() == WAREHOUSE_KEEPER_VALKON)
			{
				if ((player.getClassId() == ClassId.ARTISAN) || (player.getClassId() == ClassId.SCAVENGER))
				{
					if (player.getLevel() < MIN_LVL)
					{
						htmltext = "30103-02.html";
					}
					else
					{
						htmltext = "30103-03.htm";
					}
				}
				else
				{
					htmltext = "30103-01.html";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case WAREHOUSE_KEEPER_VALKON:
				{
					if (hasQuestItems(player, VALKONS_RECOMMENDATION))
					{
						qs.setCond(3, true);
						htmltext = "30103-07.html";
					}
					else if (hasQuestItems(player, ALLTRANS_INSTRUCTIONS))
					{
						if (getQuestItemsCount(player, JOURNEYMAN_RING) < 7)
						{
							htmltext = "30103-08.html";
						}
						else
						{
							htmltext = "30103-09.html";
						}
					}
					break;
				}
				case WAREHOUSE_KEEPER_NORMAN:
				{
					if (hasQuestItems(player, ALLTRANS_INSTRUCTIONS))
					{
						if (hasQuestItems(player, ALLTRANS_1ST_RECOMMENDATION))
						{
							htmltext = "30210-01.html";
						}
						else if (hasQuestItems(player, NORMANS_INSTRUCTIONS, NORMANS_RECEIPT))
						{
							htmltext = "30210-05.html";
						}
						else if (hasQuestItems(player, NORMANS_INSTRUCTIONS, DUNINGS_INSTRUCTIONS))
						{
							htmltext = "30210-06.html";
						}
						else if (hasQuestItems(player, NORMANS_INSTRUCTIONS) && (getQuestItemsCount(player, DUNINGS_KEY) >= 30))
						{
							htmltext = "30210-07.html";
						}
						else if (hasQuestItems(player, NORMANS_LIST))
						{
							if ((getQuestItemsCount(player, GRAY_BONE_POWDER) >= 70) && (getQuestItemsCount(player, GRANITE_WHETSTONE) >= 70) && (getQuestItemsCount(player, RED_PIGMENT) >= 70) && (getQuestItemsCount(player, BRAIDED_YARN) >= 70))
							{
								takeItems(player, NORMANS_LIST, 1);
								takeItems(player, GRAY_BONE_POWDER, -1);
								takeItems(player, GRANITE_WHETSTONE, -1);
								takeItems(player, RED_PIGMENT, -1);
								takeItems(player, BRAIDED_YARN, -1);
								giveItems(player, JOURNEYMAN_GEM, 7);
								if (getQuestItemsCount(player, JOURNEYMAN_DECO_BEADS) >= 7)
								{
									qs.setCond(6, true);
								}
								htmltext = "30210-12.html";
							}
							else
							{
								htmltext = "30210-11.html";
							}
						}
						else if (!hasAtLeastOneQuestItem(player, NORMANS_INSTRUCTIONS, NORMANS_LIST) && hasAtLeastOneQuestItem(player, JOURNEYMAN_GEM, JOURNEYMAN_RING))
						{
							htmltext = "30210-13.html";
						}
					}
					break;
				}
				case BLACKSMITH_ALTRAN:
				{
					if (hasQuestItems(player, VALKONS_RECOMMENDATION))
					{
						if (!hasQuestItems(player, MANDRAGORA_BERRY))
						{
							qs.setCond(2, true);
							htmltext = "30283-01.html";
						}
						else
						{
							htmltext = "30283-02.html";
						}
					}
					else if (hasQuestItems(player, ALLTRANS_INSTRUCTIONS))
					{
						if (getQuestItemsCount(player, JOURNEYMAN_RING) < 7)
						{
							htmltext = "30283-04.html";
						}
						else
						{
							htmltext = "30283-05.html";
						}
					}
					break;
				}
				case BLACKSMITH_PINTER:
				{
					if (hasQuestItems(player, ALLTRANS_INSTRUCTIONS))
					{
						if (hasQuestItems(player, ALLTRANS_2ND_RECOMMENDATION))
						{
							htmltext = "30298-02.html";
						}
						else if (hasQuestItems(player, PINTERS_INSTRUCTIONS))
						{
							if (getQuestItemsCount(player, AMBER_BEAD) < 70)
							{
								htmltext = "30298-06.html";
							}
							else
							{
								takeItems(player, RECIPE_AMBER_BEAD, 1);
								takeItems(player, PINTERS_INSTRUCTIONS, 1);
								takeItems(player, AMBER_BEAD, -1);
								takeItems(player, AMBER_LUMP, -1);
								giveItems(player, JOURNEYMAN_DECO_BEADS, 7);
								if (getQuestItemsCount(player, JOURNEYMAN_GEM) >= 7)
								{
									qs.setCond(6, true);
								}
								htmltext = "30298-07.html";
							}
						}
						else if (!hasQuestItems(player, PINTERS_INSTRUCTIONS) && hasAtLeastOneQuestItem(player, JOURNEYMAN_DECO_BEADS, JOURNEYMAN_RING))
						{
							htmltext = "30298-08.html";
						}
					}
					break;
				}
				case BLACKSMITH_DUNING:
				{
					if (hasQuestItems(player, ALLTRANS_INSTRUCTIONS, NORMANS_INSTRUCTIONS))
					{
						if (hasQuestItems(player, NORMANS_RECEIPT) && !hasQuestItems(player, DUNINGS_INSTRUCTIONS))
						{
							htmltext = "30688-01.html";
						}
						if (hasQuestItems(player, DUNINGS_INSTRUCTIONS) && !hasQuestItems(player, NORMANS_RECEIPT) && (getQuestItemsCount(player, DUNINGS_KEY) < 30))
						{
							htmltext = "30688-03.html";
						}
						else if ((getQuestItemsCount(player, DUNINGS_KEY) >= 30) && !hasQuestItems(player, DUNINGS_INSTRUCTIONS))
						{
							htmltext = "30688-04.html";
						}
					}
					else if (hasQuestItems(player, ALLTRANS_INSTRUCTIONS) && !hasAtLeastOneQuestItem(player, NORMANS_INSTRUCTIONS, DUNINGS_INSTRUCTIONS))
					{
						htmltext = "30688-05.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == WAREHOUSE_KEEPER_VALKON)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
	
	@Override
	public boolean checkPartyMember(L2PcInstance player, L2Npc npc)
	{
		boolean check = false;
		switch (npc.getId())
		{
			case ANT:
			case ANT_CAPTAIN:
			case ANT_OVERSEER:
			{
				check = hasQuestItems(player, ALLTRANS_INSTRUCTIONS, PINTERS_INSTRUCTIONS) && (getQuestItemsCount(player, AMBER_BEAD) < 70);
				break;
			}
			case GRANITE_GOLEM:
			{
				check = hasQuestItems(player, ALLTRANS_INSTRUCTIONS, NORMANS_LIST) && (getQuestItemsCount(player, GRANITE_WHETSTONE) < 70);
				break;
			}
			case SILENOS:
			{
				check = hasQuestItems(player, ALLTRANS_INSTRUCTIONS, NORMANS_LIST) && (getQuestItemsCount(player, BRAIDED_YARN) < 70);
				break;
			}
			case STRAIN:
			case GHOUL:
			{
				check = hasQuestItems(player, ALLTRANS_INSTRUCTIONS, NORMANS_LIST) && (getQuestItemsCount(player, GRAY_BONE_POWDER) < 70);
				break;
			}
			case DEAD_SEEKER:
			{
				check = hasQuestItems(player, ALLTRANS_INSTRUCTIONS, NORMANS_LIST) && (getQuestItemsCount(player, RED_PIGMENT) < 70);
				break;
			}
			case BREKA_ORC:
			case BREKA_ORC_ARCHER:
			case BREKA_ORC_SHAMAN:
			case BREKA_ORC_OVERLORD:
			case BREKA_ORC_WARRIOR:
			{
				check = hasQuestItems(player, ALLTRANS_INSTRUCTIONS, NORMANS_INSTRUCTIONS, DUNINGS_INSTRUCTIONS) && (getQuestItemsCount(player, DUNINGS_KEY) < 30);
				break;
			}
		}
		return check;
	}
}