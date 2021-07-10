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
import io.github.joealisson.primitive.IntIntMap;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
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

/**
 * @author JoeAlisson
 */
public abstract class ArmorRewardQuest extends Quest {

    private static final int CAPTAIN_BATHIS = 30332;

    private static final ItemHolder SOE_TO_CAPTAIN_BATHIS = new ItemHolder(91918, 1);
    private static final ItemHolder SOE_NOVICE = new ItemHolder(10650, 20);
    private static final ItemHolder SPIRIT_ORE = new ItemHolder(3031, 50);
    private static final ItemHolder HP_POTS = new ItemHolder(91912, 50);// TODO: Finish Item
    private static final ItemHolder RICE_CAKE_OF_FLAMING_FIGHTING_SPIRIT_EVENT = new ItemHolder(91840, 1);

    // HELMET FOR ALL ARMORS
    private static final ItemHolder MOON_HELMET = new ItemHolder(7850, 1);
    // HEAVY
    private static final ItemHolder MOON_ARMOR = new ItemHolder(7851, 1);
    private static final ItemHolder MOON_GAUNTLETS = new ItemHolder(7852, 1);
    private static final ItemHolder MOON_BOOTS = new ItemHolder(7853, 1);
    // LIGHT
    private static final ItemHolder MOON_SHELL = new ItemHolder(7854, 1);
    private static final ItemHolder MOON_LEATHER_GLOVES = new ItemHolder(7855, 1);
    private static final ItemHolder MOON_SHOES = new ItemHolder(7856, 1);
    // ROBE
    private static final ItemHolder MOON_CAPE = new ItemHolder(7857, 1);
    private static final ItemHolder MOON_SILK = new ItemHolder(7858, 1);
    private static final ItemHolder MOON_SANDALS = new ItemHolder(7859, 1);

    private static final String KILL_COUNT_VAR = "KillCount";
    private final int startNpc;
    private final int questItem;

    protected ArmorRewardQuest(int questId, int startNpc, int questItem, ClassId... classIds) {
        super(questId);
        addCondClassIds(classIds);
        addStartNpc(startNpc);
        addTalkId(startNpc, CAPTAIN_BATHIS);
        addKillId(huntMonsters());
        registerQuestItems(questItem);
        addCondLevel(15, 20, "no_lvl.html");
        setQuestNameNpcStringId(questName());

        this.startNpc = startNpc;
        this.questItem = questItem;
    }

    @Override
    public boolean checkPartyMember(Player member, Npc npc) {
        var qs = getQuestState(member, false);
        return qs != null && qs.isStarted();
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

        String htmltext = null;
        switch (event) {
            case "30332-01.html":
            {
                htmltext = event;
                break;
            }
            case "30332-02.html":
            {
                htmltext = event;
                break;
            }
            case "30332-03.html":
            {
                htmltext = event;
                break;
            }
            case "30332.html":
            {
                htmltext = event;
                break;
            }

            case "TELEPORT_TO_HUNTING_GROUND":
            {
                player.teleToLocation(huntingGroundLocation());
                break;
            }
            case "NEXT_QUEST":
            {
                htmltext = startNpc + ".htm";
                break;
            }
            case "HeavyArmor.html":
            {
                if (qs.isStarted())
                {
                    addExpAndSp(player, 600000, 13500);
                    giveItems(player, SOE_NOVICE);
                    giveItems(player, SPIRIT_ORE);
                    giveItems(player, HP_POTS);
                    giveItems(player, RICE_CAKE_OF_FLAMING_FIGHTING_SPIRIT_EVENT);
                    giveItems(player, MOON_HELMET);
                    giveItems(player, MOON_ARMOR);
                    giveItems(player, MOON_GAUNTLETS);
                    giveItems(player, MOON_BOOTS);
                    if (CategoryManager.getInstance().isInCategory(CategoryType.FIRST_CLASS_GROUP, player.getClassId().getId()))
                    {
                        showOnScreenMsg(player, NpcStringId.YOU_VE_FINISHED_THE_TUTORIAL_NTAKE_YOUR_1ST_CLASS_TRANSFER_AND_COMPLETE_YOUR_TRAINING_WITH_BATHIS_TO_BECOME_STRONGER, ExShowScreenMessage.TOP_CENTER, 10000);
                        player.sendPacket(ExRequestClassChangeUi.STATIC_PACKET);
                    }
                    qs.exitQuest(false, true);
                    htmltext = event;
                }
                break;
            }
            case "LightArmor.html":
            {
                if (qs.isStarted())
                {
                    addExpAndSp(player, 600000, 13500);
                    giveItems(player, SOE_NOVICE);
                    giveItems(player, SPIRIT_ORE);
                    giveItems(player, HP_POTS);
                    giveItems(player, RICE_CAKE_OF_FLAMING_FIGHTING_SPIRIT_EVENT);
                    giveItems(player, MOON_HELMET);
                    giveItems(player, MOON_SHELL);
                    giveItems(player, MOON_LEATHER_GLOVES);
                    giveItems(player, MOON_SHOES);
                    if (CategoryManager.getInstance().isInCategory(CategoryType.FIRST_CLASS_GROUP, player.getClassId().getId()))
                    {
                        showOnScreenMsg(player, NpcStringId.YOU_VE_FINISHED_THE_TUTORIAL_NTAKE_YOUR_1ST_CLASS_TRANSFER_AND_COMPLETE_YOUR_TRAINING_WITH_BATHIS_TO_BECOME_STRONGER, ExShowScreenMessage.TOP_CENTER, 10000);
                        player.sendPacket(ExRequestClassChangeUi.STATIC_PACKET);
                    }
                    qs.exitQuest(false, true);
                    htmltext = event;
                }
                break;
            }
            case "Robe.html":
            {
                if (qs.isStarted())
                {
                    addExpAndSp(player, 600000, 13500);
                    giveItems(player, SOE_NOVICE);
                    giveItems(player, SPIRIT_ORE);
                    giveItems(player, HP_POTS);
                    giveItems(player, RICE_CAKE_OF_FLAMING_FIGHTING_SPIRIT_EVENT);
                    giveItems(player, MOON_HELMET);
                    giveItems(player, MOON_CAPE);
                    giveItems(player, MOON_SILK);
                    giveItems(player, MOON_SANDALS);
                    if (CategoryManager.getInstance().isInCategory(CategoryType.FIRST_CLASS_GROUP, player.getClassId().getId()))
                    {
                        showOnScreenMsg(player, NpcStringId.YOU_VE_FINISHED_THE_TUTORIAL_NTAKE_YOUR_1ST_CLASS_TRANSFER_AND_COMPLETE_YOUR_TRAINING_WITH_BATHIS_TO_BECOME_STRONGER, ExShowScreenMessage.TOP_CENTER, 10000);
                        player.sendPacket(ExRequestClassChangeUi.STATIC_PACKET);
                    }
                    qs.exitQuest(false, true);
                    htmltext = event;
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
        if ((qs != null) && qs.isCond(1))
        {
            final int killCount = qs.getInt(KILL_COUNT_VAR) + 1;
            if (Rnd.get(100) < dropChances().get(npc.getId())) 
            {
                giveItems(killer, questItem, 1);
                playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
            }
            if (killCount < 30) 
            {
                qs.set(KILL_COUNT_VAR, killCount);
                playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
                sendNpcLogList(killer);
            }
            else 
            {
                qs.setCond(2, true);
                qs.unset(KILL_COUNT_VAR);
                killer.sendPacket(new ExShowScreenMessage("You hunted all monsters.#Use the Scroll of Escape in you inventory to go to Captain Bathis in the Town of Gludio.", 5000));
                giveItems(killer, SOE_TO_CAPTAIN_BATHIS);
            }
        }
        return super.onKill(npc, killer, isSummon);
    }

    @Override
    public Set<NpcLogListHolder> getNpcLogList(Player player)
    {
        final QuestState qs = getQuestState(player, false);
        if ((qs != null) && qs.isCond(1))
        {
            final Set<NpcLogListHolder> holder = new HashSet<>();
            holder.add(new NpcLogListHolder(inProgressStringId().getId(), true, qs.getInt(KILL_COUNT_VAR)));
            return holder;
        }
        return super.getNpcLogList(player);
    }

    @Override
    public String onTalk(Npc npc, Player player)
    {
        final QuestState qs = getQuestState(player, true);
        String htmltext = getNoQuestMsg(player);

        if(isNull(qs)) {
            return htmltext;
        }

        if (qs.isCreated())
        {
            htmltext = startNpc + ".htm";
        }
        else if (qs.isStarted())
        {
            if(npc.getId() == startNpc) {
                if(qs.isCond(1)) {
                    htmltext = startNpc + "-01.html";
                }
            } else if(npc.getId() == CAPTAIN_BATHIS) {
                if(qs.isCond(2)) {
                    htmltext = "30332.html";
                }
            }
        }
        else if (qs.isCompleted())
        {
            if (npc.getId() == startNpc)
            {
                htmltext = getAlreadyCompletedMsg(player);
            }
        }
        return htmltext;
    }

    protected abstract IntCollection huntMonsters();

    protected abstract NpcStringId questName();

    protected abstract Location huntingGroundLocation();

    protected abstract IntIntMap dropChances();

    protected abstract NpcStringId inProgressStringId();
}