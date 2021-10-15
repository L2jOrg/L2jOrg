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
package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.ArmorSet;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.function.ToIntFunction;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public record ArmorsetSkillInfo(Skill skill, int minPieces, int minEnchant, boolean isOptional, int artifactSlotMask, int artifactBookSlot) {

    public boolean validateConditions(Player player, ArmorSet armorSet, ToIntFunction<Item> idProvider) {
        // Player's doesn't have full busy (1 of 3) artifact real slot
        if (artifactSlotMask > armorSet.getArtifactSlotMask(player, artifactBookSlot)) {
            return false;
        }

        // Player doesn't have enough items equipped to use this skill
        if (minPieces > armorSet.getPiecesCount(player, idProvider)) {
            return false;
        }

        // Player's set enchantment isn't enough to use this skill
        if (minEnchant > armorSet.getLowestSetEnchant(player)) {
            return false;
        }

        // Player doesn't have the required item to use this skill
        if (isOptional && !armorSet.hasOptionalEquipped(player, idProvider)) {
            return false;
        }

        // Player already knows that skill
        return player.getSkillLevel(skill.getId()) != skill.getLevel();
    }
}
