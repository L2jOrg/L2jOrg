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
package org.l2j.gameserver.model.item.container;

import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.instance.Item;

public class PetInventory extends Inventory {
    private final Pet _owner;

    public PetInventory(Pet owner) {
        _owner = owner;
    }

    @Override
    public Pet getOwner() {
        return _owner;
    }

    @Override
    public int getOwnerId() {
        // gets the Player-owner's ID
        int id;
        try {
            id = _owner.getOwner().getObjectId();
        } catch (NullPointerException e) {
            return 0;
        }
        return id;
    }

    /**
     * Refresh the weight of equipment loaded
     */
    @Override
    protected void refreshWeight() {
        super.refreshWeight();
        _owner.updateAndBroadcastStatus(1);
    }

    public boolean validateCapacity(Item item) {
        int slots = 0;

        if (!(item.isStackable() && (getItemByItemId(item.getId()) != null)) && !item.getTemplate().hasExImmediateEffect()) {
            slots++;
        }

        return validateCapacity(slots);
    }

    @Override
    public boolean validateCapacity(long slots) {
        return ((items.size() + slots) <= _owner.getInventoryLimit());
    }

    public boolean validateWeight(Item item, long count) {
        int weight = 0;
        final ItemTemplate template = ItemEngine.getInstance().getTemplate(item.getId());
        if (template == null) {
            return false;
        }
        weight += count * template.getWeight();
        return validateWeight(weight);
    }

    @Override
    public boolean validateWeight(long weight) {
        return ((_totalWeight + weight) <= _owner.getMaxLoad());
    }

    @Override
    protected ItemLocation getBaseLocation() {
        return ItemLocation.PET;
    }

    @Override
    protected ItemLocation getEquipLocation() {
        return ItemLocation.PET_EQUIP;
    }

    @Override
    public void restore() {
        super.restore();
        // check for equiped items from other pets
        for (Item item : items.values()) {
            if (item.isEquipped()) {
                if (!item.getTemplate().checkCondition(_owner, _owner, false)) {
                    unEquipItemInSlot(InventorySlot.fromId(item.getLocationSlot()));
                }
            }
        }
    }

    public void transferItemsToOwner() {
        for (Item item : items.values()) {
            getOwner().transferItem("return", item.getObjectId(), item.getCount(), getOwner().getOwner().getInventory(), getOwner().getOwner(), getOwner());
        }
    }
}
