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

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.util.StreamUtil;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.WorldTimeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.zeroIfNullOrElse;

/**
 * @author Advi
 * @author JoeAlisson
 */
public abstract class ItemContainer {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ItemContainer.class);

    protected final IntMap<Item> items = new CHashIntMap<>();

    protected ItemContainer() {
    }

    public int getOwnerId() {
        return zeroIfNullOrElse(getOwner(), WorldObject::getObjectId);
    }

    public int getSize() {
        return items.size();
    }

    @SafeVarargs
    public final int getSize(Predicate<Item> filter, Predicate<Item>... filters) {
        for (Predicate<Item> additionalFilter : filters) {
            filter = filter.and(additionalFilter);
        }
        return (int) items.values().stream().filter(filter).count();
    }

    /**
     * Gets the items in inventory.
     *
     * @return the items in inventory.
     *
     * TODO replace with bulk method operation forEach like
     */
    public Collection<Item> getItems() {
        return getItems(i -> true);
    }

    /**
     * Gets the items in inventory filtered by filter.
     *
     * @param filter  the filter
     * @param filters multiple filters
     * @return the filtered items in inventory
     */
    @SafeVarargs
    public final Collection<Item> getItems(Predicate<Item> filter, Predicate<Item>... filters) {
        for (Predicate<Item> additionalFilter : filters) {
            filter = filter.and(additionalFilter);
        }
        return items.values().stream().filter(filter).collect(Collectors.toCollection(LinkedList::new));
    }

    public final IntSet getItemsId(Predicate<Item> filter) {
        return StreamUtil.collectToSet(items.entrySet().stream()
                .filter(e -> filter.test(e.getValue()))
                .mapToInt(IntMap.Entry::getKey));
    }

    /**
     * @param itemId the item Id
     * @return the item from inventory by itemId
     */
    public Item getItemByItemId(int itemId) {
        return items.values().stream().filter(item -> item.getId() == itemId).findFirst().orElse(null);
    }

    /**
     * @param itemId the item Id
     * @return the items list from inventory by using its itemId
     */
    public Collection<Item> getItemsByItemId(int itemId) {
        return getItems(i -> i.getId() == itemId);
    }

    /**
     * @param objectId the item object Id
     * @return item from inventory by objectId
     */
    public Item getItemByObjectId(int objectId) {
        return items.get(objectId);
    }

    /**
     * Gets the inventory item count by item Id and enchant level including equipped items.
     *
     * @param itemId       the item Id
     * @param enchantLevel the item enchant level, use -1 to match any enchant level
     * @return the inventory item count
     */
    public long getInventoryItemCount(int itemId, int enchantLevel) {
        return getInventoryItemCount(itemId, enchantLevel, true);
    }

    /**
     * Gets the inventory item count by item Id and enchant level, may include equipped items.
     *
     * @param itemId          the item Id
     * @param enchantLevel    the item enchant level, use -1 to match any enchant level
     * @param includeEquipped if {@code true} includes equipped items in the result
     * @return the inventory item count
     */
    public long getInventoryItemCount(int itemId, int enchantLevel, boolean includeEquipped) {
        long count = 0;

        for (Item item : items.values()) {
            if ((item.getId() == itemId) && ((item.getEnchantLevel() == enchantLevel) || (enchantLevel < 0)) && (includeEquipped || !item.isEquipped())) {
                if (item.isStackable()) {
                    return item.getCount();
                }
                count++;
            }
        }
        return count;
    }

    /**
     * Adds item to inventory
     *
     * @param process   : String Identifier of process triggering this action
     * @param item      : Item to be added
     * @param actor     : Player Player requesting the item add
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the new item or the updated item in inventory
     */
    public Item addItem(String process, Item item, Player actor, Object reference) {
        final Item olditem = getItemByItemId(item.getId());

        // If stackable item is found in inventory just add to current quantity
        if ((olditem != null) && olditem.isStackable()) {
            final long count = item.getCount();
            olditem.changeCount(process, count, actor, reference);
            olditem.setLastChange(Item.MODIFIED);

            // And destroys the item
            ItemEngine.getInstance().destroyItem(process, item, actor, reference);
            item.updateDatabase();
            item = olditem;

            // Updates database
            final float adenaRate = Config.RATE_DROP_AMOUNT_BY_ID.getOrDefault(CommonItem.ADENA, 1f);
            if ((item.getId() == CommonItem.ADENA) && (count < (10000 * adenaRate))) {
                // Small adena changes won't be saved to database all the time
                if ((WorldTimeController.getInstance().getGameTicks() % 5) == 0) {
                    item.updateDatabase();
                }
            } else {
                item.updateDatabase();
            }
        }
        // If item hasn't be found in inventory, create new one
        else {
            item.setOwnerId(process, getOwnerId(), actor, reference);
            item.setItemLocation(getBaseLocation());
            item.setLastChange((Item.ADDED));

            // Add item in inventory
            addItem(item);

            // Updates database
            item.updateDatabase();
        }

        refreshWeight();
        return item;
    }

    /**
     * Adds item to inventory
     *
     * @param process   : String Identifier of process triggering this action
     * @param itemId    : int Item Identifier of the item to be added
     * @param count     : long Quantity of item to be added
     * @param actor     : Player Player requesting the item add
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the new item or the updated item in inventory
     */
    public Item addItem(String process, int itemId, long count, Player actor, Object reference) {
        Item item = getItemByItemId(itemId);

        // If stackable item is found in inventory just add to current quantity
        if ((item != null) && item.isStackable()) {
            item.changeCount(process, count, actor, reference);
            item.setLastChange(Item.MODIFIED);
            // Updates database
            // If Adena drop rate is not present it will be x1.
            final float adenaRate = Config.RATE_DROP_AMOUNT_BY_ID.getOrDefault(CommonItem.ADENA, 1f);
            if ((itemId == CommonItem.ADENA) && (count < (10000 * adenaRate))) {
                // Small adena changes won't be saved to database all the time
                if ((WorldTimeController.getInstance().getGameTicks() % 5) == 0) {
                    item.updateDatabase();
                }
            } else {
                item.updateDatabase();
            }
        }
        // If item hasn't be found in inventory, create new one
        else {
            for (int i = 0; i < count; i++) {
                final ItemTemplate template = ItemEngine.getInstance().getTemplate(itemId);
                if (template == null) {
                    LOGGER.warn((actor != null ? "[" + actor.getName() + "] " : "") + "Invalid ItemId requested: ", itemId);
                    return null;
                }

                item = ItemEngine.getInstance().createItem(process, itemId, template.isStackable() ? count : 1, actor, reference);
                item.setOwnerId(getOwnerId());
                item.setItemLocation(getBaseLocation());
                item.setLastChange(Item.ADDED);

                // Add item in inventory
                addItem(item);
                // Updates database
                item.updateDatabase();

                // If stackable, end loop as entire count is included in 1 instance of item
                if (template.isStackable() || !Config.MULTIPLE_ITEM_DROP) {
                    break;
                }
            }
        }

        refreshWeight();
        return item;
    }

    /**
     * Transfers item to another inventory
     *
     * @param process   string Identifier of process triggering this action
     * @param objectId  Item Identifier of the item to be transfered
     * @param count     Quantity of items to be transfered
     * @param target    the item container where the item will be moved.
     * @param actor     Player requesting the item transfer
     * @param reference Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the new item or the updated item in inventory
     */
    public Item transferItem(String process, int objectId, long count, ItemContainer target, Player actor, Object reference) {
        if (isNull(target)) {
            return null;
        }

        final Item sourceItem = getItemByObjectId(objectId);
        if (isNull(sourceItem)) {
            return null;
        }

        // Check if requested quantity is available
        if (count > sourceItem.getCount()) {
            count = sourceItem.getCount();
        }

        Item targetItem = sourceItem.isStackable() ? target.getItemByItemId(sourceItem.getId()) : null;

        // If possible, move entire item object
        if ( sourceItem.getCount() == count && isNull(targetItem) && !sourceItem.isStackable()) {
            removeItem(sourceItem);
            target.addItem(process, sourceItem, actor, reference);
            targetItem = sourceItem;
        } else {
            if (sourceItem.getCount() > count)  {// If possible, only update counts
                sourceItem.changeCount(process, -count, actor, reference);
            } else {
                removeItem(sourceItem);
                ItemEngine.getInstance().destroyItem(process, sourceItem, actor, reference);
            }

            if (nonNull(targetItem)) { // If possible, only update counts
                targetItem.changeCount(process, count, actor, reference);
            } else {
                targetItem = target.addItem(process, sourceItem.getId(), count, actor, reference);
            }
        }

        sourceItem.updateDatabase(true);
        if ((targetItem != sourceItem) && (targetItem != null)) {
            targetItem.updateDatabase();
        }
        if (sourceItem.isAugmented()) {
            sourceItem.getAugmentation().removeBonus(actor);
        }
        refreshWeight();
        target.refreshWeight();
        return targetItem;
    }

    /**
     * Detaches the item from this item container so it can be used as a single instance.
     *
     * @param process     string Identifier of process triggering this action
     * @param item        the item instance to be detached
     * @param count       the count of item to be detached
     * @param newLocation the new item location
     * @param actor       Player requesting the item detach
     * @param reference   Object Object referencing current action like NPC selling item or previous item in transformation
     * @return the detached item instance if operation completes successfully, {@code null} if the item does not exist in this container anymore or item count is not available
     */
    public Item detachItem(String process, Item item, long count, ItemLocation newLocation, Player actor, Object reference) {
        if (item == null) {
            return null;
        }

        synchronized (item) {
            if (!items.containsKey(item.getObjectId())) {
                return null;
            }

            if (count > item.getCount()) {
                return null;
            }

            if (count == item.getCount()) {
                removeItem(item);
            } else {
                item.changeCount(process, -count, actor, reference);
                item.updateDatabase(true);
                item = ItemEngine.getInstance().createItem(process, item.getId(), count, actor, reference);
                item.setOwnerId(getOwnerId());
            }
            item.setItemLocation(newLocation);
            item.updateDatabase(true);
        }

        refreshWeight();

        return item;
    }

    /**
     * Destroy item from inventory and updates database
     *
     * @param process   : String Identifier of process triggering this action
     * @param item      : Item to be destroyed
     * @param actor     : Player Player requesting the item destroy
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the destroyed item or the updated item in inventory
     */
    public Item destroyItem(String process, Item item, Player actor, Object reference) {
        return destroyItem(process, item, item.getCount(), actor, reference);
    }

    /**
     * Destroy item from inventory and updates database
     *
     * @param process   : String Identifier of process triggering this action
     * @param item      : Item to be destroyed
     * @param count
     * @param actor     : Player Player requesting the item destroy
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the destroyed item or the updated item in inventory
     */
    public Item destroyItem(String process, Item item, long count, Player actor, Object reference) {
        synchronized (item) {
            // Adjust item quantity
            if (item.getCount() > count) {
                item.changeCount(process, -count, actor, reference);
                item.setLastChange(Item.MODIFIED);

                // don't update often for untraced item
                if ((process != null) || ((WorldTimeController.getInstance().getGameTicks() % 10) == 0)) {
                    item.updateDatabase();
                }

                refreshWeight();
            } else {
                if (item.getCount() < count) {
                    return null;
                }

                final boolean removed = removeItem(item);
                if (!removed) {
                    return null;
                }

                ItemEngine.getInstance().destroyItem(process, item, actor, reference);

                item.updateDatabase();
                refreshWeight();

                item.deleteMe();
                item.setLastChange(Item.REMOVED);
            }
        }
        return item;
    }

    /**
     * Destroy item from inventory by using its <B>objectID</B> and updates database
     *
     * @param process   : String Identifier of process triggering this action
     * @param objectId  : int Item Instance identifier of the item to be destroyed
     * @param count     : int Quantity of items to be destroyed
     * @param actor     : Player Player requesting the item destroy
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the destroyed item or the updated item in inventory
     */
    public Item destroyItem(String process, int objectId, long count, Player actor, Object reference) {
        final Item item = getItemByObjectId(objectId);
        if (item == null) {
            return null;
        }
        return destroyItem(process, item, count, actor, reference);
    }

    /**
     * Destroy item from inventory by using its <B>itemId</B> and updates database
     *
     * @param process   : String Identifier of process triggering this action
     * @param itemId    : int Item identifier of the item to be destroyed
     * @param count     : int Quantity of items to be destroyed
     * @param actor     : Player Player requesting the item destroy
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the destroyed item or the updated item in inventory
     */
    public Item destroyItemByItemId(String process, int itemId, long count, Player actor, Object reference) {
        final Item item = getItemByItemId(itemId);
        if (item == null) {
            return null;
        }
        return destroyItem(process, item, count, actor, reference);
    }

    /**
     * Destroy all items from inventory and updates database
     *
     * @param process   : String Identifier of process triggering this action
     * @param actor     : Player Player requesting the item destroy
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     */
    public void destroyAllItems(String process, Player actor, Object reference) {
        for (Item item : items.values()) {
            destroyItem(process, item, actor, reference);
        }
    }

    public long getAdena() {
        for (Item item : items.values()) {
            if (item.getId() == CommonItem.ADENA) {
                return item.getCount();
            }
        }
        return 0;
    }

    public long getBeautyTickets() {
        for (Item item : items.values()) {
            if (item.getId() == Inventory.BEAUTY_TICKET_ID) {
                return item.getCount();
            }
        }
        return 0;
    }

    /**
     * Adds item to inventory for further adjustments.
     *
     * @param item : Item to be added from inventory
     */
    protected void addItem(Item item) {
        items.put(item.getObjectId(), item);
    }

    /**
     * Removes item from inventory for further adjustments.
     *
     * @param item : Item to be removed from inventory
     */
    protected boolean removeItem(Item item) {
        return items.remove(item.getObjectId()) != null;
    }

    /**
     * Refresh the weight of equipment loaded
     */
    protected void refreshWeight() {
    }

    /**
     * Delete item object from world
     */
    public void deleteMe() {
        if (getOwner() != null) {
            for (Item item : items.values()) {
                item.updateDatabase(true);
                item.deleteMe();
                World.getInstance().removeObject(item);
            }
        }
        items.clear();
    }

    /**
     * Update database with items in inventory
     */
    public void updateDatabase() {
        if (getOwner() != null) {
            for (Item item : items.values()) {
                item.updateDatabase(true);
            }
        }
    }

    /**
     * Get back items in container from database
     */
    public void restore() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM items WHERE owner_id=? AND (loc=?)")) {
            ps.setInt(1, getOwnerId());
            ps.setString(2, getBaseLocation().name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Item item = new Item(rs);
                    World.getInstance().addObject(item);

                    final Player owner = getOwner() != null ? getOwner().getActingPlayer() : null;

                    // If stackable item is found in inventory just add to current quantity
                    if (item.isStackable() && (getItemByItemId(item.getId()) != null)) {
                        addItem("Restore", item, owner, null);
                    } else {
                        addItem(item);
                    }
                }
            }
            refreshWeight();
        } catch (Exception e) {
            LOGGER.warn("could not restore container:", e);
        }
    }

    public boolean validateCapacity(long slots) {
        return true;
    }

    public boolean validateWeight(long weight) {
        return true;
    }

    /**
     * If the item is stackable validates 1 slot, if the item isn't stackable validates the item count.
     *
     * @param itemId the item Id to verify
     * @param count  amount of item's weight to validate
     * @return {@code true} if the item doesn't exists or it validates its slot count
     */
    public boolean validateCapacityByItemId(int itemId, long count) {
        final ItemTemplate template = ItemEngine.getInstance().getTemplate(itemId);
        return (template == null) || (template.isStackable() ? validateCapacity(1) : validateCapacity(count));
    }

    /**
     * @param itemId the item Id to verify
     * @param count  amount of item's weight to validate
     * @return {@code true} if the item doesn't exists or it validates its weight
     */
    public boolean validateWeightByItemId(int itemId, long count) {
        final ItemTemplate template = ItemEngine.getInstance().getTemplate(itemId);
        return (template == null) || validateWeight(template.getWeight() * count);
    }

    public String getName() {
        return "ItemContainer";
    }

    public abstract Creature getOwner();

    protected abstract ItemLocation getBaseLocation();
}
