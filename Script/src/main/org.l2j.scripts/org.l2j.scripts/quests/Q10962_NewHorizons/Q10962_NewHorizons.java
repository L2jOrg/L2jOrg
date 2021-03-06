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
package org.l2j.scripts.quests.Q10962_NewHorizons;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.serverpackets.classchange.ExRequestClassChangeUi;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.falseIfNullOrElse;

/**
 * @author RobikBobik
 * @author JoeAlisson
 */
public final class Q10962_NewHorizons extends Quest {

	private static final int LEAHEN = 34111;
	private static final int CAPTAIN_BATHIS = 30332;

	private static final int MOUNTAIN_WEREWORLF = 21985;
	private static final int MOUNTAIN_FUNGUES = 21986;
	private static final int MUERTOS_WARRIOR = 21987;
	private static final int MUERTOS_CAPTAIN = 21988;

	private static final ItemHolder SOE_TO_CAPTAIN_BATHIS = new ItemHolder(91918, 1);
	private static final ItemHolder SOE_NOVICE = new ItemHolder(10650, 20);
	private static final ItemHolder SPIRIT_ORE = new ItemHolder(3031, 50);
	private static final ItemHolder HP_POTS = new ItemHolder(91912, 50); // TODO: Finish Item
	private static final ItemHolder RICE_CAKE_OF_FLAMING_FIGHTING_SPIRIT_EVENT = new ItemHolder(91840, 1);

	private static final ItemHolder MOON_HELMET = new ItemHolder(7850, 1);

	private static final ItemHolder MOON_ARMOR = new ItemHolder(7851, 1);
	private static final ItemHolder MOON_GAUNTLETS = new ItemHolder(7852, 1);
	private static final ItemHolder MOON_BOOTS = new ItemHolder(7853, 1);

	private static final ItemHolder MOON_SHELL = new ItemHolder(7854, 1);
	private static final ItemHolder MOON_LEATHER_GLOVES = new ItemHolder(7855, 1);
	private static final ItemHolder MOON_SHOES = new ItemHolder(7856, 1);

	private static final ItemHolder MOON_CAPE = new ItemHolder(7857, 1);
	private static final ItemHolder MOON_SILK = new ItemHolder(7858, 1);
	private static final ItemHolder MOON_SANDALS = new ItemHolder(7859, 1);

	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10962_NewHorizons() {
		super(10962);
		addCondClassIds(ClassId.JIN_KAMAEL_SOLDIER);
		addStartNpc(LEAHEN);
		addTalkId(LEAHEN, CAPTAIN_BATHIS);
		addKillId(MOUNTAIN_WEREWORLF, MOUNTAIN_FUNGUES, MUERTOS_WARRIOR, MUERTOS_CAPTAIN);
		addCondLevel(15, 20, "no_lvl.html");
		setQuestNameNpcStringId(NpcStringId.LV_15_20_NEW_HORIZONS);
	}
	
	@Override
	public boolean checkPartyMember(Player player, Npc npc) {
		return falseIfNullOrElse(getQuestState(player, false), QuestState::isStarted);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		final var state = getQuestState(player, false);
		if (isNull(state)) {
			return null;
		}
		
		return switch (event) {
			case "30332-01.html", "30332-02.html", "30332-03.html", "30332.html" -> event;
			case "NEXT_QUEST" -> "34111.htm";
			case "34111-01.html" -> {
				state.startQuest();
				yield event;
			}
			case "TELEPORT_TO_HUNTING_GROUND" -> {
				player.teleToLocation(-107827, 47535, -1448);
				yield null;
			}
			case "HeavyArmor.html" -> {
				if (state.isStarted()) {
					giveHeavyRewards(player);
					state.exitQuest(false, true);
					yield event;
				}
				yield null;
			}
			case "LightArmor.html" -> {
				if (state.isStarted()) {
					giveLightRewards(player);
					state.exitQuest(false, true);
					yield event;
				}
				yield null;
			}
			case "Robe.html" -> {
				if (state.isStarted()) {
					giveRobeRewards(player);
					state.exitQuest(false, true);
					yield event;
				}
				yield null;
			}
			default -> null;
		};
	}

	private void giveRobeRewards(Player player) {
		giveCommonRewards(player);
		giveItems(player, MOON_CAPE);
		giveItems(player, MOON_SILK);
		giveItems(player, MOON_SANDALS);
		checkClassChange(player);
	}

	private void checkClassChange(Player player) {
		if (CategoryManager.getInstance().isInCategory(CategoryType.FIRST_CLASS_GROUP, player.getClassId().getId())) {
			showOnScreenMsg(player, NpcStringId.YOU_VE_FINISHED_THE_TUTORIAL_NTAKE_YOUR_1ST_CLASS_TRANSFER_AND_COMPLETE_YOUR_TRAINING_WITH_BATHIS_TO_BECOME_STRONGER, ExShowScreenMessage.TOP_CENTER, 10000);
			player.sendPacket(ExRequestClassChangeUi.STATIC_PACKET);
		}
	}

	private void giveLightRewards(Player player) {
		giveCommonRewards(player);
		giveItems(player, MOON_SHELL);
		giveItems(player, MOON_LEATHER_GLOVES);
		giveItems(player, MOON_SHOES);
		checkClassChange(player);
	}

	private void giveHeavyRewards(Player player) {
		giveCommonRewards(player);
		giveItems(player, MOON_ARMOR);
		giveItems(player, MOON_GAUNTLETS);
		giveItems(player, MOON_BOOTS);
		checkClassChange(player);

	}

	private void giveCommonRewards(Player player) {
		addExpAndSp(player, 600000, 13500);
		giveItems(player, SOE_NOVICE);
		giveItems(player, SPIRIT_ORE);
		giveItems(player, HP_POTS);
		giveItems(player, RICE_CAKE_OF_FLAMING_FIGHTING_SPIRIT_EVENT);
		giveItems(player, MOON_HELMET);
	}

	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon) {
		final var state = getQuestState(killer, false);
		if (nonNull(state) && state.isCond(1)) {
			final int killCount = state.getInt(KILL_COUNT_VAR) + 1;
			if (killCount < 30) {
				state.set(KILL_COUNT_VAR, killCount);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				sendNpcLogList(killer);
			} else {
				state.setCond(2, true);
				state.unset(KILL_COUNT_VAR);
				killer.sendPacket(new ExShowScreenMessage("You hunted all monsters.#Use the Scroll of Escape in you inventory to go to Captain Bathis in the Town of Gludio.", 5000));
				giveItems(killer, SOE_TO_CAPTAIN_BATHIS);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(Player player) {
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1)) {
			final Set<NpcLogListHolder> holder = new HashSet<>();
			holder.add(new NpcLogListHolder(NpcStringId.DEFEAT_THE_MONSTERS_IN_THE_GOLDEN_HILLS.getId(), true, qs.getInt(KILL_COUNT_VAR)));
			return holder;
		}
		return super.getNpcLogList(player);
	}
	
	@Override
	public String onTalk(Npc npc, Player player) {
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);

		if(isNull(qs)) {
			return htmltext;
		}

		if (qs.isCreated()){
			htmltext = "34111.htm";
		}
		else if (qs.isStarted())
		{
			switch (npc.getId()) {
				case LEAHEN -> {
					if (qs.isCond(1)) {
						htmltext = "34111-01.html";
					}
				}
				case CAPTAIN_BATHIS -> {
					if (qs.isCond(2)) {
						htmltext = "30332.html";
					}
				}
			}
		}
		else if (qs.isCompleted()) {
			if (npc.getId() == LEAHEN) {
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLogin(OnPlayerLogin event) {
		if (Config.DISABLE_TUTORIAL) {
			return;
		}
		
		final Player player = event.getPlayer();
		if (player == null) {
			return;
		}
		
		if (!CategoryManager.getInstance().isInCategory(CategoryType.FIRST_CLASS_GROUP, player.getClassId().getId())) {
			return;
		}
		
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCompleted()) {
			player.sendPacket(ExRequestClassChangeUi.STATIC_PACKET);
		}
	}
}