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
package org.l2j.gameserver.engine.item.container.listener;

import org.l2j.gameserver.api.item.PlayerInventoryListener;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.engine.item.Item;

import java.util.Arrays;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public final class BroochListener implements PlayerInventoryListener {

    private static final int[] specialEffectJewels = {
        70451, 70452, 70453, 70454, 70455, 70456, 70457, 70458, 70459, 70460, 71368, 71369, 71370, 71371, 71372, 71373, 71374, 71375, 71376, 71377,
        90328, 90329, 90330, 90331, 90332, 90333, 90334, 90335, 90336, 90337, 91320, 91321, 91322, 91323, 91324, 91325, 91326, 91327, 91328, 91329,
        91418, 91419, 91431, 91432, 91758, 91759, 92388, 92389
    };

    private BroochListener() {
    }

    @Override
    public void notifyUnequipped(InventorySlot slot, Item item, Inventory inventory) {
        if (slot == InventorySlot.BROOCH) {
            InventorySlot.brochesJewel().forEach(inventory::unEquipItemInSlot);
        } else if(inventory instanceof PlayerInventory inv && hasSpecialEffect(item)) {
            updateAdditionalSoulshot(inv);
        }
    }

    private boolean hasSpecialEffect(Item item) {
        return item.getBodyPart() == BodyPart.BROOCH_JEWEL && Arrays.binarySearch(specialEffectJewels, item.getId()) >= 0;
    }

    private void updateAdditionalSoulshot(PlayerInventory inventory) {
        int jewel = 0;
        int currentLevel = -1;
        for (InventorySlot slot : InventorySlot.brochesJewel()) {
            var item = inventory.getPaperdollItem(slot);
            if(nonNull(item) && hasSpecialEffect(item)) {
                var itemLevel = item.getSkills(ItemSkillType.NORMAL).stream().mapToInt(SkillHolder::getLevel).max().orElse(-1);
                if(jewel == 0 || itemLevel > currentLevel) {
                    jewel = item.getId();
                    currentLevel = itemLevel;
                }
            }
        }
        inventory.getOwner().setAdditionalSoulshot(jewel);
    }

    @Override
    public void notifyEquipped(InventorySlot slot, Item item, Inventory inventory) {
        if(inventory instanceof PlayerInventory inv && hasSpecialEffect(item)) {
            updateAdditionalSoulshot(inv);
        }
    }

    public static BroochListener provider() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final BroochListener INSTANCE = new BroochListener();
    }
}
