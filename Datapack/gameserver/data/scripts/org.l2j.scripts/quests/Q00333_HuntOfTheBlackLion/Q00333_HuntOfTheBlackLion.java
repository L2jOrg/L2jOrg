/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q00333_HuntOfTheBlackLion;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.util.GameUtils;

/**
 * Hunt Of The Black Lion (333)
 * @author ivantotov
 */
public final class Q00333_HuntOfTheBlackLion extends Quest
{
	// NPCs
	private static final int ABYSSAL_CELEBRANT_UNDRIAS = 30130;
	private static final int BLACKSMITH_RUPIO = 30471;
	private static final int IRON_GATES_LOCKIRIN = 30531;
	private static final int MERCENARY_CAPTAIN_SOPHYA = 30735;
	private static final int MERCENARY_REEDFOOT = 30736;
	private static final int GUILDSMAN_MORGON = 30737;
	// Items
	private static final int BLACK_LION_MARK = 1369;
	private static final int CARGO_BOX_1ST = 3440;
	private static final int CARGO_BOX_2ND = 3441;
	private static final int CARGO_BOX_3RD = 3442;
	private static final int CARGO_BOX_4TH = 3443;
	private static final int STATUE_OF_SHILEN_HEAD = 3457;
	private static final int STATUE_OF_SHILEN_TORSO = 3458;
	private static final int STATUE_OF_SHILEN_ARM = 3459;
	private static final int STATUE_OF_SHILEN_LEG = 3460;
	private static final int COMPLETE_STATUE_OF_SHILEN = 3461;
	private static final int FRAGMENT_OF_ANCIENT_TABLET_1ST_PIECE = 3462;
	private static final int FRAGMENT_OF_ANCIENT_TABLET_2ND_PIECE = 3463;
	private static final int FRAGMENT_OF_ANCIENT_TABLET_3RD_PIECE = 3464;
	private static final int FRAGMENT_OF_ANCIENT_TABLET_4TH_PIECE = 3465;
	private static final int COMPLETE_ANCIENT_TABLET = 3466;
	private static final int SOPHYAS_1ST_ORDER = 3671;
	private static final int SOPHYAS_2ND_ORDER = 3672;
	private static final int SOPHYAS_3RD_ORDER = 3673;
	private static final int SOPHYAS_4TH_ORDER = 3674;
	private static final int LIONS_CLAW = 3675;
	private static final int LIONS_EYE = 3676;
	private static final int GUILD_COIN = 3677;
	private static final int UNDEAD_ASH = 3848;
	private static final int BLOODY_AXE_INSIGNIA = 3849;
	private static final int DELU_LIZARDMAN_FANG = 3850;
	private static final int STAKATO_TALON = 3851;
	// Rewards
	private static final int ALACRITY_POTION = 735;
	private static final int SCROL_OF_ESCAPE = 736;
	private static final int HELING_POTION = 1061;
	private static final int SOULSHOT_D_GRADE = 1463;
	private static final int SPIRITSHOT_D_GRADE = 2510;
	private static final int GLUDIO_APPLES = 3444;
	private static final int DION_CORN_MEAL = 3445;
	private static final int DIRE_WOLF_PELTS = 3446;
	private static final int MOONSTONE = 3447;
	private static final int GLUDIO_WHEAT_FLOUR = 3448;
	private static final int SPIDERSILK_ROPE = 3449;
	private static final int ALEXANDRITE = 3450;
	private static final int SILVER_TEA_SERVICE = 3451;
	private static final int MECHANIC_GOLEM_SPACE_PARTS = 3452;
	private static final int FIRE_EMERALD = 3453;
	private static final int AVELLAN_SILK_FROCK = 3454;
	private static final int FERIOTIC_PORCELAIN_URM = 3455;
	private static final int IMPERIAL_DIAMOND = 3456;
	// Monster
	private static final int MARSH_STAKATO = 20157;
	private static final int NEER_CRAWLER = 20160;
	private static final int SPECTER = 20171;
	private static final int SORROW_MAIDEN = 20197;
	private static final int NEER_CRAWLER_BERSERKER = 20198;
	private static final int STRAIN = 20200;
	private static final int GHOUL = 20201;
	private static final int OL_MAHUM_GUERILLA = 20207;
	private static final int OL_MAHUM_RAIDER = 20208;
	private static final int OL_MAHUM_MARKSMAN = 20209;
	private static final int OL_MAHUM_SERGEANT = 20210;
	private static final int OL_MAHUM_CAPTAIN = 20211;
	private static final int MARSH_STAKATO_WORKER = 20230;
	private static final int MARSH_STAKATO_SOLDIER = 20232;
	private static final int MARSH_STAKATO_DRONE = 20234;
	private static final int DELU_LIZARDMAN = 20251;
	private static final int DELU_LIZARDMAN_SCOUT = 20252;
	private static final int DELU_LIZARDMAN_WARRIOR = 20253;
	// Quest Monster
	private static final int DELU_LIZARDMAN_HEADHUNTER = 27151;
	private static final int MARSH_STAKATO_MARQUESS = 27152;
	// Misc
	private static final int MIN_LEVEL = 25;
	
	public Q00333_HuntOfTheBlackLion()
	{
		super(333);
		addStartNpc(MERCENARY_CAPTAIN_SOPHYA);
		addTalkId(MERCENARY_CAPTAIN_SOPHYA, ABYSSAL_CELEBRANT_UNDRIAS, BLACKSMITH_RUPIO, IRON_GATES_LOCKIRIN, MERCENARY_REEDFOOT, GUILDSMAN_MORGON);
		addKillId(MARSH_STAKATO, NEER_CRAWLER, SPECTER, SORROW_MAIDEN, NEER_CRAWLER_BERSERKER, STRAIN, GHOUL, OL_MAHUM_GUERILLA, OL_MAHUM_RAIDER, OL_MAHUM_MARKSMAN, OL_MAHUM_SERGEANT, OL_MAHUM_CAPTAIN, MARSH_STAKATO_WORKER, MARSH_STAKATO_SOLDIER, MARSH_STAKATO_DRONE, DELU_LIZARDMAN, DELU_LIZARDMAN_SCOUT, DELU_LIZARDMAN_WARRIOR, DELU_LIZARDMAN_HEADHUNTER, MARSH_STAKATO_MARQUESS);
		registerQuestItems(BLACK_LION_MARK, CARGO_BOX_1ST, CARGO_BOX_2ND, CARGO_BOX_3RD, CARGO_BOX_4TH, STATUE_OF_SHILEN_HEAD, STATUE_OF_SHILEN_TORSO, STATUE_OF_SHILEN_ARM, STATUE_OF_SHILEN_LEG, COMPLETE_STATUE_OF_SHILEN, FRAGMENT_OF_ANCIENT_TABLET_1ST_PIECE, FRAGMENT_OF_ANCIENT_TABLET_2ND_PIECE, FRAGMENT_OF_ANCIENT_TABLET_3RD_PIECE, FRAGMENT_OF_ANCIENT_TABLET_4TH_PIECE, COMPLETE_ANCIENT_TABLET, SOPHYAS_1ST_ORDER, SOPHYAS_2ND_ORDER, SOPHYAS_3RD_ORDER, SOPHYAS_4TH_ORDER, LIONS_CLAW, LIONS_EYE, GUILD_COIN, UNDEAD_ASH, BLOODY_AXE_INSIGNIA, DELU_LIZARDMAN_FANG, STAKATO_TALON);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		final int chance = getRandom(100);
		final int chance1 = getRandom(100);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "30735-04.htm":
			{
				if (qs.isCreated())
				{
					qs.startQuest();
					htmltext = event;
				}
				break;
			}
			case "30735-05.html":
			case "30735-06.html":
			case "30735-07.html":
			case "30735-08.html":
			case "30735-09.html":
			case "30130-05.html":
			case "30531-05.html":
			case "30735-21.html":
			case "30735-24a.html":
			case "30735-25b.html":
			case "30736-06.html":
			case "30736-09.html":
			case "30737-07.html":
			{
				htmltext = event;
				break;
			}
			case "30735-10.html":
			{
				if (!hasQuestItems(player, SOPHYAS_1ST_ORDER))
				{
					giveItems(player, SOPHYAS_1ST_ORDER, 1);
					htmltext = event;
				}
				break;
			}
			case "30735-11.html":
			{
				if (!hasQuestItems(player, SOPHYAS_2ND_ORDER))
				{
					giveItems(player, SOPHYAS_2ND_ORDER, 1);
					htmltext = event;
				}
				break;
			}
			case "30735-12.html":
			{
				if (!hasQuestItems(player, SOPHYAS_3RD_ORDER))
				{
					giveItems(player, SOPHYAS_3RD_ORDER, 1);
					htmltext = event;
				}
				break;
			}
			case "30735-13.html":
			{
				if (!hasQuestItems(player, SOPHYAS_4TH_ORDER))
				{
					giveItems(player, SOPHYAS_4TH_ORDER, 1);
					htmltext = event;
				}
				break;
			}
			case "30735-16.html":
			{
				if (getQuestItemsCount(player, LIONS_CLAW) < 10)
				{
					htmltext = event;
				}
				else if ((getQuestItemsCount(player, LIONS_CLAW) >= 10) && (getQuestItemsCount(player, LIONS_EYE) < 4))
				{
					giveItems(player, LIONS_EYE, 1);
					if (chance < 25)
					{
						giveItems(player, HELING_POTION, 20);
					}
					else if (chance < 50)
					{
						if (player.isInCategory(CategoryType.FIGHTER_GROUP))
						{
							giveItems(player, SOULSHOT_D_GRADE, 100);
						}
						else if (player.isInCategory(CategoryType.MAGE_GROUP))
						{
							giveItems(player, SPIRITSHOT_D_GRADE, 50);
						}
					}
					else if (chance < 75)
					{
						giveItems(player, SCROL_OF_ESCAPE, 20);
					}
					else
					{
						giveItems(player, ALACRITY_POTION, 3);
					}
					takeItems(player, LIONS_CLAW, 10);
					htmltext = "30735-17a.html";
				}
				else if ((getQuestItemsCount(player, LIONS_CLAW) >= 10) && (getQuestItemsCount(player, LIONS_EYE) >= 4) && (getQuestItemsCount(player, LIONS_EYE) <= 7))
				{
					giveItems(player, LIONS_EYE, 1);
					if (chance < 25)
					{
						giveItems(player, HELING_POTION, 25);
					}
					else if (chance < 50)
					{
						if (player.isInCategory(CategoryType.FIGHTER_GROUP))
						{
							giveItems(player, SOULSHOT_D_GRADE, 200);
						}
						else if (player.isInCategory(CategoryType.MAGE_GROUP))
						{
							giveItems(player, SPIRITSHOT_D_GRADE, 100);
						}
					}
					else if (chance < 75)
					{
						giveItems(player, SCROL_OF_ESCAPE, 20);
					}
					else
					{
						giveItems(player, ALACRITY_POTION, 3);
					}
					takeItems(player, LIONS_CLAW, 10);
					htmltext = "30735-18b.html";
				}
				else if ((getQuestItemsCount(player, LIONS_CLAW) >= 10) && (getQuestItemsCount(player, LIONS_EYE) >= 8))
				{
					takeItems(player, LIONS_EYE, 8);
					if (chance < 25)
					{
						giveItems(player, HELING_POTION, 50);
					}
					else if (chance < 50)
					{
						if (player.isInCategory(CategoryType.FIGHTER_GROUP))
						{
							giveItems(player, SOULSHOT_D_GRADE, 400);
						}
						else if (player.isInCategory(CategoryType.MAGE_GROUP))
						{
							giveItems(player, SPIRITSHOT_D_GRADE, 200);
						}
					}
					else if (chance < 75)
					{
						giveItems(player, SCROL_OF_ESCAPE, 30);
					}
					else
					{
						giveItems(player, ALACRITY_POTION, 4);
					}
					takeItems(player, LIONS_CLAW, 10);
					htmltext = "30735-19b.html";
				}
				break;
			}
			case "30735-20.html":
			{
				takeItems(player, SOPHYAS_1ST_ORDER, -1);
				takeItems(player, SOPHYAS_2ND_ORDER, -1);
				takeItems(player, SOPHYAS_3RD_ORDER, -1);
				takeItems(player, SOPHYAS_4TH_ORDER, -1);
				htmltext = event;
				break;
			}
			case "30735-26.html":
			{
				if (hasQuestItems(player, BLACK_LION_MARK))
				{
					giveAdena(player, 12400, true);
					qs.exitQuest(true, true);
					htmltext = event;
				}
				break;
			}
			case "30130-04.html":
			{
				if (hasQuestItems(player, COMPLETE_STATUE_OF_SHILEN))
				{
					giveAdena(player, 30000, true);
					takeItems(player, COMPLETE_STATUE_OF_SHILEN, 1);
					htmltext = event;
				}
				break;
			}
			case "30471-03.html":
			{
				if (!hasQuestItems(player, STATUE_OF_SHILEN_HEAD, STATUE_OF_SHILEN_TORSO, STATUE_OF_SHILEN_ARM, STATUE_OF_SHILEN_LEG))
				{
					htmltext = event;
				}
				else
				{
					if (getRandom(100) < 50)
					{
						giveItems(player, COMPLETE_STATUE_OF_SHILEN, 1);
						takeItems(player, STATUE_OF_SHILEN_HEAD, 1);
						takeItems(player, STATUE_OF_SHILEN_TORSO, 1);
						takeItems(player, STATUE_OF_SHILEN_ARM, 1);
						takeItems(player, STATUE_OF_SHILEN_LEG, 1);
						htmltext = "30471-04.html";
					}
					else
					{
						takeItems(player, STATUE_OF_SHILEN_HEAD, 1);
						takeItems(player, STATUE_OF_SHILEN_TORSO, 1);
						takeItems(player, STATUE_OF_SHILEN_ARM, 1);
						takeItems(player, STATUE_OF_SHILEN_LEG, 1);
						htmltext = "30471-05.html";
					}
				}
				break;
			}
			case "30471-06.html":
			{
				if (!hasQuestItems(player, FRAGMENT_OF_ANCIENT_TABLET_1ST_PIECE, FRAGMENT_OF_ANCIENT_TABLET_2ND_PIECE, FRAGMENT_OF_ANCIENT_TABLET_3RD_PIECE, FRAGMENT_OF_ANCIENT_TABLET_4TH_PIECE))
				{
					htmltext = event;
				}
				else
				{
					if (getRandom(100) < 50)
					{
						giveItems(player, COMPLETE_ANCIENT_TABLET, 1);
						takeItems(player, FRAGMENT_OF_ANCIENT_TABLET_1ST_PIECE, 1);
						takeItems(player, FRAGMENT_OF_ANCIENT_TABLET_2ND_PIECE, 1);
						takeItems(player, FRAGMENT_OF_ANCIENT_TABLET_3RD_PIECE, 1);
						takeItems(player, FRAGMENT_OF_ANCIENT_TABLET_4TH_PIECE, 1);
						htmltext = "30471-07.html";
					}
					else
					{
						takeItems(player, FRAGMENT_OF_ANCIENT_TABLET_1ST_PIECE, 1);
						takeItems(player, FRAGMENT_OF_ANCIENT_TABLET_2ND_PIECE, 1);
						takeItems(player, FRAGMENT_OF_ANCIENT_TABLET_3RD_PIECE, 1);
						takeItems(player, FRAGMENT_OF_ANCIENT_TABLET_4TH_PIECE, 1);
						htmltext = "30471-08.html";
					}
				}
				break;
			}
			case "30531-04.html":
			{
				if (hasQuestItems(player, COMPLETE_ANCIENT_TABLET))
				{
					giveAdena(player, 30000, true);
					takeItems(player, COMPLETE_ANCIENT_TABLET, 1);
					htmltext = event;
				}
				break;
			}
			case "30736-03.html":
			{
				if ((getQuestItemsCount(player, CommonItem.ADENA) < 650) && ((getQuestItemsCount(player, CARGO_BOX_1ST) + getQuestItemsCount(player, CARGO_BOX_2ND) + getQuestItemsCount(player, CARGO_BOX_3RD) + getQuestItemsCount(player, CARGO_BOX_4TH)) >= 1))
				{
					htmltext = event;
				}
				else if ((getQuestItemsCount(player, CommonItem.ADENA) >= 650) && ((getQuestItemsCount(player, CARGO_BOX_1ST) + getQuestItemsCount(player, CARGO_BOX_2ND) + getQuestItemsCount(player, CARGO_BOX_3RD) + getQuestItemsCount(player, CARGO_BOX_4TH)) >= 1))
				{
					takeItems(player, CommonItem.ADENA, 650);
					if (hasQuestItems(player, CARGO_BOX_1ST))
					{
						takeItems(player, CARGO_BOX_1ST, 1);
					}
					else if (hasQuestItems(player, CARGO_BOX_2ND))
					{
						takeItems(player, CARGO_BOX_2ND, 1);
					}
					else if (hasQuestItems(player, CARGO_BOX_3RD))
					{
						takeItems(player, CARGO_BOX_3RD, 1);
					}
					else if (hasQuestItems(player, CARGO_BOX_4TH))
					{
						takeItems(player, CARGO_BOX_4TH, 1);
					}
					
					if (chance < 40)
					{
						if (chance1 < 33)
						{
							giveItems(player, GLUDIO_APPLES, 1);
							htmltext = "30736-04a.html";
						}
						else if (chance1 < 66)
						{
							giveItems(player, DION_CORN_MEAL, 1);
							htmltext = "30736-04b.html";
						}
						else
						{
							giveItems(player, DIRE_WOLF_PELTS, 1);
							htmltext = "30736-04c.html";
						}
					}
					else if (chance < 60)
					{
						if (chance1 < 33)
						{
							giveItems(player, MOONSTONE, 1);
							htmltext = "30736-04d.html";
						}
						else if (chance1 < 66)
						{
							giveItems(player, GLUDIO_WHEAT_FLOUR, 1);
							htmltext = "30736-04e.html";
						}
						else
						{
							giveItems(player, SPIDERSILK_ROPE, 1);
							htmltext = "30736-04f.html";
						}
					}
					else if (chance < 70)
					{
						if (chance1 < 33)
						{
							giveItems(player, ALEXANDRITE, 1);
							htmltext = "30736-04g.html";
						}
						else if (chance1 < 66)
						{
							giveItems(player, SILVER_TEA_SERVICE, 1);
							htmltext = "30736-04h.html";
						}
						else
						{
							giveItems(player, MECHANIC_GOLEM_SPACE_PARTS, 1);
							htmltext = "30736-04i.html";
						}
					}
					else if (chance < 75)
					{
						if (chance1 < 33)
						{
							giveItems(player, FIRE_EMERALD, 1);
							htmltext = "30736-04j.html";
						}
						else if (chance1 < 66)
						{
							giveItems(player, AVELLAN_SILK_FROCK, 1);
							htmltext = "30736-04k.html";
						}
						else
						{
							giveItems(player, FERIOTIC_PORCELAIN_URM, 1);
							htmltext = "30736-04l.html";
						}
					}
					else if (chance < 76)
					{
						giveItems(player, IMPERIAL_DIAMOND, 1);
						htmltext = "30736-04m.html";
					}
					else if (getRandom(100) < 50)
					{
						if (chance1 < 25)
						{
							giveItems(player, STATUE_OF_SHILEN_HEAD, 1);
						}
						else if (chance1 < 50)
						{
							giveItems(player, STATUE_OF_SHILEN_TORSO, 1);
						}
						else if (chance1 < 75)
						{
							giveItems(player, STATUE_OF_SHILEN_ARM, 1);
						}
						else
						{
							giveItems(player, STATUE_OF_SHILEN_LEG, 1);
						}
						htmltext = "30736-04n.html";
					}
					else
					{
						if (chance1 < 25)
						{
							giveItems(player, FRAGMENT_OF_ANCIENT_TABLET_1ST_PIECE, 1);
						}
						else if (chance1 < 50)
						{
							giveItems(player, FRAGMENT_OF_ANCIENT_TABLET_2ND_PIECE, 1);
						}
						else if (chance1 < 75)
						{
							giveItems(player, FRAGMENT_OF_ANCIENT_TABLET_3RD_PIECE, 1);
						}
						else
						{
							giveItems(player, FRAGMENT_OF_ANCIENT_TABLET_4TH_PIECE, 1);
						}
						htmltext = "30736-04o.html";
					}
				}
				else if ((getQuestItemsCount(player, CARGO_BOX_1ST) + getQuestItemsCount(player, CARGO_BOX_2ND) + getQuestItemsCount(player, CARGO_BOX_3RD) + getQuestItemsCount(player, CARGO_BOX_4TH)) < 1)
				{
					htmltext = "30736-05.html";
				}
				break;
			}
			case "30736-07.html":
			{
				if (player.getAdena() < (200 + (qs.getMemoState() * 200)))
				{
					htmltext = event;
				}
				else if ((qs.getMemoState() * 100) > 200)
				{
					htmltext = "30736-08.html";
				}
				else
				{
					if (chance < 5)
					{
						htmltext = "30736-08a.html";
					}
					else if (chance < 10)
					{
						htmltext = "30736-08b.html";
					}
					else if (chance < 15)
					{
						htmltext = "30736-08c.html";
					}
					else if (chance < 20)
					{
						htmltext = "30736-08d.html";
					}
					else if (chance < 25)
					{
						htmltext = "30736-08e.html";
					}
					else if (chance < 30)
					{
						htmltext = "30736-08f.html";
					}
					else if (chance < 35)
					{
						htmltext = "30736-08g.html";
					}
					else if (chance < 40)
					{
						htmltext = "30736-08h.html";
					}
					else if (chance < 45)
					{
						htmltext = "30736-08i.html";
					}
					else if (chance < 50)
					{
						htmltext = "30736-08j.html";
					}
					else if (chance < 55)
					{
						htmltext = "30736-08k.html";
					}
					else if (chance < 60)
					{
						htmltext = "30736-08l.html";
					}
					else if (chance < 65)
					{
						htmltext = "30736-08m.html";
					}
					else if (chance < 70)
					{
						htmltext = "30736-08n.html";
					}
					else if (chance < 75)
					{
						htmltext = "30736-08o.html";
					}
					else if (chance < 80)
					{
						htmltext = "30736-08p.html";
					}
					else if (chance < 85)
					{
						htmltext = "30736-08q.html";
					}
					else if (chance < 90)
					{
						htmltext = "30736-08r.html";
					}
					else if (chance < 95)
					{
						htmltext = "30736-08s.html";
					}
					else
					{
						htmltext = "30736-08t.html";
					}
					takeItems(player, CommonItem.ADENA, 200 + (qs.getMemoState() * 200));
					qs.setMemoState(qs.getMemoState() + 1);
				}
				break;
			}
			case "30737-06.html":
			{
				if ((getQuestItemsCount(player, CARGO_BOX_1ST) + getQuestItemsCount(player, CARGO_BOX_2ND) + getQuestItemsCount(player, CARGO_BOX_3RD) + getQuestItemsCount(player, CARGO_BOX_4TH)) < 1)
				{
					htmltext = event;
				}
				else
				{
					if (hasQuestItems(player, CARGO_BOX_1ST))
					{
						takeItems(player, CARGO_BOX_1ST, 1);
					}
					else if (hasQuestItems(player, CARGO_BOX_2ND))
					{
						takeItems(player, CARGO_BOX_2ND, 1);
					}
					else if (hasQuestItems(player, CARGO_BOX_3RD))
					{
						takeItems(player, CARGO_BOX_3RD, 1);
					}
					else if (hasQuestItems(player, CARGO_BOX_4TH))
					{
						takeItems(player, CARGO_BOX_4TH, 1);
					}
					
					if (getQuestItemsCount(player, GUILD_COIN) < 80)
					{
						giveItems(player, GUILD_COIN, 1);
					}
					else
					{
						takeItems(player, GUILD_COIN, 80);
					}
					
					if (getQuestItemsCount(player, GUILD_COIN) < 40)
					{
						giveAdena(player, 100, true);
						htmltext = "30737-03.html";
					}
					else if ((getQuestItemsCount(player, GUILD_COIN) >= 40) && (getQuestItemsCount(player, GUILD_COIN) < 80))
					{
						giveAdena(player, 200, true);
						htmltext = "30737-04.html";
					}
					else
					{
						giveAdena(player, 300, true);
						htmltext = "30737-05.html";
					}
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
				case MARSH_STAKATO:
				{
					if (hasQuestItems(killer, SOPHYAS_4TH_ORDER))
					{
						if (getRandom(100) < 55)
						{
							giveItems(killer, STAKATO_TALON, 1);
						}
						if (getRandom(100) < 12)
						{
							giveItems(killer, CARGO_BOX_4TH, 1);
						}
						if ((getRandom(100) < 2) && hasQuestItems(killer, SOPHYAS_4TH_ORDER))
						{
							addSpawn(MARSH_STAKATO_MARQUESS, npc, true, 0, false);
						}
					}
					break;
				}
				case NEER_CRAWLER:
				{
					if (hasQuestItems(killer, SOPHYAS_1ST_ORDER))
					{
						if (getRandom(2) == 0)
						{
							giveItems(killer, UNDEAD_ASH, 1);
						}
						if (getRandom(100) < 11)
						{
							giveItems(killer, CARGO_BOX_1ST, 1);
						}
					}
					break;
				}
				case SPECTER:
				{
					if (hasQuestItems(killer, SOPHYAS_1ST_ORDER))
					{
						if (getRandom(100) < 60)
						{
							giveItems(killer, UNDEAD_ASH, 1);
						}
						if (getRandom(100) < 8)
						{
							giveItems(killer, CARGO_BOX_1ST, 1);
						}
					}
					break;
				}
				case SORROW_MAIDEN:
				{
					if (hasQuestItems(killer, SOPHYAS_1ST_ORDER))
					{
						if (getRandom(100) < 60)
						{
							giveItems(killer, UNDEAD_ASH, 1);
						}
						if (getRandom(100) < 9)
						{
							giveItems(killer, CARGO_BOX_1ST, 1);
						}
					}
					break;
				}
				case NEER_CRAWLER_BERSERKER:
				{
					if (hasQuestItems(killer, SOPHYAS_1ST_ORDER))
					{
						if (getRandom(2) == 0)
						{
							giveItems(killer, UNDEAD_ASH, 1);
						}
						if (getRandom(100) < 12)
						{
							giveItems(killer, CARGO_BOX_1ST, 1);
						}
					}
					break;
				}
				case STRAIN:
				{
					if (hasQuestItems(killer, SOPHYAS_1ST_ORDER))
					{
						if (getRandom(2) == 0)
						{
							giveItems(killer, UNDEAD_ASH, 1);
						}
						if (getRandom(100) < 13)
						{
							giveItems(killer, CARGO_BOX_1ST, 1);
						}
					}
					break;
				}
				case GHOUL:
				{
					if (hasQuestItems(killer, SOPHYAS_1ST_ORDER))
					{
						if (getRandom(2) == 0)
						{
							giveItems(killer, UNDEAD_ASH, 1);
						}
						if (getRandom(100) < 15)
						{
							giveItems(killer, CARGO_BOX_1ST, 1);
						}
					}
					break;
				}
				case OL_MAHUM_GUERILLA:
				{
					if (hasQuestItems(killer, SOPHYAS_2ND_ORDER))
					{
						if (getRandom(2) == 0)
						{
							giveItems(killer, BLOODY_AXE_INSIGNIA, 1);
						}
						if (getRandom(100) < 9)
						{
							giveItems(killer, CARGO_BOX_2ND, 1);
						}
					}
					break;
				}
				case OL_MAHUM_RAIDER:
				{
					if (hasQuestItems(killer, SOPHYAS_2ND_ORDER))
					{
						if (getRandom(2) == 0)
						{
							giveItems(killer, BLOODY_AXE_INSIGNIA, 1);
						}
						if (getRandom(100) < 10)
						{
							giveItems(killer, CARGO_BOX_2ND, 1);
						}
					}
					break;
				}
				case OL_MAHUM_MARKSMAN:
				{
					if (hasQuestItems(killer, SOPHYAS_2ND_ORDER))
					{
						if (getRandom(2) == 0)
						{
							giveItems(killer, BLOODY_AXE_INSIGNIA, 1);
						}
						if (getRandom(100) < 11)
						{
							giveItems(killer, CARGO_BOX_2ND, 1);
						}
					}
					break;
				}
				case OL_MAHUM_SERGEANT:
				{
					if (hasQuestItems(killer, SOPHYAS_2ND_ORDER))
					{
						if (getRandom(2) == 0)
						{
							giveItems(killer, BLOODY_AXE_INSIGNIA, 1);
						}
						if (getRandom(100) < 12)
						{
							giveItems(killer, CARGO_BOX_2ND, 1);
						}
					}
					break;
				}
				case OL_MAHUM_CAPTAIN:
				{
					if (hasQuestItems(killer, SOPHYAS_2ND_ORDER))
					{
						if (getRandom(2) == 0)
						{
							giveItems(killer, BLOODY_AXE_INSIGNIA, 1);
						}
						if (getRandom(100) < 13)
						{
							giveItems(killer, CARGO_BOX_2ND, 1);
						}
					}
					break;
				}
				case MARSH_STAKATO_WORKER:
				{
					if (hasQuestItems(killer, SOPHYAS_4TH_ORDER))
					{
						if (getRandom(100) < 60)
						{
							giveItems(killer, STAKATO_TALON, 1);
						}
						if (getRandom(100) < 13)
						{
							giveItems(killer, CARGO_BOX_4TH, 1);
						}
						if ((getRandom(100) < 2) && hasQuestItems(killer, SOPHYAS_4TH_ORDER))
						{
							addSpawn(MARSH_STAKATO_MARQUESS, npc, true, 0, false);
						}
					}
					break;
				}
				case MARSH_STAKATO_SOLDIER:
				{
					if (hasQuestItems(killer, SOPHYAS_4TH_ORDER))
					{
						if (getRandom(100) < 56)
						{
							giveItems(killer, STAKATO_TALON, 1);
						}
						if (getRandom(100) < 14)
						{
							giveItems(killer, CARGO_BOX_4TH, 1);
						}
						if ((getRandom(100) < 2) && hasQuestItems(killer, SOPHYAS_4TH_ORDER))
						{
							addSpawn(MARSH_STAKATO_MARQUESS, npc, true, 0, false);
						}
					}
					break;
				}
				case MARSH_STAKATO_DRONE:
				{
					if (hasQuestItems(killer, SOPHYAS_4TH_ORDER))
					{
						if (getRandom(100) < 60)
						{
							giveItems(killer, STAKATO_TALON, 1);
						}
						if (getRandom(100) < 15)
						{
							giveItems(killer, CARGO_BOX_4TH, 1);
						}
						if ((getRandom(100) < 2) && hasQuestItems(killer, SOPHYAS_4TH_ORDER))
						{
							addSpawn(MARSH_STAKATO_MARQUESS, npc, true, 0, false);
						}
					}
					break;
				}
				case DELU_LIZARDMAN:
				case DELU_LIZARDMAN_SCOUT:
				{
					if (hasQuestItems(killer, SOPHYAS_3RD_ORDER))
					{
						if (getRandom(2) == 0)
						{
							giveItems(killer, DELU_LIZARDMAN_FANG, 1);
						}
						if (getRandom(100) < 14)
						{
							giveItems(killer, CARGO_BOX_3RD, 1);
						}
					}
					if ((getRandom(100) < 3) && hasQuestItems(killer, SOPHYAS_3RD_ORDER))
					{
						addSpawn(DELU_LIZARDMAN_HEADHUNTER, npc, true, 0, false);
						addSpawn(DELU_LIZARDMAN_HEADHUNTER, npc, true, 0, false);
					}
					break;
				}
				case DELU_LIZARDMAN_WARRIOR:
				{
					if (hasQuestItems(killer, SOPHYAS_3RD_ORDER))
					{
						if (getRandom(2) == 0)
						{
							giveItems(killer, DELU_LIZARDMAN_FANG, 1);
						}
						if (getRandom(100) < 15)
						{
							giveItems(killer, CARGO_BOX_3RD, 1);
						}
					}
					if ((getRandom(100) < 3) && hasQuestItems(killer, SOPHYAS_3RD_ORDER))
					{
						addSpawn(DELU_LIZARDMAN_HEADHUNTER, npc, true, 0, false);
						addSpawn(DELU_LIZARDMAN_HEADHUNTER, npc, true, 0, false);
					}
					break;
				}
				case DELU_LIZARDMAN_HEADHUNTER:
				{
					if (hasQuestItems(killer, SOPHYAS_3RD_ORDER))
					{
						giveItems(killer, DELU_LIZARDMAN_FANG, 4);
						playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case MARSH_STAKATO_MARQUESS:
				{
					if (hasQuestItems(killer, SOPHYAS_4TH_ORDER))
					{
						giveItems(killer, STAKATO_TALON, 8);
						playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_ITEMGET);
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
			if (npc.getId() == MERCENARY_CAPTAIN_SOPHYA)
			{
				if (player.getLevel() < MIN_LEVEL)
				{
					htmltext = "30735-01.htm";
				}
				else
				{
					if (!hasQuestItems(player, BLACK_LION_MARK))
					{
						htmltext = "30735-02.htm";
					}
					else
					{
						htmltext = "30735-03.htm";
					}
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case MERCENARY_CAPTAIN_SOPHYA:
				{
					if ((getQuestItemsCount(player, SOPHYAS_1ST_ORDER) + getQuestItemsCount(player, SOPHYAS_2ND_ORDER) + getQuestItemsCount(player, SOPHYAS_3RD_ORDER) + getQuestItemsCount(player, SOPHYAS_4TH_ORDER)) == 0)
					{
						htmltext = "30735-14.html";
					}
					else if (((getQuestItemsCount(player, SOPHYAS_1ST_ORDER) + getQuestItemsCount(player, SOPHYAS_2ND_ORDER) + getQuestItemsCount(player, SOPHYAS_3RD_ORDER) + getQuestItemsCount(player, SOPHYAS_4TH_ORDER)) == 1) && ((getQuestItemsCount(player, UNDEAD_ASH) + getQuestItemsCount(player, BLOODY_AXE_INSIGNIA) + getQuestItemsCount(player, DELU_LIZARDMAN_FANG) + getQuestItemsCount(player, STAKATO_TALON)) < 1) && ((getQuestItemsCount(player, CARGO_BOX_1ST) + getQuestItemsCount(player, CARGO_BOX_2ND) + getQuestItemsCount(player, CARGO_BOX_3RD) + getQuestItemsCount(player, CARGO_BOX_4TH)) < 1))
					{
						htmltext = "30735-15.html";
					}
					else if (((getQuestItemsCount(player, SOPHYAS_1ST_ORDER) + getQuestItemsCount(player, SOPHYAS_2ND_ORDER) + getQuestItemsCount(player, SOPHYAS_3RD_ORDER) + getQuestItemsCount(player, SOPHYAS_4TH_ORDER)) == 1) && ((getQuestItemsCount(player, UNDEAD_ASH) + getQuestItemsCount(player, BLOODY_AXE_INSIGNIA) + getQuestItemsCount(player, DELU_LIZARDMAN_FANG) + getQuestItemsCount(player, STAKATO_TALON)) < 1) && ((getQuestItemsCount(player, CARGO_BOX_1ST) + getQuestItemsCount(player, CARGO_BOX_2ND) + getQuestItemsCount(player, CARGO_BOX_3RD) + getQuestItemsCount(player, CARGO_BOX_4TH)) >= 1))
					{
						htmltext = "30735-15a.html";
					}
					else if (((getQuestItemsCount(player, SOPHYAS_1ST_ORDER) + getQuestItemsCount(player, SOPHYAS_2ND_ORDER) + getQuestItemsCount(player, SOPHYAS_3RD_ORDER) + getQuestItemsCount(player, SOPHYAS_4TH_ORDER)) == 1) && ((getQuestItemsCount(player, UNDEAD_ASH) + getQuestItemsCount(player, BLOODY_AXE_INSIGNIA) + getQuestItemsCount(player, DELU_LIZARDMAN_FANG) + getQuestItemsCount(player, STAKATO_TALON)) >= 1) && ((getQuestItemsCount(player, CARGO_BOX_1ST) + getQuestItemsCount(player, CARGO_BOX_2ND) + getQuestItemsCount(player, CARGO_BOX_3RD) + getQuestItemsCount(player, CARGO_BOX_4TH)) == 0))
					{
						final long itemcount = getQuestItemsCount(player, UNDEAD_ASH) + getQuestItemsCount(player, BLOODY_AXE_INSIGNIA) + getQuestItemsCount(player, DELU_LIZARDMAN_FANG) + getQuestItemsCount(player, STAKATO_TALON);
						if (itemcount < 20)
						{
							
						}
						else if (itemcount < 50)
						{
							giveItems(player, LIONS_CLAW, 1);
						}
						else if (itemcount < 100)
						{
							giveItems(player, LIONS_CLAW, 2);
						}
						else
						{
							giveItems(player, LIONS_CLAW, 3);
						}
						final long ash = getQuestItemsCount(player, UNDEAD_ASH);
						final long insignia = getQuestItemsCount(player, BLOODY_AXE_INSIGNIA);
						final long fang = getQuestItemsCount(player, DELU_LIZARDMAN_FANG);
						final long talon = getQuestItemsCount(player, STAKATO_TALON);
						giveAdena(player, ((ash * 10) + (insignia * 10) + (((fang + 7) + (talon * 8)))), true);
						takeItems(player, UNDEAD_ASH, -1);
						takeItems(player, BLOODY_AXE_INSIGNIA, -1);
						takeItems(player, DELU_LIZARDMAN_FANG, -1);
						takeItems(player, STAKATO_TALON, -1);
						qs.setMemoState(0);
						htmltext = "30735-22.html";
					}
					else if (((getQuestItemsCount(player, SOPHYAS_1ST_ORDER) + getQuestItemsCount(player, SOPHYAS_2ND_ORDER) + getQuestItemsCount(player, SOPHYAS_3RD_ORDER) + getQuestItemsCount(player, SOPHYAS_4TH_ORDER)) == 1) && ((getQuestItemsCount(player, UNDEAD_ASH) + getQuestItemsCount(player, BLOODY_AXE_INSIGNIA) + getQuestItemsCount(player, DELU_LIZARDMAN_FANG) + getQuestItemsCount(player, STAKATO_TALON)) >= 1) && ((getQuestItemsCount(player, CARGO_BOX_1ST) + getQuestItemsCount(player, CARGO_BOX_2ND) + getQuestItemsCount(player, CARGO_BOX_3RD) + getQuestItemsCount(player, CARGO_BOX_4TH)) >= 1))
					{
						final long itemcount = getQuestItemsCount(player, UNDEAD_ASH) + getQuestItemsCount(player, BLOODY_AXE_INSIGNIA) + getQuestItemsCount(player, DELU_LIZARDMAN_FANG) + getQuestItemsCount(player, STAKATO_TALON);
						if (itemcount < 20)
						{
							
						}
						else if (itemcount < 50)
						{
							giveItems(player, LIONS_CLAW, 1);
						}
						else if (itemcount < 100)
						{
							giveItems(player, LIONS_CLAW, 2);
						}
						else
						{
							giveItems(player, LIONS_CLAW, 3);
						}
						giveAdena(player, (getQuestItemsCount(player, UNDEAD_ASH) * 10), true);
						giveAdena(player, (getQuestItemsCount(player, BLOODY_AXE_INSIGNIA) * 10), true);
						giveAdena(player, (getQuestItemsCount(player, DELU_LIZARDMAN_FANG) * 7), true);
						giveAdena(player, (getQuestItemsCount(player, STAKATO_TALON) * 8), true);
						takeItems(player, UNDEAD_ASH, -1);
						takeItems(player, BLOODY_AXE_INSIGNIA, -1);
						takeItems(player, DELU_LIZARDMAN_FANG, -1);
						takeItems(player, STAKATO_TALON, -1);
						qs.setMemoState(0);
						htmltext = "30735-23.html";
					}
					break;
				}
				case ABYSSAL_CELEBRANT_UNDRIAS:
				{
					if (!hasQuestItems(player, COMPLETE_STATUE_OF_SHILEN))
					{
						if ((getQuestItemsCount(player, STATUE_OF_SHILEN_HEAD) + getQuestItemsCount(player, STATUE_OF_SHILEN_TORSO) + getQuestItemsCount(player, STATUE_OF_SHILEN_ARM) + getQuestItemsCount(player, STATUE_OF_SHILEN_LEG)) >= 1)
						{
							htmltext = "30130-02.html";
						}
						else
						{
							htmltext = "30130-01.html";
						}
					}
					else
					{
						htmltext = "30130-03.html";
					}
					break;
				}
				case BLACKSMITH_RUPIO:
				{
					if (((getQuestItemsCount(player, STATUE_OF_SHILEN_HEAD) + getQuestItemsCount(player, STATUE_OF_SHILEN_TORSO) + getQuestItemsCount(player, STATUE_OF_SHILEN_ARM) + getQuestItemsCount(player, STATUE_OF_SHILEN_LEG)) >= 1) || ((getQuestItemsCount(player, FRAGMENT_OF_ANCIENT_TABLET_1ST_PIECE) + getQuestItemsCount(player, FRAGMENT_OF_ANCIENT_TABLET_2ND_PIECE) + getQuestItemsCount(player, FRAGMENT_OF_ANCIENT_TABLET_3RD_PIECE) + getQuestItemsCount(player, FRAGMENT_OF_ANCIENT_TABLET_4TH_PIECE)) >= 1))
					{
						htmltext = "30471-02.html";
					}
					else
					{
						htmltext = "30471-01.html";
					}
					break;
				}
				case IRON_GATES_LOCKIRIN:
				{
					if (!hasQuestItems(player, COMPLETE_ANCIENT_TABLET))
					{
						if ((getQuestItemsCount(player, FRAGMENT_OF_ANCIENT_TABLET_1ST_PIECE) + getQuestItemsCount(player, FRAGMENT_OF_ANCIENT_TABLET_2ND_PIECE) + getQuestItemsCount(player, FRAGMENT_OF_ANCIENT_TABLET_3RD_PIECE) + getQuestItemsCount(player, FRAGMENT_OF_ANCIENT_TABLET_4TH_PIECE)) >= 1)
						{
							htmltext = "30531-02.html";
						}
						else
						{
							htmltext = "30531-01.html";
						}
					}
					else
					{
						htmltext = "30531-03.html";
					}
					break;
				}
				case MERCENARY_REEDFOOT:
				{
					if ((getQuestItemsCount(player, CARGO_BOX_1ST) + getQuestItemsCount(player, CARGO_BOX_2ND) + getQuestItemsCount(player, CARGO_BOX_3RD) + getQuestItemsCount(player, CARGO_BOX_4TH)) >= 1)
					{
						htmltext = "30736-02.html";
					}
					else
					{
						htmltext = "30736-01.html";
					}
					break;
				}
				case GUILDSMAN_MORGON:
				{
					if ((getQuestItemsCount(player, CARGO_BOX_1ST) + getQuestItemsCount(player, CARGO_BOX_2ND) + getQuestItemsCount(player, CARGO_BOX_3RD) + getQuestItemsCount(player, CARGO_BOX_4TH)) >= 1)
					{
						htmltext = "30737-02.html";
					}
					else
					{
						htmltext = "30737-01.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
}
