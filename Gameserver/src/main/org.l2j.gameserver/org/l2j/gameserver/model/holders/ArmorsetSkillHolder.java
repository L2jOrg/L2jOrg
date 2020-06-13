/*
 * Copyright © 2019-2020 L2JOrg
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

import org.l2j.gameserver.model.ArmorSet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;

import java.util.function.ToIntFunction;

/**
 * @author UnAfraid
 */
public class ArmorsetSkillHolder extends SkillHolder {
    private final int _minimumPieces;
    private final int _minEnchant;
    private final int _artifactSlotMask;
    private final int _artifactBookSlot;
    private final boolean _isOptional;

    public ArmorsetSkillHolder(int skillId, int skillLvl, int minimumPieces, int minEnchant, boolean isOptional, int artifactSlotMask, int artifactBookSlot) {
        super(skillId, skillLvl);
        _minimumPieces = minimumPieces;
        _minEnchant = minEnchant;
        _isOptional = isOptional;
        _artifactSlotMask = artifactSlotMask;
        _artifactBookSlot = artifactBookSlot;
    }

    public int getMinimumPieces() {
        return _minimumPieces;
    }

    public int getMinEnchant() {
        return _minEnchant;
    }

    public boolean isOptional() {
        return _isOptional;
    }

    public boolean validateConditions(Player player, ArmorSet armorSet, ToIntFunction<Item> idProvider) {
        // Player's doesn't have full busy (1 of 3) artifact real slot
        if (_artifactSlotMask > armorSet.getArtifactSlotMask(player, _artifactBookSlot)) {
            return false;
        }

        // Player doesn't have enough items equipped to use this skill
        if (_minimumPieces > armorSet.getPiecesCount(player, idProvider)) {
            return false;
        }

        // Player's set enchantment isn't enough to use this skill
        if (_minEnchant > armorSet.getLowestSetEnchant(player)) {
            return false;
        }

        // Player doesn't have the required item to use this skill
        if (_isOptional && !armorSet.hasOptionalEquipped(player, idProvider)) {
            return false;
        }

        // Player already knows that skill
        if (player.getSkillLevel(getSkillId()) == getLevel()) {
            return false;
        }

        return true;
    }
}
