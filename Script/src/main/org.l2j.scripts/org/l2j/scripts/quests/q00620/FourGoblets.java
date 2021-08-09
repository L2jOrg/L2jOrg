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

	private static final int[][] ARMOR_PARTS_CHANCES = {{88, 6698}, {185, 6699 }, {238, 6670}, {262, 6671}, {292, 6672}, {356, 6673}, {420, 6674}, {482, 6675}, {554, 6676}, {576, 6677}, {640, 6678}, {704, 6679}, {777, 6680}, {799, 6681}, {863, 6682}, {927, 6683}, {1000, 6684}};
	private static final int[][] WEAPON_PARTS_CHANCES = { {100, 6688}, {198, 6689}, {298, 6690}, {398, 6691}, {499, 7579}, {601, 6693}, {703, 6695}, {801, 6696}, {902, 6697}, {1000, 6698}};
	private static final int[][] ARMOR_ENCHANT_CHANCES = { {223, 730}, {893, 948}, {1000, 960} };
	private static final int[][] WEAPON_ENCHANT_CHANCES = { {202, 729}, {928, 967}, {1000, 959} };
	private static final int[][] CRAFT_MATERIAL_CHANCES = { {148, 1878}, {175, 1882}, {273, 1879}, {322, 1880}, {357, 1885}, {554, 1875}, {685, 1883}, {803, 5220}, {901, 4039}, {1000, 4044}};
	private static final int[][] CRAFT_STONES_CHANCES = { {350, 1887}, {587, 4042}, {798, 1886}, {922, 4041}, {966, 1892}, {996, 1891}, {1000, 4047}};
	private static final int[][] MATERIAL_CHANCES = {{43, 1884}, {66, 1895}, {184, 1876}, {250, 1881}, {287, 5549}, {484, 1874}, {681, 1889}, {799, 1877}, {902, 1894}, {1000, 4043}};
	private static final int[][] SYNTHETIC_MATERIAL_CHANCES = {{335, 1888}, {556, 4040}, {725, 1890}, {872, 5550}, {962, 1893}, {986, 4046}, {1000, 4048}};


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
			case "accept" -> htmlText = onAccept(player, qs);
			case "11" -> htmlText = tryOpenSealedBox(player, htmlText);
			case "19" -> htmlText = onGhostOfChamberlianOpenBox(player, htmlText);
			case "12" -> htmlText = checkHasAllGoblets(player, qs);
			case "13" -> htmlText = finishQuest(qs);
			case "14" -> htmlText = qs.getCond() == 2 ? "31453-19.htm" : "31453-13.htm";
			case "15" -> htmlText = onTeleport15(player, qs);
			case "16" -> htmlText = onTeleport16(player, qs);
			case "17" -> htmlText = onEscape(player, htmlText, qs);
			case "18" -> htmlText = checkGobletsAmount(player, htmlText);
			case "6881", "6899", "6897", "6895", "6893", "6891", "7580", "6887", "6885", "6883" -> htmlText = changeRelic(event, player, qs);
		}
		return htmlText;
	}

	private String changeRelic(String event, Player player, QuestState qs) {
		String htmlText;
		takeItems(player, RELIC, 1000);
		giveItems(player, qs.getInt(event), 1);
		htmlText = "31454-17.htm";
		return htmlText;
	}

	private String onTeleport16(Player player, QuestState qs) {
		String htmlText;
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
		return htmlText;
	}

	private String onTeleport15(Player player, QuestState qs) {
		String htmlText;
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
		return htmlText;
	}

	private String checkGobletsAmount(Player player, String htmlText) {
		if ((getQuestItemsCount(player, GOBLETS[0]) + getQuestItemsCount(player, GOBLETS[1]) + getQuestItemsCount(player, GOBLETS[2]) + getQuestItemsCount(player, GOBLETS[3])) < 3) {
			htmlText = "31452-3.htm";
		} else if ((getQuestItemsCount(player, GOBLETS[0]) + getQuestItemsCount(player, GOBLETS[1]) + getQuestItemsCount(player, GOBLETS[2]) + getQuestItemsCount(player, GOBLETS[3])) == 3) {
			htmlText = "31452-4.htm";
		} else if ((getQuestItemsCount(player, GOBLETS[0]) + getQuestItemsCount(player, GOBLETS[1]) + getQuestItemsCount(player, GOBLETS[2]) + getQuestItemsCount(player, GOBLETS[3])) >= 4) {
			htmlText = "31452-5.htm";
		}
		return htmlText;
	}

	private String onEscape(Player player, String htmlText, QuestState qs) {
		if (getQuestItemsCount(player, ANTIQUE_BROOCH) >= 1) {
			qs.getPlayer().teleToLocation(169590, -90218, -2914);
		} else {
			takeItems(player, GRAVE_PASS, 1);
			qs.getPlayer().teleToLocation(169590, -90218, -2914);
			htmlText = "31452-6.htm";
		}
		return htmlText;
	}

	private String finishQuest(QuestState qs) {
		String htmlText;
		qs.exitQuest(true, true);
		htmlText = "31453-18.htm";
		return htmlText;
	}

	private String checkHasAllGoblets(Player player, QuestState qs) {
		String htmlText;
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
		return htmlText;
	}

	private String onGhostOfChamberlianOpenBox(Player player, String htmlText) {
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
		return htmlText;
	}

	private String tryOpenSealedBox(Player player, String htmlText) {
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
		return htmlText;
	}

	private String onAccept(Player player, QuestState qs) {
		String htmlText;
		if ((qs.getPlayer().getLevel() >= 74) && ((qs.getPlayer().getLevel() <= 80))) {
			qs.startQuest();
			htmlText = "31453-13.htm";
			giveItems(player, ENTRANCE_PASS, 1);
		} else {
			htmlText = "31453-12.htm";
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
			giveRandomChancedItem(player, ARMOR_PARTS_CHANCES);
			return true;
		} else if (Rnd.get(1000) < 54) {
			giveRandomChancedItem(player, WEAPON_PARTS_CHANCES);
			return true;
		}
		return false;
	}

	private void giveRandomChancedItem(Player player, int[][] itemsChances) {
		var chance = Rnd.get(1000);
		for (int[] armorPartsChance : itemsChances) {
			if(chance < armorPartsChance[0]) {
				giveItems(player, armorPartsChance[1], 1);
				break;
			}
		}
	}

	private boolean giveRandomEnchantmentScroll(Player player) {
		if (Rnd.get(1000) < 31) {
			giveRandomChancedItem(player, ARMOR_ENCHANT_CHANCES);
			return true;
		} else if (Rnd.get(1000) < 50) {
			giveRandomChancedItem(player, WEAPON_ENCHANT_CHANCES);
			return true;
		}
		return false;
	}

	private boolean giveRandomCraftMaterial(Player player) {
		if (Rnd.get(1000) < 847) {
			giveRandomChancedItem(player, CRAFT_MATERIAL_CHANCES);
			return true;
		} else if (Rnd.get(1000) < 251) {
			giveRandomChancedItem(player, CRAFT_STONES_CHANCES);
			return true;
		}
		return false;
	}

	private boolean giveRandomMaterial(Player player) {
		if (Rnd.get(1000) < 848) {
			giveRandomChancedItem(player, MATERIAL_CHANCES);
			return true;
		} else if (Rnd.get(1000) < 323) {
			giveRandomChancedItem(player, SYNTHETIC_MATERIAL_CHANCES);
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
