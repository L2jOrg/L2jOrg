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
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.instance.Item;

/**
 * @author JoeAlisson
 */
public final class BraceletListener implements PlayerInventoryListener {

    private BraceletListener() {

    }

    @Override
    public void notifyUnequiped(InventorySlot slot, Item item, Inventory inventory) {
        if (item.getBodyPart() == BodyPart.RIGHT_BRACELET) {
            InventorySlot.talismans().forEach(inventory::unEquipItemInSlot);
        }
    }

    // Note (April 3, 2009): Currently on equip, talismans do not display properly, do we need checks here to fix this?
    @Override
    public void notifyEquiped(InventorySlot slot, Item item, Inventory inventory) {
    }

    public static BraceletListener provider() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final BraceletListener INSTANCE = new BraceletListener();
    }
}
