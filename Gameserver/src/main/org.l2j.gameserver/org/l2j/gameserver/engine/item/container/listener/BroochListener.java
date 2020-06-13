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
package org.l2j.gameserver.engine.item.container.listener;

import org.l2j.gameserver.api.item.PlayerInventoryListener;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.item.instance.Item;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public final class BroochListener implements PlayerInventoryListener {

    private BroochListener() {
    }

    @Override
    public void notifyUnequiped(InventorySlot slot, Item item, Inventory inventory) {
        if (slot == InventorySlot.BROOCH) {
            InventorySlot.brochesJewel().forEach(inventory::unEquipItemInSlot);
        } else if(item.getBodyPart() == BodyPart.BROOCH_JEWEL && inventory instanceof PlayerInventory inv) {
            updateAdditionalSoulshot(inv);
        }
    }

    private void updateAdditionalSoulshot(PlayerInventory inventory) {
        int jewel = 0;
        int currentLevel = -1;
        for (InventorySlot slot : InventorySlot.brochesJewel()) {
            var item = inventory.getPaperdollItem(slot);

            if(nonNull(item)) {
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
    public void notifyEquiped(InventorySlot slot, Item item, Inventory inventory) {
        if(item.getBodyPart() == BodyPart.BROOCH_JEWEL && inventory instanceof PlayerInventory inv) {
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
