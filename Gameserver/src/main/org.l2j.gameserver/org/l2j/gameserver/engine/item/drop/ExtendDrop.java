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
package org.l2j.gameserver.engine.item.drop;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.List;

/**
 * @author JoeAlisson
 */
public record ExtendDrop(List<ExtendDropItem> items, List<ExtendDropCondition> conditions) {

    public void reward(Player player, Npc npc, float penaltyModifier) {
        for (var condition : conditions) {
            if (!condition.test(player, npc)) {
                return;
            }
        }

        for (var item : items) {
            if (Rnd.chance(item.getChance())) {
                player.addItem("ExtendDrop", item.getId(), (long) (Rnd.get(item.getCount(), item.getMaxCount()) * penaltyModifier), player, true);
            }
        }
    }
}