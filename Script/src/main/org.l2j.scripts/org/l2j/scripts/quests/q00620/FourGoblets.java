/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.quests.q00620;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.util.MathUtil;

/**
 * @author sandman
 * @author JoeAlisson
 */
public class FourGoblets extends Quest {

	private static final int NAMELESS_SPIRIT = 31453;
	private static final int GHOST_OF_WIGOTH_1 = 31452;
	private static final int GHOST_OF_WIGOTH_2 = 31454;
	private static final int CONQ_SM = 31921;
	private static final int EMPER_SM = 31922;
	private static final int SAGES_SM = 31923;
	private static final int JUDGE_SM = 31924;
	private static final int GHOST_CHAMBERLAIN_1 = 31919;
	private static final int GHOST_CHAMBERLAIN_2 = 31920;

	private static final int[] NPCS = {
		NAMELESS_SPIRIT,
		GHOST_OF_WIGOTH_1,
		GHOST_OF_WIGOTH_2,
		CONQ_SM,
		EMPER_SM,
		SAGES_SM,
		JUDGE_SM,
		GHOST_CHAMBERLAIN_1,
		GHOST_CHAMBERLAIN_2
	};

	private static final int ANTIQUE_BROOCH = 7262;

	private static final int ENTRANCE_PASS = 7075;
	private static final int GRAVE_PASS = 7261;

	private static final int[] GOBLETS = {
		7256,
		7257,
		7258,
		7259
	};

	private static final int BOSS_1 = 25339;
	private static final int BOSS_2 = 25342;
	private static final int BOSS_3 = 25346;
	private static final int BOSS_4 = 25349;
	
	private static final int RELIC = 7254;
	private static final int SEALED_BOX = 7255;

	private static final int[] QI = {
		7256,
		7257,
		7258,
		7259,
	};

	private static final int MIN_LEVEL = 74;
	private static final int MAX_LEVEL = 80;
	
	public FourGoblets() {
		super(620);
		addStartNpc(NAMELESS_SPIRIT);
		addTalkId(NPCS);
		registerQuestItems(QI);
		addKillId(huntMonsters());
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "31453-12.htm");
	}

	private int[] huntMonsters() {
		var killIds =  new int[18256 - 18120 + 4];
		killIds[0] = BOSS_1;
		killIds[1] = BOSS_2;
		killIds[2] = BOSS_3;
		killIds[3] = BOSS_4;
		for (int i = 4; i < killIds.length; i++) {
			killIds[i] =  18120 + i - 4;
		}
		return killIds;
	}

	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		var htmlText = event;
		var qs = player.getQuestState(getName());
		if (qs == null) {
			return htmlText;
		}

		switch (event) {
			case "accept" -> {
				if ((qs.getPlayer().getLevel() >= 74) && ((qs.getPlayer().getLevel() <= 80))) {
					qs.startQuest();
					htmlText = "31453-13.htm";
					giveItems(player, ENTRANCE_PASS, 1);
				} else {
					htmlText = "31453-12.htm";
				}
			}
			case "11" -> {
				if (getQuestItemsCount(player, SEALED_BOX) >= 1) {
					htmlText = "31454-13.htm";
					if(!openSealedBox(player)) {
						if (Rnd.nextBoolean()) {
							htmlText = "31454-14.htm";
						} else {
							htmlText = "31454-15.htm";
						}
					}
				}
			}
			case "19" -> {
				if(getQuestItemsCount(player, SEALED_BOX) >= 1) {
					htmlText = "31919-3.htm";
					if(!openSealedBox(player)) {
						if (Rnd.nextBoolean()) {
							htmlText = "31919-4.htm";
						} else {
							htmlText = "31919-5.htm";
						}
					}
				}
			}
			case "12" -> {
				if (hasAllGoblets(player)) {
					takeItems(player, GOBLETS[0], -1);
					takeItems(player, GOBLETS[1], -1);
					takeItems(player, GOBLETS[2], -1);
					takeItems(player, GOBLETS[3], -1);
					if (getQuestItemsCount(player, ANTIQUE_BROOCH) < 1) {
						giveItems(player, ANTIQUE_BROOCH, 1);
					}
					qs.setCond(2, true);
					htmlText = "31453-16.htm";
				} else {
					htmlText = "31453-14.htm";
				}
			}
			case "13" -> {
				qs.exitQuest(true, true);
				htmlText = "31453-18.htm";
			}
			case "14" -> htmlText = qs.getCond() == 2 ? "31453-19.htm" : "31453-13.htm";
			case "15" -> {
				if (getQuestItemsCount(player, ANTIQUE_BROOCH) >= 1) {
					qs.getPlayer().teleToLocation(178298, -84574, -7216);
					htmlText = null;
				} else if (getQuestItemsCount(player, GRAVE_PASS) >= 1) {
					takeItems(player, GRAVE_PASS, 1);
					qs.getPlayer().teleToLocation(178298, -84574, -7216);
					htmlText = null;
				} else {
					htmlText = "31919-0.htm";
				}
			}
			case "16" -> {
				if (getQuestItemsCount(player, ANTIQUE_BROOCH) >= 1) {
					qs.getPlayer().teleToLocation(186942, -75602, -2834);
					htmlText = null;
				} else if (getQuestItemsCount(player, GRAVE_PASS) >= 1) {
					takeItems(player, GRAVE_PASS, 1);
					qs.getPlayer().teleToLocation(186942, -75602, -2834);
					htmlText = null;
				} else {
					htmlText = "31920-0.htm";
				}
			}
			case "17" -> {
				if (getQuestItemsCount(player, ANTIQUE_BROOCH) >= 1) {
					qs.getPlayer().teleToLocation(169590, -90218, -2914);
				} else {
					takeItems(player, GRAVE_PASS, 1);
					qs.getPlayer().teleToLocation(169590, -90218, -2914);
					htmlText = "31452-6.htm";
				}
			}
			case "18" -> {
				if ((getQuestItemsCount(player, GOBLETS[0]) + getQuestItemsCount(player, GOBLETS[1]) + getQuestItemsCount(player, GOBLETS[2]) + getQuestItemsCount(player, GOBLETS[3])) < 3) {
					htmlText = "31452-3.htm";
				} else if ((getQuestItemsCount(player, GOBLETS[0]) + getQuestItemsCount(player, GOBLETS[1]) + getQuestItemsCount(player, GOBLETS[2]) + getQuestItemsCount(player, GOBLETS[3])) == 3) {
					htmlText = "31452-4.htm";
				} else if ((getQuestItemsCount(player, GOBLETS[0]) + getQuestItemsCount(player, GOBLETS[1]) + getQuestItemsCount(player, GOBLETS[2]) + getQuestItemsCount(player, GOBLETS[3])) >= 4) {
					htmlText = "31452-5.htm";
				}
			}
			case "6881", "6899", "6897", "6895", "6893", "6891", "7580", "6887", "6885", "6883" -> {
				takeItems(player, RELIC, 1000);
				giveItems(player, qs.getInt(event), 1);
				htmlText = "31454-17.htm";
			}
		}
		return htmlText;
	}

	private boolean openSealedBox(Player player) {
		takeItems(player, SEALED_BOX, 1);
		var success = false;
		final int rnd = Rnd.get(5);
		if (rnd == 0) {
			giveItems(player, 57, 10000);
			success = true;
		} else if (rnd == 1) {
			success = giveRandomMaterial(player);
		} else if (rnd == 2) {
			success = giveRandomCraftMaterial(player);
		} else if (rnd == 3) {
			success = giveRandomEnchantmentScroll(player);
		} else if (rnd == 4) {
			success = giveRandomItemPart(player);
		}
		return success;
	}

	private boolean giveRandomItemPart(Player player) {
		if (Rnd.get(1000) < 329) {
			final int i = Rnd.get(1000);
			if (i < 88) {
				giveItems(player, 6698, 1);
			} else if (i < 185) {
				giveItems(player, 6699, 1);
			} else if (i < 238) {
				giveItems(player, 6700, 1);
			} else if (i < 262) {
				giveItems(player, 6701, 1);
			} else if (i < 292) {
				giveItems(player, 6702, 1);
			} else if (i < 356) {
				giveItems(player, 6703, 1);
			} else if (i < 420) {
				giveItems(player, 6704, 1);
			} else if (i < 482) {
				giveItems(player, 6705, 1);
			} else if (i < 554) {
				giveItems(player, 6706, 1);
			} else if (i < 576) {
				giveItems(player, 6707, 1);
			} else if (i < 640) {
				giveItems(player, 6708, 1);
			} else if (i < 704) {
				giveItems(player, 6709, 1);
			} else if (i < 777) {
				giveItems(player, 6710, 1);
			} else if (i < 799) {
				giveItems(player, 6711, 1);
			} else if (i < 863) {
				giveItems(player, 6712, 1);
			} else if (i < 927) {
				giveItems(player, 6713, 1);
			} else {
				giveItems(player, 6714, 1);
			}
			return true;
		} else if (Rnd.get(1000) < 54) {
			final int i = Rnd.get(1000);
			if (i < 100) {
				giveItems(player, 6688, 1);
			} else if (i < 198) {
				giveItems(player, 6689, 1);
			} else if (i < 298) {
				giveItems(player, 6690, 1);
			} else if (i < 398) {
				giveItems(player, 6691, 1);
			} else if (i < 499) {
				giveItems(player, 7579, 1);
			} else if (i < 601) {
				giveItems(player, 6693, 1);
			} else if (i < 703) {
				giveItems(player, 6694, 1);
			} else if (i < 801) {
				giveItems(player, 6695, 1);
			} else if (i < 902) {
				giveItems(player, 6696, 1);
			} else {
				giveItems(player, 6697, 1);
			}
			return true;
		}
		return false;
	}

	private boolean giveRandomEnchantmentScroll(Player player) {
		if (Rnd.get(1000) < 31) {
			final int i = Rnd.get(1000);
			if (i < 223) {
				giveItems(player, 730, 1);
			} else if (i < 893) {
				giveItems(player, 948, 1);
			} else {
				giveItems(player, 960, 1);
			}
			return true;
		} else if (Rnd.get(1000) < 50) {
			final int i = Rnd.get(1000);
			if (i < 202) {
				giveItems(player, 729, 1);
			} else if (i < 928) {
				giveItems(player, 947, 1);
			} else {
				giveItems(player, 959, 1);
			}
			return true;
		}
		return false;
	}

	private boolean giveRandomCraftMaterial(Player player) {
		if (Rnd.get(1000) < 847) {
			final int i = Rnd.get(1000);
			if (i < 148) {
				giveItems(player, 1878, 8);
			} else if (i < 175) {
				giveItems(player, 1882, 24);
			} else if (i < 273) {
				giveItems(player, 1879, 4);
			} else if (i < 322) {
				giveItems(player, 1880, 6);
			} else if (i < 357) {
				giveItems(player, 1885, 6);
			} else if (i < 554) {
				giveItems(player, 1875, 1);
			} else if (i < 685) {
				giveItems(player, 1883, 1);
			} else if (i < 803) {
				giveItems(player, 5220, 1);
			} else if (i < 901) {
				giveItems(player, 4039, 1);
			} else {
				giveItems(player, 4044, 1);
			}
			return true;
		} else if (Rnd.get(1000) < 251) {
			final int i = Rnd.get(1000);
			if (i < 350) {
				giveItems(player, 1887, 1);
			} else if (i < 587) {
				giveItems(player, 4042, 1);
			} else if (i < 798) {
				giveItems(player, 1886, 1);
			} else if (i < 922) {
				giveItems(player, 4041, 1);
			} else if (i < 966) {
				giveItems(player, 1892, 1);
			} else if (i < 996) {
				giveItems(player, 1891, 1);
			} else {
				giveItems(player, 4047, 1);
			}
			return true;
		}
		return false;
	}

	private boolean giveRandomMaterial(Player player) {
		if (Rnd.get(1000) < 848) {
			final int i = Rnd.get(1000);
			if (i < 43) {
				giveItems(player, 1884, 42);
			} else if (i < 66) {
				giveItems(player, 1895, 36);
			} else if (i < 184) {
				giveItems(player, 1876, 4);
			} else if (i < 250) {
				giveItems(player, 1881, 6);
			} else if (i < 287) {
				giveItems(player, 5549, 8);
			} else if (i < 484) {
				giveItems(player, 1874, 1);
			} else if (i < 681) {
				giveItems(player, 1889, 1);
			} else if (i < 799) {
				giveItems(player, 1877, 1);
			} else if (i < 902) {
				giveItems(player, 1894, 1);
			} else {
				giveItems(player, 4043, 1);
			}
			return true;
		} else if (Rnd.get(1000) < 323) {
			final int i = Rnd.get(1000);
			if (i < 335) {
				giveItems(player, 1888, 1);
			} else if (i < 556) {
				giveItems(player, 4040, 1);
			} else if (i < 725) {
				giveItems(player, 1890, 1);
			} else if (i < 872) {
				giveItems(player, 5550, 1);
			} else if (i < 962) {
				giveItems(player, 1893, 1);
			} else if (i < 986) {
				giveItems(player, 4046, 1);
			} else {
				giveItems(player, 4048, 1);
			}
			return true;
		}
		return false;
	}

	@Override
	public String onTalk(Npc npc, Player talker) {
		var qs = getQuestState(talker, true);

		return switch (npc.getId()) {
			case NAMELESS_SPIRIT -> onNamelessSpiritTalk(talker, qs);
			case GHOST_OF_WIGOTH_1 -> onWigoth1Talk(talker, qs);
			case GHOST_OF_WIGOTH_2 ->  onWigoth2Talk(talker);
			case CONQ_SM -> "31921-E.htm";
			case EMPER_SM -> "31922-E.htm";
			case SAGES_SM -> "31923-E.htm";
			case JUDGE_SM ->  "31924-E.htm";
			case GHOST_CHAMBERLAIN_1 ->  "31919-1.htm";
			default -> getNoQuestMsg(talker);
		};
	}

	private String onWigoth2Talk(Player talker) {
		String htmlText;
		if (getQuestItemsCount(talker, RELIC) >= 1000) {
			if (getQuestItemsCount(talker, SEALED_BOX) >= 1) {
				if (hasAllGoblets(talker)) {
					htmlText = "31454-4.htm";
				} else if (getQuestItemsCount(talker, GOBLETS) > 1) {
					htmlText = "31454-8.htm";
				} else {
					htmlText = "31454-12.htm";
				}
			} else if (hasAllGoblets(talker)) {
				htmlText = "31454-3.htm";
			} else if (getQuestItemsCount(talker, GOBLETS) > 1) {
				htmlText = "31454-7.htm";
			} else {
				htmlText = "31454-11.htm";
			}
		} else if (getQuestItemsCount(talker, SEALED_BOX) >= 1) {
			if (hasAllGoblets(talker)) {
				htmlText = "31454-2.htm";
			} else if (getQuestItemsCount(talker, GOBLETS) > 1) {
				htmlText = "31454-6.htm";
			} else {
				htmlText = "31454-10.htm";
			}
		} else if (hasAllGoblets(talker)) {
			htmlText = "31454-1.htm";
		} else if (getQuestItemsCount(talker, GOBLETS) > 1) {
			htmlText = "31454-5.htm";
		} else {
			htmlText = "31454-9.htm";
		}
		return htmlText;
	}

	private String onWigoth1Talk(Player talker, QuestState qs) {
		String htmlText = getNoQuestMsg(talker);
		if (qs.getCond() == 1) {
			var gobletsCount = getQuestItemsCount(talker, GOBLETS);
			if (gobletsCount == 1) {
				htmlText = "31452-01.html";
			} else if (gobletsCount > 1) {
				htmlText = "31452-02.html";
			}
		} else if (qs.getCond() == 2) {
			htmlText = "31452-02.html";
		}
		return htmlText;
	}

	private String onNamelessSpiritTalk(Player talker, QuestState qs) {
		String htmlText;
		if (qs.isCreated()) {
			if (talker.getLevel() >= 74 && talker.getLevel() <= 80) {
				htmlText = "31453-1.htm";
			} else {
				htmlText = "31453-12.htm";
			}
		} else if (qs.getCond() == 1) {
			htmlText = hasAllGoblets(talker) ? "31453-15.htm" : "31453-14.htm";
		} else if (qs.getCond() == 2) {
			htmlText = "31453-17.htm";
		} else {
			htmlText = getNoQuestMsg(talker);
		}
		return htmlText;
	}

	private boolean hasAllGoblets(Player talker) {
		for (int goblet : GOBLETS) {
			if(getQuestItemsCount(talker, goblet) < 1) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon) {
		var qs = killer.getQuestState(getName());
		var npcId = npc.getId();

		if (qs != null && qs.getCond() > 0 && npcId >= 18120 && npcId <= 18256) {
			if (Rnd.get(100) < 15) {
				giveItems(killer, SEALED_BOX, 1);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			if (getQuestItemsCount(killer, GRAVE_PASS) < 1) {
				giveItems(killer, GRAVE_PASS, 1);
			}
			if (getQuestItemsCount(killer, RELIC) < 1000) {
				giveItems(killer, RELIC, 1);
			}
		}

		var partyMember = getRandomPartyMember(killer, 3);

		switch (npc.getId()) {
			case BOSS_1 -> {
				if (!MathUtil.isInsideRadius3D(npc, partyMember, 1000) && (getQuestItemsCount(partyMember, GOBLETS[0]) < 1)) {
					giveItems(partyMember, GOBLETS[0], 1);
				}
			}
			case BOSS_2 -> {
				if (!MathUtil.isInsideRadius3D(npc, partyMember, 1000) && (getQuestItemsCount(partyMember, GOBLETS[1]) < 1)) {
					giveItems(partyMember, GOBLETS[1], 1);
				}
			}
			case BOSS_3 -> {
				if (!MathUtil.isInsideRadius3D(npc, partyMember, 1000) && (getQuestItemsCount(partyMember, GOBLETS[2]) < 1)) {
					giveItems(partyMember, GOBLETS[2], 1);
				}
			}
			case BOSS_4 -> {
				if (!MathUtil.isInsideRadius3D(npc, partyMember, 1000) && (getQuestItemsCount(partyMember, GOBLETS[3]) < 1)) {
					giveItems(partyMember, GOBLETS[3], 1);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
