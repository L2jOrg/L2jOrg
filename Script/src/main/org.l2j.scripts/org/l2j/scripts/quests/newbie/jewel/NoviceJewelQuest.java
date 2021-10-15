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
package org.l2j.scripts.quests.newbie.jewel;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.settings.PartySettings;

import java.util.Collection;
import java.util.Set;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

/**
 * @author JoeAlisson
 */
public abstract class NoviceJewelQuest extends Quest {

    private static final ItemHolder SOE_NOVICE = new ItemHolder(10650, 10);
    private static final ItemHolder RING_NOVICE = new ItemHolder(49041, 2);
    private static final ItemHolder EARRING_NOVICE = new ItemHolder(49040, 2);
    private static final ItemHolder NECKLACE_NOVICE = new ItemHolder(49039, 1);

    private static final int MAX_LEVEL = 20;
    private static final String QUEST_HUNT_PROGRESS = "hunt_progress";

    private final int newbieGuide;
    private final int trader;

    protected NoviceJewelQuest(int questId, int newbieGuide, int trader) {
        super(questId);
        addStartNpc(newbieGuide);
        addTalkId(newbieGuide, trader);
        addKillId(huntMonsters());
        addCondMaxLevel(MAX_LEVEL, "no_lvl.html");
        setQuestNameNpcStringId(questName());
        
        this.newbieGuide = newbieGuide;
        this.trader = trader;
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        final QuestState qs = getQuestState(player, false);
        if (qs == null) {
            return null;
        }
        
        if(event.equals(newbieGuide + "-02.htm")) {
            qs.startQuest();
            return event;
        }
        
        if(!qs.isStarted()) {
            return getNoQuestMsg(player);
        } else if("TELEPORT_TO_HUNTING_GROUND".equals(event)) {
            player.teleToLocation(huntingGroundLocation());
        } else if(event.equals(trader + "-02.htm")) {
            finishQuest(player, qs);
            var nextQuest = QuestManager.getInstance().getQuest(nextQuest());
            nextQuest.newQuestState(player);
            return nextQuest.getId() + ":" + event;
        }
        return null;
    }

    private void finishQuest(Player player, QuestState qs) {
        addExpAndSp(player, 260000, 6000);
        giveItems(player, SOE_NOVICE);
        giveItems(player, RING_NOVICE);
        giveItems(player, EARRING_NOVICE);
        giveItems(player, NECKLACE_NOVICE);
        qs.exitQuest(false, true);
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        if(processQuestKill(killer)) {
            notifyNearPartyMembers(killer, npc);
        }
        return super.onKill(npc, killer, isSummon);
    }

    private boolean processQuestKill(Player killer) {
        var qs = getQuestState(killer, false);
        if (qs != null && qs.isCond(1)) {
            var progress = qs.getInt(QUEST_HUNT_PROGRESS) + 1;
            qs.set(QUEST_HUNT_PROGRESS, progress);
            playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
            sendNpcLogList(killer, killCountLogList(qs));

            if (progress >= 20) {
                qs.setCond(2, true);
                qs.unset(QUEST_HUNT_PROGRESS);
                showOnScreenMsg(killer, finishHuntingMessage(), ExShowScreenMessage.TOP_CENTER, 5000);
                giveItems(killer, scrollEscapeToTrader(), 1);
            }
            return true;
        }
        return false;
    }

    private void notifyNearPartyMembers(Player player, Npc npc) {
        var party = player.getParty();
        if(party != null) {
            for (Player member : party.getMembers()) {
                if(member != player && isInsideRadius3D(member, npc, PartySettings.partyRange())) {
                    processQuestKill(member);
                }
            }
        }
    }

    @Override
    public Collection<NpcLogListHolder> getNpcLogList(Player player) {
        var qs = getQuestState(player, false);
        if (qs != null && qs.isCond(1)) {
            return killCountLogList(qs);
        }
        return super.getNpcLogList(player);
    }

    private Collection<NpcLogListHolder> killCountLogList(QuestState qs) {
        return Set.of(new NpcLogListHolder(questHuntingProgressName().getId(), true, qs.getInt(QUEST_HUNT_PROGRESS)));
    }

    @Override
    public String onTalk(Npc npc, Player player) {
        var qs = getQuestState(player, true);
        String htmltext = getNoQuestMsg(player);

        if(isNull(qs)) {
            return htmltext;
        }

        if (qs.isCreated()) {
            htmltext = newbieGuide + "-01.htm";
        } else if (qs.isStarted()) {
            if(npc.getId() == newbieGuide && qs.isCond(1)) {
                htmltext = newbieGuide + "-02.htm";
            } else if(npc.getId() == trader && qs.isCond(2)) {
                htmltext = trader + ".htm";
            }
        } else if (qs.isCompleted()) {
            if (npc.getId() == newbieGuide) {
                htmltext = getAlreadyCompletedMsg(player);
            }
        }
        return htmltext;
    }

    protected abstract int[] huntMonsters();

    protected abstract NpcStringId questName();

    protected abstract NpcStringId questHuntingProgressName();

    protected abstract Location huntingGroundLocation();

    protected abstract NpcStringId finishHuntingMessage();

    protected abstract int scrollEscapeToTrader();

    protected abstract String nextQuest();
}
