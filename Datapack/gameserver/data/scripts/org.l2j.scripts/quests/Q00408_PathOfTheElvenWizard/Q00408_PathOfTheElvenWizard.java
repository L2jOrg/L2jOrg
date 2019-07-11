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
package quests.Q00408_PathOfTheElvenWizard;

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
 * Path Of The Elven Wizard (408)
 * @author ivantotov
 */
public final class Q00408_PathOfTheElvenWizard extends Quest
{
	// NPCs
	private static final int ROSSELA = 30414;
	private static final int GREENIS = 30157;
	private static final int THALIA = 30371;
	private static final int NORTHWIND = 30423;
	// Items
	private static final int ROSELLAS_LETTER = 1218;
	private static final int RED_DOWN = 1219;
	private static final int MAGICAL_POWERS_RUBY = 1220;
	private static final int PURE_AQUAMARINE = 1221;
	private static final int APPETIZING_APPLE = 1222;
	private static final int GOLD_LEAVES = 1223;
	private static final int IMMORTAL_LOVE = 1224;
	private static final int AMETHYST = 1225;
	private static final int NOBILITY_AMETHYST = 1226;
	private static final int FERTILITY_PERIDOT = 1229;
	private static final int GREENISS_CHARM = 1272;
	private static final int SAP_OF_THE_MOTHER_TREE = 1273;
	private static final int LUCKY_POTPOURRI = 1274;
	// Reward
	private static final int ETERNITY_DIAMOND = 1230;
	// Monster
	private static final int DRYAD_ELDER = 20019;
	private static final int SUKAR_WERERAT_LEADER = 20047;
	private static final int PINCER_SPIDER = 20466;
	// Misc
	private static final int MIN_LEVEL = 19;
	
	public Q00408_PathOfTheElvenWizard()
	{
		super(408);
		addStartNpc(ROSSELA);
		addTalkId(ROSSELA, GREENIS, THALIA, NORTHWIND);
		addKillId(DRYAD_ELDER, SUKAR_WERERAT_LEADER, PINCER_SPIDER);
		registerQuestItems(ROSELLAS_LETTER, RED_DOWN, MAGICAL_POWERS_RUBY, PURE_AQUAMARINE, APPETIZING_APPLE, GOLD_LEAVES, IMMORTAL_LOVE, AMETHYST, NOBILITY_AMETHYST, FERTILITY_PERIDOT, GREENISS_CHARM, SAP_OF_THE_MOTHER_TREE, LUCKY_POTPOURRI);
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
				if (player.getClassId() != ClassId.ELVEN_MAGE)
				{
					if (player.getClassId() == ClassId.ELVEN_WIZARD)
					{
						htmltext = "30414-02a.htm";
					}
					else
					{
						htmltext = "30414-03.htm";
					}
				}
				else if (player.getLevel() < MIN_LEVEL)
				{
					htmltext = "30414-04.htm";
				}
				else if (hasQuestItems(player, ETERNITY_DIAMOND))
				{
					htmltext = "30414-05.htm";
				}
				else
				{
					if (!hasQuestItems(player, FERTILITY_PERIDOT))
					{
						giveItems(player, FERTILITY_PERIDOT, 1);
					}
					qs.startQuest();
					htmltext = "30414-06.htm";
				}
				break;
			}
			case "30414-02.htm":
			{
				htmltext = event;
				break;
			}
			case "30414-10.html":
			{
				if (hasQuestItems(player, MAGICAL_POWERS_RUBY))
				{
					htmltext = event;
				}
				else if (!hasQuestItems(player, MAGICAL_POWERS_RUBY) && hasQuestItems(player, FERTILITY_PERIDOT))
				{
					if (!hasQuestItems(player, ROSELLAS_LETTER))
					{
						giveItems(player, ROSELLAS_LETTER, 1);
					}
					htmltext = "30414-07.html";
				}
				break;
			}
			case "30414-12.html":
			{
				if (hasQuestItems(player, PURE_AQUAMARINE))
				{
					htmltext = event;
				}
				else if (!hasQuestItems(player, PURE_AQUAMARINE) && hasQuestItems(player, FERTILITY_PERIDOT))
				{
					if (!hasQuestItems(player, APPETIZING_APPLE))
					{
						giveItems(player, APPETIZING_APPLE, 1);
					}
					htmltext = "30414-13.html";
				}
				break;
			}
			case "30414-16.html":
			{
				if (hasQuestItems(player, NOBILITY_AMETHYST))
				{
					htmltext = event;
				}
				else if (!hasQuestItems(player, NOBILITY_AMETHYST) && hasQuestItems(player, FERTILITY_PERIDOT))
				{
					if (!hasQuestItems(player, IMMORTAL_LOVE))
					{
						giveItems(player, IMMORTAL_LOVE, 1);
					}
					htmltext = "30414-17.html";
				}
				break;
			}
			case "30157-02.html":
			{
				if (hasQuestItems(player, ROSELLAS_LETTER))
				{
					takeItems(player, ROSELLAS_LETTER, 1);
					if (!hasQuestItems(player, GREENISS_CHARM))
					{
						giveItems(player, GREENISS_CHARM, 1);
					}
				}
				htmltext = event;
				break;
			}
			case "30371-02.html":
			{
				if (hasQuestItems(player, APPETIZING_APPLE))
				{
					takeItems(player, APPETIZING_APPLE, 1);
					if (!hasQuestItems(player, SAP_OF_THE_MOTHER_TREE))
					{
						giveItems(player, SAP_OF_THE_MOTHER_TREE, 1);
					}
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
				case DRYAD_ELDER:
				{
					if (hasQuestItems(killer, SAP_OF_THE_MOTHER_TREE) && (getQuestItemsCount(killer, GOLD_LEAVES) < 5) && (getRandom(100) < 40))
					{
						giveItems(killer, GOLD_LEAVES, 1);
						if (getQuestItemsCount(killer, GOLD_LEAVES) == 5)
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
				case SUKAR_WERERAT_LEADER:
				{
					if (hasQuestItems(killer, LUCKY_POTPOURRI) && (getQuestItemsCount(killer, AMETHYST) < 2) && (getRandom(100) < 40))
					{
						giveItems(killer, AMETHYST, 1);
						if (getQuestItemsCount(killer, AMETHYST) == 2)
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
				case PINCER_SPIDER:
				{
					if (hasQuestItems(killer, GREENISS_CHARM) && (getQuestItemsCount(killer, RED_DOWN) < 5) && (getRandom(100) < 70))
					{
						giveItems(killer, RED_DOWN, 1);
						if (getQuestItemsCount(killer, RED_DOWN) == 5)
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
			if (npc.getId() == ROSSELA)
			{
				htmltext = "30414-01.htm";
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == ROSSELA)
			{
				return htmltext;
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case ROSSELA:
				{
					if (!hasAtLeastOneQuestItem(player, ROSELLAS_LETTER, APPETIZING_APPLE, IMMORTAL_LOVE, GREENISS_CHARM, SAP_OF_THE_MOTHER_TREE, LUCKY_POTPOURRI) && hasQuestItems(player, FERTILITY_PERIDOT) && !hasQuestItems(player, MAGICAL_POWERS_RUBY, NOBILITY_AMETHYST, PURE_AQUAMARINE))
					{
						htmltext = "30414-11.html";
					}
					else if (hasQuestItems(player, ROSELLAS_LETTER))
					{
						htmltext = "30414-08.html";
					}
					else if (hasQuestItems(player, GREENISS_CHARM))
					{
						if (getQuestItemsCount(player, RED_DOWN) < 5)
						{
							htmltext = "30414-09.html";
						}
						else
						{
							htmltext = "30414-21.html";
						}
					}
					else if (hasQuestItems(player, APPETIZING_APPLE))
					{
						htmltext = "30414-14.html";
					}
					else if (hasQuestItems(player, SAP_OF_THE_MOTHER_TREE))
					{
						if (getQuestItemsCount(player, GOLD_LEAVES) < 5)
						{
							htmltext = "30414-15.html";
						}
						else
						{
							htmltext = "30414-22.html";
						}
					}
					else if (hasQuestItems(player, IMMORTAL_LOVE))
					{
						htmltext = "30414-18.html";
					}
					else if (hasQuestItems(player, LUCKY_POTPOURRI))
					{
						if (getQuestItemsCount(player, AMETHYST) < 2)
						{
							htmltext = "30414-19.html";
						}
						else
						{
							htmltext = "30414-23.html";
						}
					}
					else
					{
						if (!hasAtLeastOneQuestItem(player, ROSELLAS_LETTER, APPETIZING_APPLE, IMMORTAL_LOVE, GREENISS_CHARM, SAP_OF_THE_MOTHER_TREE, LUCKY_POTPOURRI) && hasQuestItems(player, FERTILITY_PERIDOT, MAGICAL_POWERS_RUBY, NOBILITY_AMETHYST, PURE_AQUAMARINE))
						{
							if (!hasQuestItems(player, ETERNITY_DIAMOND))
							{
								giveItems(player, ETERNITY_DIAMOND, 1);
							}
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
							htmltext = "30414-20.html";
						}
					}
					break;
				}
				case GREENIS:
				{
					if (hasQuestItems(player, ROSELLAS_LETTER))
					{
						htmltext = "30157-01.html";
					}
					else if (hasQuestItems(player, GREENISS_CHARM))
					{
						if (getQuestItemsCount(player, RED_DOWN) < 5)
						{
							htmltext = "30157-03.html";
						}
						else
						{
							takeItems(player, RED_DOWN, -1);
							if (!hasQuestItems(player, MAGICAL_POWERS_RUBY))
							{
								giveItems(player, MAGICAL_POWERS_RUBY, 1);
							}
							takeItems(player, GREENISS_CHARM, 1);
							htmltext = "30157-04.html";
						}
					}
					break;
				}
				case THALIA:
				{
					if (hasQuestItems(player, APPETIZING_APPLE))
					{
						htmltext = "30371-01.html";
					}
					else if (hasQuestItems(player, SAP_OF_THE_MOTHER_TREE))
					{
						if (getQuestItemsCount(player, GOLD_LEAVES) < 5)
						{
							htmltext = "30371-03.html";
						}
						else
						{
							if (!hasQuestItems(player, PURE_AQUAMARINE))
							{
								giveItems(player, PURE_AQUAMARINE, 1);
							}
							takeItems(player, GOLD_LEAVES, -1);
							takeItems(player, SAP_OF_THE_MOTHER_TREE, 1);
							htmltext = "30371-04.html";
						}
					}
					break;
				}
				case NORTHWIND:
				{
					if (hasQuestItems(player, IMMORTAL_LOVE))
					{
						takeItems(player, IMMORTAL_LOVE, 1);
						if (!hasQuestItems(player, LUCKY_POTPOURRI))
						{
							giveItems(player, LUCKY_POTPOURRI, 1);
						}
						htmltext = "30423-01.html";
					}
					else if (hasQuestItems(player, LUCKY_POTPOURRI))
					{
						if (getQuestItemsCount(player, AMETHYST) < 2)
						{
							htmltext = "30423-02.html";
						}
						else
						{
							takeItems(player, AMETHYST, -1);
							if (!hasQuestItems(player, NOBILITY_AMETHYST))
							{
								giveItems(player, NOBILITY_AMETHYST, 1);
							}
							takeItems(player, LUCKY_POTPOURRI, 1);
							htmltext = "30423-03.html";
						}
					}
					break;
				}
			}
		}
		return htmltext;
	}
}