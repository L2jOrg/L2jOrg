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
package org.l2j.gameserver.model.item.container;

import io.github.joealisson.primitive.*;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.gameserver.data.database.data.ItemData;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.item.ItemChangeType;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.engine.item.ItemTemplate;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.WorldTimeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.zeroIfNullOrElse;

/**
 * @author Advi
 * @author JoeAlisson
 */
public abstract class ItemContainer {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ItemContainer.class);

    protected final IntMap<Item> items = new CHashIntMap<>();
    protected final IntIntMap itemIdLookup = new HashIntIntMap();

    protected ItemContainer() {
    }

    public int getOwnerId() {
        return zeroIfNullOrElse(getOwner(), WorldObject::getObjectId);
    }

    public int getSize() {
        return items.size();
    }

    /**
     * Gets the items in inventory.
     *
     * @return the items in inventory.
     *
     * TODO replace with bulk method operation forEach like
     */
    public Collection<Item> getItems() {
        return items.values();
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
        List<Item> selected = new LinkedList<>();
        for (Item item : items.values()) {
            if(filter.test(item)) {
                selected.add(item);
            }
        }
        return selected;
    }

    public void forEachItem(Predicate<Item> filter, Consumer<Item> action) {
        for (Item item : items.values()) {
            if(filter.test(item)) {
                action.accept(item);
            }
        }
    }

    public void forEachItem(Consumer<Item> action) {
        items.values().forEach(action);
    }

    public final IntSet getItemsId(Predicate<Item> filter) {
        IntSet ids = new HashIntSet();
        for (IntMap.Entry<Item> entry : items.entrySet()) {
            if(filter.test(entry.getValue())) {
                ids.add(entry.getKey());
            }
        }
        return ids;
    }

    public Item getItemByItemId(int itemId) {
        if(itemIdLookup.containsKey(itemId)) {
            return items.get(itemIdLookup.get(itemId));
        }
        return null;
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
            if (item.getId() == itemId && (item.getEnchantLevel() == enchantLevel || enchantLevel < 0) && (includeEquipped || !item.isEquipped())) {
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
        final Item oldItem = getItemByItemId(item.getId());

        if (nonNull(oldItem) && oldItem.isStackable()) {
            changeItemCount(process, oldItem.getId(), item.getCount(), actor, reference, oldItem);

            ItemEngine.getInstance().destroyItem(process, item, actor, reference);
            item.updateDatabase();
            item = oldItem;
        }
        else {
            addNewItem(item);
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

        if (nonNull(item) && item.isStackable()) {
            changeItemCount(process, itemId, count, actor, reference, item);
        }
        else {
            item = createNewItem(process, itemId, count, actor, reference);
        }
        refreshWeight();
        return item;
    }

    private Item createNewItem(String process, int itemId, long count, Player actor, Object reference) {
        Item item = null;
        final ItemTemplate template = ItemEngine.getInstance().getTemplate(itemId);
        if(nonNull(template)) {
            if(template.isStackable()) {
                item = ItemEngine.getInstance().createItem(process, itemId, count, actor, reference);
                addNewItem(item);
            } else {
                for (int i = 0; i < count; i++) {
                    item = ItemEngine.getInstance().createItem(process, itemId, count, actor, reference);
                    addNewItem(item);
                }
            }
        } else {
            LOGGER.warn("{} Invalid ItemId {} requested by process {}", actor, itemId, process);
        }
        return item;
    }

    private void addNewItem(Item item) {
        item.changeOwner(getOwnerId());
        item.changeItemLocation(getBaseLocation());
        item.setLastChange(ItemChangeType.ADDED);

        addItem(item);
        item.updateDatabase();
    }

    private void changeItemCount(String process, int itemId, long count, Player actor, Object reference, Item item) {
        item.changeCount(process, count, actor, reference);
        item.setLastChange(ItemChangeType.MODIFIED);

        final float adenaRate = Config.RATE_DROP_AMOUNT_BY_ID.getOrDefault(CommonItem.ADENA, 1f);
        if ((itemId == CommonItem.ADENA) && (count < (10000 * adenaRate))) {

            if ((WorldTimeController.getInstance().getGameTicks() % 5) == 0) {
                item.updateDatabase();
            }
        } else {
            item.updateDatabase();
        }
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
        sourceItem.removeAugmentationBonus(actor);
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
            item.changeOwner(getOwnerId());
        }
        item.changeItemLocation(newLocation);
        item.updateDatabase(true);

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
     * @param count     : amount to be destroyed
     * @param actor     : Player Player requesting the item destroy
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the destroyed item or the updated item in inventory
     */
    public Item destroyItem(String process, Item item, long count, Player actor, Object reference) {
        // Adjust item quantity
        if (item.getCount() > count) {
            item.changeCount(process, -count, actor, reference);
            item.setLastChange(ItemChangeType.MODIFIED);

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
        itemIdLookup.putIfAbsent(item.getId(), item.getObjectId());
    }

    /**
     * Removes item from inventory for further adjustments.
     *
     * @param reference : Item to be removed from inventory
     */
    protected boolean removeItem(Item reference) {
        Item removed = items.remove(reference.getObjectId());
        if(nonNull(removed)) {
            updateItemIdLookUp(removed, items.values());
            return true;
        }
        return false;
    }

    protected void updateItemIdLookUp(Item removed, Collection<Item> items) {
        if(itemIdLookup.get(removed.getId()) == removed.getObjectId()) {
            itemIdLookup.remove(removed.getId());
            if(!removed.isStackable()) {
                for (Item i : items) {
                    if (i.getId() == removed.getId()) {
                        itemIdLookup.put(removed.getId(), i.getObjectId());
                        break;
                    }
                }
            }
        }
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

    public void restore() {
        for (ItemData itemData : getDAO(ItemDAO.class).findItemsByOwnerAndLoc(getOwnerId(), getBaseLocation())) {
            var item = new Item(itemData);
            World.getInstance().addObject(item);

            final Player owner = getOwner() != null ? getOwner().getActingPlayer() : null;

            // If stackable item is found in inventory just add to current quantity
            if (item.isStackable() && (getItemByItemId(item.getId()) != null)) {
                addItem("Restore", item, owner, null);
            } else {
                addItem(item);
            }
        }
        refreshWeight();
    }

    public boolean validateCapacity(long slots) {
        return true;
    }

    public boolean validateWeight(long weight) {
        return true;
    }

    public String getName() {
        return "ItemContainer";
    }

    public abstract Creature getOwner();

    protected abstract ItemLocation getBaseLocation();
}
