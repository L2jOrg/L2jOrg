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
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;

/**
 * @author JoeAlisson
 */
public final class WeaponListener implements PlayerInventoryListener {

    private WeaponListener() {
        // singleton
    }

    @Override
    public void notifyUnequipped(InventorySlot slot, Item item, Inventory inv) {
        if (!isWeaponSlot(slot)) {
            return;
        }

        if(inv instanceof PlayerInventory inventory) {
            updateConditionalItems(inventory);
        }
    }

    private void updateConditionalItems(PlayerInventory inventory) {
        var update = new InventoryUpdate();
        inventory.forEachItem(Item::hasCondition, update::addModifiedItem);
        if(update.hasItem()) {
            inventory.getOwner().sendPacket(update);
        }
    }

    private boolean isWeaponSlot(InventorySlot slot) {
        return slot == InventorySlot.RIGHT_HAND || slot == InventorySlot.TWO_HAND;
    }

    @Override
    public void notifyEquipped(InventorySlot slot, Item item, Inventory inv) {
        if (!isWeaponSlot(slot)) {
            return;
        }

        if(inv instanceof PlayerInventory inventory) {
            onWeaponEquipped(inventory);
        }
    }

    private void onWeaponEquipped(PlayerInventory inventory) {
        inventory.findAmmunitionForCurrentWeapon();
        var owner = inventory.getOwner();
        owner.rechargeShot(ShotType.SOULSHOTS);
        owner.rechargeShot(ShotType.SPIRITSHOTS);
        updateConditionalItems(inventory);
    }

    public static WeaponListener provider() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final WeaponListener INSTANCE = new WeaponListener();
    }
}
