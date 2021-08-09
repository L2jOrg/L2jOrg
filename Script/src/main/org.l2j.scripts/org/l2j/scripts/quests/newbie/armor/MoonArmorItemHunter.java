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

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;

/**
 * @author JoeAlisson
 */
public abstract class MoonArmorItemHunter extends MoonArmorRewardQuest {

    private final int itemHunted;

    protected MoonArmorItemHunter(int questId, int startNpc, int itemHunted, ClassId... classIds) {
        super(questId, startNpc, classIds);
        registerQuestItems(itemHunted);
        this.itemHunted = itemHunted;
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        var qs = getQuestState(killer, false);
        if(qs != null && qs.isCond(1)) {
            if(Rnd.chance(dropChance(npc.getId()))) {
                giveItems(killer, itemHunted, 1);
                playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
            }
        }
        return super.onKill(npc, killer, isSummon);
    }

    @Override
    protected int questHuntProgress(Player player) {
        return (int) getQuestItemsCount(player, itemHunted);
    }

    protected abstract int dropChance(int npcId);
}