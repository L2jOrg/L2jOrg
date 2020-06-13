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
package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;

/**
 * @author UnAfraid
 */
public final class EnchantItemRequest extends AbstractRequest {

    private volatile int enchantingItemObjectId;
    private volatile int enchantingScrollObjectId;
    private volatile int supportItemObjectId;

    public EnchantItemRequest(Player player, int enchantingScrollObjectId) {
        super(player);
        this.enchantingScrollObjectId = enchantingScrollObjectId;
    }

    public Item getEnchantingItem() {
        return player.getInventory().getItemByObjectId(enchantingItemObjectId);
    }

    public void setEnchantingItem(int objectId) {
        enchantingItemObjectId = objectId;
    }

    public Item getEnchantingScroll() {
        return player.getInventory().getItemByObjectId(enchantingScrollObjectId);
    }

    public void setEnchantingScroll(int objectId) {
        enchantingScrollObjectId = objectId;
    }

    public Item getSupportItem() {
        return getPlayer().getInventory().getItemByObjectId(supportItemObjectId);
    }

    public void setSupportItem(int objectId) {
        supportItemObjectId = objectId;
    }

    @Override
    public boolean isItemRequest() {
        return true;
    }

    @Override
    public boolean canWorkWith(AbstractRequest request) {
        return !request.isItemRequest();
    }

    @Override
    public boolean isUsing(int objectId) {
        return (objectId > 0) && ((objectId == enchantingItemObjectId) || (objectId == enchantingScrollObjectId) || (objectId == supportItemObjectId));
    }
}
