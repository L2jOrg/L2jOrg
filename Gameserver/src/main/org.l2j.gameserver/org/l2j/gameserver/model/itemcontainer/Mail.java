/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.itemcontainer;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author DS
 */
public class Mail extends ItemContainer {
    private final int _ownerId;
    private int _messageId;

    public Mail(int objectId, int messageId) {
        _ownerId = objectId;
        _messageId = messageId;
    }

    @Override
    public String getName() {
        return "Mail";
    }

    @Override
    public Player getOwner() {
        return null;
    }

    @Override
    public ItemLocation getBaseLocation() {
        return ItemLocation.MAIL;
    }

    public int getMessageId() {
        return _messageId;
    }

    public void setNewMessageId(int messageId) {
        _messageId = messageId;
        for (Item item : items.values()) {
            item.setItemLocation(getBaseLocation(), messageId);
        }

        updateDatabase();
    }

    public void returnToWh(ItemContainer wh) {
        for (Item item : items.values()) {
            if (wh == null) {
                item.setItemLocation(ItemLocation.WAREHOUSE);
            } else {
                transferItem("Expire", item.getObjectId(), item.getCount(), wh, null, null);
            }
        }
    }

    @Override
    protected void addItem(Item item) {
        super.addItem(item);
        item.setItemLocation(getBaseLocation(), _messageId);
    }

    /*
     * Allow saving of the items without owner
     */
    @Override
    public void updateDatabase() {
        for (Item item : items.values()) {
            item.updateDatabase(true);
        }
    }

    @Override
    public void restore() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM items WHERE owner_id=? AND loc=? AND loc_data=?")) {
            statement.setInt(1, _ownerId);
            statement.setString(2, getBaseLocation().name());
            statement.setInt(3, _messageId);
            try (ResultSet inv = statement.executeQuery()) {
                while (inv.next()) {
                    final Item item = new Item(inv);
                    World.getInstance().addObject(item);

                    // If stackable item is found just add to current quantity
                    if (item.isStackable() && (getItemByItemId(item.getId()) != null)) {
                        addItem("Restore", item, null, null);
                    } else {
                        addItem(item);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("could not restore container:", e);
        }
    }

    @Override
    public void deleteMe() {
        for (Item item : items.values()) {
            item.updateDatabase(true);
            item.deleteMe();
            World.getInstance().removeObject(item);
        }

        items.clear();
    }

    @Override
    public int getOwnerId() {
        return _ownerId;
    }
}