/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.scripts.quests.Q00933_ExploringTheWestWingOfTheDungeonOfAbyss;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.QuestType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

import static java.util.Objects.isNull;

/**
 * @author Bru7aLMike
 */
public class Q00933_ExploringTheWestWingOfTheDungeonOfAbyss extends Quest {

    private static final ItemHolder REMNANT_ASHES = new ItemHolder(90008, 1);
    private static final ItemHolder DIMENSIONAL_GIFT = new ItemHolder(90136, 1);

    private static final int MIN_LEVEL = 40;
    private static final int MAX_LEVEL = 44;
    private static final int MAGRIT = 31774;
    private static final int INGRIT = 31775;
    private static final int WANDERING_DEAD = 21638;
    private static final int WANDERING_SPIRIT = 21639;
    private static final int WANDERING_GHOST = 21640;
    private static final int WANDERING_EVIL = 21641;

    public Q00933_ExploringTheWestWingOfTheDungeonOfAbyss() {
        super(933);
        addStartNpc(MAGRIT, INGRIT);
        addTalkId(MAGRIT, INGRIT);
        addKillId(WANDERING_DEAD, WANDERING_SPIRIT, WANDERING_GHOST, WANDERING_EVIL);
        registerQuestItems(REMNANT_ASHES.getId());
        setQuestNameNpcStringId(NpcStringId.LV_40_44_EXPLORING_THE_WEST_OUTSKIRTS_OF_THE_DUNGEON_OF_ABYSS);
        addCondMinLevel(MIN_LEVEL, "no_lvl.html");
        addCondMaxLevel(MAX_LEVEL, "no_lvl.html");
    }

    @Override
    public boolean checkPartyMember(Player member, Npc npc) {
        final QuestState qs = getQuestState(member, false);
        return ((qs != null) && qs.isStarted());
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        final QuestState qs = getQuestState(player, false);
        if (qs == null) {
            return null;
        }

        String htmltext = null;
        switch (event) {
            case "31774-01.htm", "31774-02.htm", "31774-03.htm", "31775-01.htm", "31775-02.htm", "31775-03.htm" -> htmltext = event;
            case "31774-04.htm", "31775-04.htm" -> {
                qs.startQuest();
                htmltext = event;
            }
            case "end.htm" -> {
                if (qs.isStarted()) {
                    addExpAndSp(player, 250000, 7700);
                    giveItems(player, DIMENSIONAL_GIFT);
                    qs.exitQuest(QuestType.DAILY, true);
                    htmltext = event;
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(Npc npc, Player player) {
        final QuestState qs = getQuestState(player, true);
        String htmltext = getNoQuestMsg(player);

        if (isNull(qs)) {
            return htmltext;
        }

        if (qs.isCreated()) {
            htmltext = "31774-01.htm";
        } else if (qs.isStarted()) {
            switch (npc.getId()) {
                case MAGRIT -> {
                    if (qs.isCond(2)) {
                        htmltext = "31774-05.htm";
                    } else {
                        htmltext = "31774-04.htm";
                    }
                }
                case INGRIT -> {
                    if (qs.isCond(2)) {
                        htmltext = "31775-05.htm";
                    } else {
                        htmltext = "31775-04.htm";
                    }
                }
            }
        } else if (qs.isCompleted()) {
            if (qs.isNowAvailable()) {
                switch (npc.getId()) {
                    case MAGRIT: {
                        htmltext = "31774-01.htm";
                        break;
                    }
                    case INGRIT: {
                        htmltext = "31775-01.htm";
                    }
                }
            } else {
                htmltext = getAlreadyCompletedMsg(player);
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        final QuestState qs = getQuestState(killer, false);
        if ((qs != null) && qs.isCond(1)) {
            if (getQuestItemsCount(killer, REMNANT_ASHES.getId()) < 50) {
                giveItems(killer, REMNANT_ASHES);
                playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
            } else {
                qs.setCond(2);
                killer.sendPacket(new ExShowScreenMessage("Magrit and Ingrit are awaiting your return!", 5000));
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
}