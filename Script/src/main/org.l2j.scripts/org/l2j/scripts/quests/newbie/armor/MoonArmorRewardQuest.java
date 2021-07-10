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
package org.l2j.scripts.quests.newbie.armor;

import io.github.joealisson.primitive.IntCollection;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.serverpackets.classchange.ExRequestClassChangeUi;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public abstract class MoonArmorRewardQuest extends Quest {

    private static final int CAPTAIN_BATHIS = 30332;

    private static final ItemHolder SOE_TO_CAPTAIN_BATHIS = new ItemHolder(91918, 1);
    private static final ItemHolder SOE_NOVICE = new ItemHolder(10650, 20);
    private static final ItemHolder SPIRIT_ORE = new ItemHolder(3031, 50);
    private static final ItemHolder HP_POTS = new ItemHolder(91912, 50);
    private static final ItemHolder RICE_CAKE_OF_FLAMING_FIGHTING_SPIRIT_EVENT = new ItemHolder(91840, 1);

    private static final int[] MOON_ARMOR_HEAVY = { 7850, 7851, 7852, 7853 };
    private static final int[] MOON_ARMOR_LIGHT = { 7850, 7854, 7855, 7856 };
    private static final int[] MOON_ARMOR_ROBE =  { 7850, 7857, 7858, 7859 };

    private static final String QUEST_PROGRESS = "progress";
    private final int startNpc;

    protected MoonArmorRewardQuest(int questId, int startNpc, ClassId... classIds) {
        super(questId);
        addCondClassIds(classIds);
        addStartNpc(startNpc);
        addTalkId(startNpc, CAPTAIN_BATHIS);
        addKillId(huntMonsters());
        addCondLevel(15, 20, "no_lvl.html");
        setQuestNameNpcStringId(questName());

        this.startNpc = startNpc;
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        var qs = getQuestState(player, false);
        if (qs == null) {
            return null;
        }

        if(event.equals(startNpc + "-01.html")) {
            qs.startQuest();
            return event;
        }

        if(!qs.isStarted()) {
            return getNoQuestMsg(player);
        }

        return processEvent(event, player, qs);
    }

    private String processEvent(String event, Player player, QuestState qs) {
        return switch (event) {
            case "30332-01.html", "30332-02.html", "30332-03.html", "30332.html" -> event;
            case "TELEPORT_TO_HUNTING_GROUND" -> {
                player.teleToLocation(huntingGroundLocation());
               yield null;
            }
            case "NEXT_QUEST" -> startNpc + ".htm";
            case "HeavyArmor.html" -> {
                finishQuest(player, qs, MOON_ARMOR_HEAVY);
                yield "../HeavyArmor.html";
            }
            case "LightArmor.html" -> {
                finishQuest(player, qs, MOON_ARMOR_LIGHT);
                yield "../LightArmor.html";
            }
            case "Robe.html" -> {
                finishQuest(player, qs, MOON_ARMOR_ROBE);
                yield "../Robe.html";
            }
            default -> null;
        };
    }

    private void finishQuest(Player player, QuestState qs, int[] armorReward) {
        for (int piece : armorReward) {
            giveItems(player, piece, 1);
        }
        giveCommonRewards(player);
        if (CategoryManager.getInstance().isInCategory(CategoryType.FIRST_CLASS_GROUP, player.getClassId().getId())) {
            showOnScreenMsg(player, NpcStringId.YOU_VE_FINISHED_THE_TUTORIAL_NTAKE_YOUR_1ST_CLASS_TRANSFER_AND_COMPLETE_YOUR_TRAINING_WITH_BATHIS_TO_BECOME_STRONGER, ExShowScreenMessage.TOP_CENTER, 10000);
            player.sendPacket(ExRequestClassChangeUi.STATIC_PACKET);
        }
        qs.exitQuest(false, true);
    }

    private void giveCommonRewards(Player player) {
        addExpAndSp(player, 600000, 13500);
        giveItems(player, SOE_NOVICE);
        giveItems(player, SPIRIT_ORE);
        giveItems(player, HP_POTS);
        giveItems(player, RICE_CAKE_OF_FLAMING_FIGHTING_SPIRIT_EVENT);
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        var qs = getQuestState(killer, false);
        if (qs != null && qs.isCond(1)) {
            var progress = questHuntProgress(killer);

            if (progress >= 30) {
                qs.setCond(2, true);
                qs.unset(QUEST_PROGRESS);
                showOnScreenMsg(killer, NpcStringId.YOU_VE_KILLED_ALL_THE_MONSTERS_USE_THE_SCROLL_OF_ESCAPE_IN_YOUR_INVENTORY_TO_RETURN_TO_CAPTAIN_BATHIS_IN_GLUDIO, ExShowScreenMessage.TOP_CENTER, 10000);
                giveItems(killer, SOE_TO_CAPTAIN_BATHIS);
            }
        }
        return super.onKill(npc, killer, isSummon);
    }

    @Override
    public String onTalk(Npc npc, Player player) {
        var qs = getQuestState(player, true);
        var htmlText = getNoQuestMsg(player);

        if(isNull(qs)) {
            return htmlText;
        }

        if (qs.isCreated()) {
            htmlText = startNpc + ".htm";
        }
        else if (qs.isStarted()) {
            if(npc.getId() == startNpc && qs.isCond(1)) {
                htmlText = startNpc + "-01.html";
            } else if(npc.getId() == CAPTAIN_BATHIS && qs.isCond(2)) {
                htmlText = "30332.html";
            }
        } else if (qs.isCompleted()) {
            if (npc.getId() == startNpc) {
                htmlText = getAlreadyCompletedMsg(player);
            }
        }
        return htmlText;
    }

    protected abstract IntCollection huntMonsters();

    protected abstract NpcStringId questName();

    protected abstract Location huntingGroundLocation();

    protected abstract int questHuntProgress(Player player);
}