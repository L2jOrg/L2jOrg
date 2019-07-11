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
package quests.Q00231_TestOfTheMaestro;

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
 * Test Of The Maestro (231)
 * @author ivantotov
 */
public final class Q00231_TestOfTheMaestro extends Quest
{
	// NPCs
	private static final int IRON_GATES_LOCKIRIN = 30531;
	private static final int GOLDEN_WHEELS_SPIRON = 30532;
	private static final int SILVER_SCALES_BALANKI = 30533;
	private static final int BRONZE_KEYS_KEEF = 30534;
	private static final int GRAY_PILLAR_MEMBER_FILAUR = 30535;
	private static final int BLACK_ANVILS_ARIN = 30536;
	private static final int MASTER_TOMA = 30556;
	private static final int CHIEF_CROTO = 30671;
	private static final int JAILER_DUBABAH = 30672;
	private static final int RESEARCHER_LORAIN = 30673;
	// Items
	private static final int RECOMMENDATION_OF_BALANKI = 2864;
	private static final int RECOMMENDATION_OF_FILAUR = 2865;
	private static final int RECOMMENDATION_OF_ARIN = 2866;
	private static final int LETTER_OF_SOLDER_DERACHMENT = 2868;
	private static final int PAINT_OF_KAMURU = 2869;
	private static final int NECKLACE_OF_KAMUTU = 2870;
	private static final int PAINT_OF_TELEPORT_DEVICE = 2871;
	private static final int TELEPORT_DEVICE = 2872;
	private static final int ARCHITECTURE_OF_CRUMA = 2873;
	private static final int REPORT_OF_CRUMA = 2874;
	private static final int INGREDIENTS_OF_ANTIDOTE = 2875;
	private static final int STINGER_WASP_NEEDLE = 2876;
	private static final int MARSH_SPIDERS_WEB = 2877;
	private static final int BLOOD_OF_LEECH = 2878;
	private static final int BROKEN_TELEPORT_DEVICE = 2916;
	// Reward
	private static final int MARK_OF_MAESTRO = 2867;
	// Monster
	private static final int KING_BUGBEAR = 20150;
	private static final int GIANT_MIST_LEECH = 20225;
	private static final int STINGER_WASP = 20229;
	private static final int MARSH_SPIDER = 20233;
	// Quest Monster
	private static final int EVIL_EYE_LORD = 27133;
	// Misc
	private static final int MIN_LEVEL = 39;
	
	public Q00231_TestOfTheMaestro()
	{
		super(231);
		addStartNpc(IRON_GATES_LOCKIRIN);
		addTalkId(IRON_GATES_LOCKIRIN, GOLDEN_WHEELS_SPIRON, SILVER_SCALES_BALANKI, BRONZE_KEYS_KEEF, GRAY_PILLAR_MEMBER_FILAUR, BLACK_ANVILS_ARIN, MASTER_TOMA, CHIEF_CROTO, JAILER_DUBABAH, RESEARCHER_LORAIN);
		addKillId(KING_BUGBEAR, GIANT_MIST_LEECH, STINGER_WASP, MARSH_SPIDER, EVIL_EYE_LORD);
		registerQuestItems(RECOMMENDATION_OF_BALANKI, RECOMMENDATION_OF_FILAUR, RECOMMENDATION_OF_ARIN, LETTER_OF_SOLDER_DERACHMENT, PAINT_OF_KAMURU, NECKLACE_OF_KAMUTU, PAINT_OF_TELEPORT_DEVICE, TELEPORT_DEVICE, ARCHITECTURE_OF_CRUMA, REPORT_OF_CRUMA, INGREDIENTS_OF_ANTIDOTE, STINGER_WASP_NEEDLE, MARSH_SPIDERS_WEB, BLOOD_OF_LEECH, BROKEN_TELEPORT_DEVICE);
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
				if (qs.isCreated())
				{
					qs.startQuest();
					qs.setMemoState(1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				}
				break;
			}
			case "30533-02.html":
			{
				qs.setMemoState(2);
				htmltext = event;
				break;
			}
			case "30556-02.html":
			case "30556-03.html":
			case "30556-04.html":
			{
				htmltext = event;
				break;
			}
			case "30556-05.html":
			{
				if (hasQuestItems(player, PAINT_OF_TELEPORT_DEVICE))
				{
					giveItems(player, BROKEN_TELEPORT_DEVICE, 1);
					takeItems(player, PAINT_OF_TELEPORT_DEVICE, 1);
					player.teleToLocation(140352, -194133, -3146);
					startQuestTimer("SPAWN_KING_BUGBEAR", 5000, npc, player);
					htmltext = event;
				}
				break;
			}
			case "30671-02.html":
			{
				giveItems(player, PAINT_OF_KAMURU, 1);
				htmltext = event;
				break;
			}
			case "30673-04.html":
			{
				if (hasQuestItems(player, INGREDIENTS_OF_ANTIDOTE) && (getQuestItemsCount(player, STINGER_WASP_NEEDLE) >= 10) && (getQuestItemsCount(player, MARSH_SPIDERS_WEB) >= 10) && (getQuestItemsCount(player, BLOOD_OF_LEECH) >= 10))
				{
					giveItems(player, REPORT_OF_CRUMA, 1);
					takeItems(player, STINGER_WASP_NEEDLE, -1);
					takeItems(player, MARSH_SPIDERS_WEB, -1);
					takeItems(player, BLOOD_OF_LEECH, -1);
					takeItems(player, INGREDIENTS_OF_ANTIDOTE, 1);
					htmltext = event;
				}
				break;
			}
			case "SPAWN_KING_BUGBEAR":
			{
				addAttackPlayerDesire(addSpawn(KING_BUGBEAR, 140395, -194147, -3146, 0, false, 200000, false), player);
				addAttackPlayerDesire(addSpawn(KING_BUGBEAR, 140395, -194147, -3146, 0, false, 200000, false), player);
				addAttackPlayerDesire(addSpawn(KING_BUGBEAR, 140395, -194147, -3146, 0, false, 200000, false), player);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			switch (npc.getId())
			{
				case GIANT_MIST_LEECH:
				{
					if (qs.isMemoState(4) && hasQuestItems(killer, INGREDIENTS_OF_ANTIDOTE) && (getQuestItemsCount(killer, BLOOD_OF_LEECH) < 10))
					{
						giveItems(killer, BLOOD_OF_LEECH, 1);
						if (getQuestItemsCount(killer, BLOOD_OF_LEECH) >= 10)
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case STINGER_WASP:
				{
					if (qs.isMemoState(4) && hasQuestItems(killer, INGREDIENTS_OF_ANTIDOTE) && (getQuestItemsCount(killer, STINGER_WASP_NEEDLE) < 10))
					{
						giveItems(killer, STINGER_WASP_NEEDLE, 1);
						if (getQuestItemsCount(killer, STINGER_WASP_NEEDLE) >= 10)
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case MARSH_SPIDER:
				{
					if (qs.isMemoState(4) && hasQuestItems(killer, INGREDIENTS_OF_ANTIDOTE) && (getQuestItemsCount(killer, MARSH_SPIDERS_WEB) < 10))
					{
						giveItems(killer, MARSH_SPIDERS_WEB, 1);
						if (getQuestItemsCount(killer, MARSH_SPIDERS_WEB) >= 10)
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case EVIL_EYE_LORD:
				{
					if (qs.isMemoState(2) && hasQuestItems(killer, PAINT_OF_KAMURU) && !hasQuestItems(killer, NECKLACE_OF_KAMUTU))
					{
						giveItems(killer, NECKLACE_OF_KAMUTU, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		final int memoState = qs.getMemoState();
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			if (npc.getId() == IRON_GATES_LOCKIRIN)
			{
				if (player.getClassId() == ClassId.ARTISAN)
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						htmltext = "30531-03.htm";
					}
					else
					{
						htmltext = "30531-01.html";
					}
				}
				else
				{
					htmltext = "30531-02.html";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case IRON_GATES_LOCKIRIN:
				{
					if ((memoState >= 1) && !hasQuestItems(player, RECOMMENDATION_OF_BALANKI, RECOMMENDATION_OF_FILAUR, RECOMMENDATION_OF_ARIN))
					{
						htmltext = "30531-05.html";
					}
					else if (hasQuestItems(player, RECOMMENDATION_OF_BALANKI, RECOMMENDATION_OF_FILAUR, RECOMMENDATION_OF_ARIN))
					{
						giveAdena(player, 372154, true);
						giveItems(player, MARK_OF_MAESTRO, 1);
						addExpAndSp(player, 2085244, 141240);
						qs.exitQuest(false, true);
						player.sendPacket(new SocialAction(player.getObjectId(), 3));
						htmltext = "30531-06.html";
					}
					break;
				}
				case GOLDEN_WHEELS_SPIRON:
				{
					htmltext = "30532-01.html";
					break;
				}
				case SILVER_SCALES_BALANKI:
				{
					if ((memoState == 1) && !hasQuestItems(player, RECOMMENDATION_OF_BALANKI))
					{
						htmltext = "30533-01.html";
					}
					else if (memoState == 2)
					{
						if (!hasQuestItems(player, LETTER_OF_SOLDER_DERACHMENT))
						{
							htmltext = "30533-03.html";
						}
						else
						{
							giveItems(player, RECOMMENDATION_OF_BALANKI, 1);
							takeItems(player, LETTER_OF_SOLDER_DERACHMENT, 1);
							qs.setMemoState(1);
							if (hasQuestItems(player, RECOMMENDATION_OF_ARIN, RECOMMENDATION_OF_FILAUR))
							{
								qs.setCond(2, true);
							}
							htmltext = "30533-04.html";
						}
					}
					else if (hasQuestItems(player, RECOMMENDATION_OF_BALANKI))
					{
						htmltext = "30533-05.html";
					}
					break;
				}
				case BRONZE_KEYS_KEEF:
				{
					htmltext = "30534-01.html";
					break;
				}
				case GRAY_PILLAR_MEMBER_FILAUR:
				{
					if ((memoState == 1) && !hasQuestItems(player, RECOMMENDATION_OF_FILAUR))
					{
						giveItems(player, ARCHITECTURE_OF_CRUMA, 1);
						qs.setMemoState(4);
						htmltext = "30535-01.html";
					}
					else if (memoState == 4)
					{
						if (hasQuestItems(player, ARCHITECTURE_OF_CRUMA) && !hasQuestItems(player, REPORT_OF_CRUMA))
						{
							htmltext = "30535-02.html";
						}
						else if (hasQuestItems(player, REPORT_OF_CRUMA) && !hasQuestItems(player, ARCHITECTURE_OF_CRUMA))
						{
							giveItems(player, RECOMMENDATION_OF_FILAUR, 1);
							takeItems(player, REPORT_OF_CRUMA, 1);
							qs.setMemoState(1);
							if (hasQuestItems(player, RECOMMENDATION_OF_BALANKI, RECOMMENDATION_OF_ARIN))
							{
								qs.setCond(2, true);
							}
							htmltext = "30535-03.html";
						}
					}
					else if (hasQuestItems(player, RECOMMENDATION_OF_FILAUR))
					{
						htmltext = "30535-04.html";
					}
					break;
				}
				case BLACK_ANVILS_ARIN:
				{
					if ((memoState == 1) && !hasQuestItems(player, RECOMMENDATION_OF_ARIN))
					{
						giveItems(player, PAINT_OF_TELEPORT_DEVICE, 1);
						qs.setMemoState(3);
						htmltext = "30536-01.html";
					}
					else if (memoState == 3)
					{
						if (hasQuestItems(player, PAINT_OF_TELEPORT_DEVICE) && !hasQuestItems(player, TELEPORT_DEVICE))
						{
							htmltext = "30536-02.html";
						}
						else if (getQuestItemsCount(player, TELEPORT_DEVICE) >= 5)
						{
							giveItems(player, RECOMMENDATION_OF_ARIN, 1);
							takeItems(player, TELEPORT_DEVICE, -1);
							qs.setMemoState(1);
							if (hasQuestItems(player, RECOMMENDATION_OF_BALANKI, RECOMMENDATION_OF_FILAUR))
							{
								qs.setCond(2, true);
							}
							htmltext = "30536-03.html";
						}
					}
					else if (hasQuestItems(player, RECOMMENDATION_OF_ARIN))
					{
						htmltext = "30536-04.html";
					}
					break;
				}
				case MASTER_TOMA:
				{
					if (memoState == 3)
					{
						if (hasQuestItems(player, PAINT_OF_TELEPORT_DEVICE))
						{
							htmltext = "30556-01.html";
						}
						else if (hasQuestItems(player, BROKEN_TELEPORT_DEVICE))
						{
							giveItems(player, TELEPORT_DEVICE, 5);
							takeItems(player, BROKEN_TELEPORT_DEVICE, 1);
							htmltext = "30556-06.html";
						}
						else if (getQuestItemsCount(player, TELEPORT_DEVICE) == 5)
						{
							htmltext = "30556-07.html";
						}
					}
					break;
				}
				case CHIEF_CROTO:
				{
					if ((memoState == 2) && !hasAtLeastOneQuestItem(player, PAINT_OF_KAMURU, NECKLACE_OF_KAMUTU, LETTER_OF_SOLDER_DERACHMENT))
					{
						htmltext = "30671-01.html";
					}
					else if (hasQuestItems(player, PAINT_OF_KAMURU) && !hasQuestItems(player, NECKLACE_OF_KAMUTU))
					{
						htmltext = "30671-03.html";
					}
					else if (hasQuestItems(player, NECKLACE_OF_KAMUTU))
					{
						giveItems(player, LETTER_OF_SOLDER_DERACHMENT, 1);
						takeItems(player, NECKLACE_OF_KAMUTU, 1);
						takeItems(player, PAINT_OF_KAMURU, 1);
						htmltext = "30671-04.html";
					}
					else if (hasQuestItems(player, LETTER_OF_SOLDER_DERACHMENT))
					{
						htmltext = "30671-05.html";
					}
					break;
				}
				case JAILER_DUBABAH:
				{
					if (hasQuestItems(player, PAINT_OF_KAMURU))
					{
						htmltext = "30672-01.html";
					}
					break;
				}
				case RESEARCHER_LORAIN:
				{
					if (memoState == 4)
					{
						if (hasQuestItems(player, ARCHITECTURE_OF_CRUMA) && !hasAtLeastOneQuestItem(player, INGREDIENTS_OF_ANTIDOTE, REPORT_OF_CRUMA))
						{
							giveItems(player, INGREDIENTS_OF_ANTIDOTE, 1);
							takeItems(player, ARCHITECTURE_OF_CRUMA, 1);
							htmltext = "30673-01.html";
						}
						else if (hasQuestItems(player, INGREDIENTS_OF_ANTIDOTE) && !hasQuestItems(player, REPORT_OF_CRUMA))
						{
							if ((getQuestItemsCount(player, STINGER_WASP_NEEDLE) >= 10) && (getQuestItemsCount(player, MARSH_SPIDERS_WEB) >= 10) && (getQuestItemsCount(player, BLOOD_OF_LEECH) >= 10))
							{
								htmltext = "30673-03.html";
							}
							else
							{
								htmltext = "30673-02.html";
							}
						}
						else if (hasQuestItems(player, REPORT_OF_CRUMA))
						{
							htmltext = "30673-05.html";
						}
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == IRON_GATES_LOCKIRIN)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}