package org.l2j.gameserver.model.itemcontainer;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.CommonItem;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Advi
 */
public abstract class ItemContainer {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ItemContainer.class);

    protected final Map<Integer, L2ItemInstance> _items = new ConcurrentHashMap<>();

    protected ItemContainer() {
    }

    protected abstract Creature getOwner();

    protected abstract ItemLocation getBaseLocation();

    public String getName() {
        return "ItemContainer";
    }

    /**
     * @return int the owner object Id
     */
    public int getOwnerId() {
        return getOwner() == null ? 0 : getOwner().getObjectId();
    }

    /**
     * @return the quantity of items in the inventory
     */
    public int getSize() {
        return _items.size();
    }

    /**
     * @param filter
     * @param filters
     * @return the quantity of items in the inventory
     */
    @SafeVarargs
    public final int getSize(Predicate<L2ItemInstance> filter, Predicate<L2ItemInstance>... filters) {
        for (Predicate<L2ItemInstance> additionalFilter : filters) {
            filter = filter.and(additionalFilter);
        }
        return (int) _items.values().stream().filter(filter).count();
    }

    /**
     * Gets the items in inventory.
     *
     * @return the items in inventory.
     */
    public Collection<L2ItemInstance> getItems() {
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
    public final Collection<L2ItemInstance> getItems(Predicate<L2ItemInstance> filter, Predicate<L2ItemInstance>... filters) {
        for (Predicate<L2ItemInstance> additionalFilter : filters) {
            filter = filter.and(additionalFilter);
        }
        return _items.values().stream().filter(filter).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * @param itemId the item Id
     * @return the item from inventory by itemId
     */
    public L2ItemInstance getItemByItemId(int itemId) {
        return _items.values().stream().filter(item -> item.getId() == itemId).findFirst().orElse(null);
    }

    /**
     * @param itemId the item Id
     * @return the items list from inventory by using its itemId
     */
    public Collection<L2ItemInstance> getItemsByItemId(int itemId) {
        return getItems(i -> i.getId() == itemId);
    }

    /**
     * @param objectId the item object Id
     * @return item from inventory by objectId
     */
    public L2ItemInstance getItemByObjectId(int objectId) {
        return _items.get(objectId);
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

        for (L2ItemInstance item : _items.values()) {
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
     * @return true if player got item for self resurrection
     */
    public final boolean haveItemForSelfResurrection() {
        return _items.values().stream().anyMatch(item -> item.getItem().isAllowSelfResurrection());
    }

    /**
     * Adds item to inventory
     *
     * @param process   : String Identifier of process triggering this action
     * @param item      : L2ItemInstance to be added
     * @param actor     : Player Player requesting the item add
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the new item or the updated item in inventory
     */
    public L2ItemInstance addItem(String process, L2ItemInstance item, Player actor, Object reference) {
        final L2ItemInstance olditem = getItemByItemId(item.getId());

        // If stackable item is found in inventory just add to current quantity
        if ((olditem != null) && olditem.isStackable()) {
            final long count = item.getCount();
            olditem.changeCount(process, count, actor, reference);
            olditem.setLastChange(L2ItemInstance.MODIFIED);

            // And destroys the item
            ItemTable.getInstance().destroyItem(process, item, actor, reference);
            item.updateDatabase();
            item = olditem;

            // Updates database
            final float adenaRate = Config.RATE_DROP_AMOUNT_BY_ID.getOrDefault(CommonItem.ADENA, 1f);
            if ((item.getId() == CommonItem.ADENA) && (count < (10000 * adenaRate))) {
                // Small adena changes won't be saved to database all the time
                if ((GameTimeController.getInstance().getGameTicks() % 5) == 0) {
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
            item.setLastChange((L2ItemInstance.ADDED));

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
     * @param count     : long Quantity of items to be added
     * @param actor     : Player Player requesting the item add
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the new item or the updated item in inventory
     */
    public L2ItemInstance addItem(String process, int itemId, long count, Player actor, Object reference) {
        L2ItemInstance item = getItemByItemId(itemId);

        // If stackable item is found in inventory just add to current quantity
        if ((item != null) && item.isStackable()) {
            item.changeCount(process, count, actor, reference);
            item.setLastChange(L2ItemInstance.MODIFIED);
            // Updates database
            // If Adena drop rate is not present it will be x1.
            final float adenaRate = Config.RATE_DROP_AMOUNT_BY_ID.getOrDefault(CommonItem.ADENA, 1f);
            if ((itemId == CommonItem.ADENA) && (count < (10000 * adenaRate))) {
                // Small adena changes won't be saved to database all the time
                if ((GameTimeController.getInstance().getGameTicks() % 5) == 0) {
                    item.updateDatabase();
                }
            } else {
                item.updateDatabase();
            }
        }
        // If item hasn't be found in inventory, create new one
        else {
            for (int i = 0; i < count; i++) {
                final L2Item template = ItemTable.getInstance().getTemplate(itemId);
                if (template == null) {
                    LOGGER.warn((actor != null ? "[" + actor.getName() + "] " : "") + "Invalid ItemId requested: ", itemId);
                    return null;
                }

                item = ItemTable.getInstance().createItem(process, itemId, template.isStackable() ? count : 1, actor, reference);
                item.setOwnerId(getOwnerId());
                item.setItemLocation(getBaseLocation());
                item.setLastChange(L2ItemInstance.ADDED);

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
     * @return L2ItemInstance corresponding to the new item or the updated item in inventory
     */
    public L2ItemInstance transferItem(String process, int objectId, long count, ItemContainer target, Player actor, Object reference) {
        if (target == null) {
            return null;
        }

        final L2ItemInstance sourceitem = getItemByObjectId(objectId);
        if (sourceitem == null) {
            return null;
        }
        L2ItemInstance targetitem = sourceitem.isStackable() ? target.getItemByItemId(sourceitem.getId()) : null;

        synchronized (sourceitem) {
            // check if this item still present in this container
            if (getItemByObjectId(objectId) != sourceitem) {
                return null;
            }

            // Check if requested quantity is available
            if (count > sourceitem.getCount()) {
                count = sourceitem.getCount();
            }

            // If possible, move entire item object
            if ((sourceitem.getCount() == count) && (targetitem == null) && !sourceitem.isStackable()) {
                removeItem(sourceitem);
                target.addItem(process, sourceitem, actor, reference);
                targetitem = sourceitem;
            } else {
                if (sourceitem.getCount() > count) // If possible, only update counts
                {
                    sourceitem.changeCount(process, -count, actor, reference);
                } else
                // Otherwise destroy old item
                {
                    removeItem(sourceitem);
                    ItemTable.getInstance().destroyItem(process, sourceitem, actor, reference);
                }

                if (targetitem != null) // If possible, only update counts
                {
                    targetitem.changeCount(process, count, actor, reference);
                } else
                // Otherwise add new item
                {
                    targetitem = target.addItem(process, sourceitem.getId(), count, actor, reference);
                }
            }

            // Updates database
            sourceitem.updateDatabase(true);
            if ((targetitem != sourceitem) && (targetitem != null)) {
                targetitem.updateDatabase();
            }
            if (sourceitem.isAugmented()) {
                sourceitem.getAugmentation().removeBonus(actor);
            }
            refreshWeight();
            target.refreshWeight();
        }
        return targetitem;
    }

    /**
     * Detaches the item from this item container so it can be used as a single instance.
     *
     * @param process     string Identifier of process triggering this action
     * @param item        the item instance to be detached
     * @param count       the count of items to be detached
     * @param newLocation the new item location
     * @param actor       Player requesting the item detach
     * @param reference   Object Object referencing current action like NPC selling item or previous item in transformation
     * @return the detached item instance if operation completes successfully, {@code null} if the item does not exist in this container anymore or item count is not available
     */
    public L2ItemInstance detachItem(String process, L2ItemInstance item, long count, ItemLocation newLocation, Player actor, Object reference) {
        if (item == null) {
            return null;
        }

        synchronized (item) {
            if (!_items.containsKey(item.getObjectId())) {
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
                item = ItemTable.getInstance().createItem(process, item.getId(), count, actor, reference);
                item.setOwnerId(getOwnerId());
            }
            item.setItemLocation(newLocation);
            item.updateDatabase(true);
        }

        refreshWeight();

        return item;
    }

    /**
     * Detaches the item from this item container so it can be used as a single instance.
     *
     * @param process      string Identifier of process triggering this action
     * @param itemObjectId the item object id to be detached
     * @param count        the count of items to be detached
     * @param newLocation  the new item location
     * @param actor        Player requesting the item detach
     * @param reference    Object Object referencing current action like NPC selling item or previous item in transformation
     * @return the detached item instance if operation completes successfully, {@code null} if the item does not exist in this container anymore or item count is not available
     */
    public L2ItemInstance detachItem(String process, int itemObjectId, long count, ItemLocation newLocation, Player actor, Object reference) {
        final L2ItemInstance itemInstance = getItemByObjectId(itemObjectId);
        if (itemInstance == null) {
            return null;
        }

        return detachItem(process, itemInstance, count, newLocation, actor, reference);
    }

    /**
     * Destroy item from inventory and updates database
     *
     * @param process   : String Identifier of process triggering this action
     * @param item      : L2ItemInstance to be destroyed
     * @param actor     : Player Player requesting the item destroy
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
     */
    public L2ItemInstance destroyItem(String process, L2ItemInstance item, Player actor, Object reference) {
        return destroyItem(process, item, item.getCount(), actor, reference);
    }

    /**
     * Destroy item from inventory and updates database
     *
     * @param process   : String Identifier of process triggering this action
     * @param item      : L2ItemInstance to be destroyed
     * @param count
     * @param actor     : Player Player requesting the item destroy
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
     */
    public L2ItemInstance destroyItem(String process, L2ItemInstance item, long count, Player actor, Object reference) {
        synchronized (item) {
            // Adjust item quantity
            if (item.getCount() > count) {
                item.changeCount(process, -count, actor, reference);
                item.setLastChange(L2ItemInstance.MODIFIED);

                // don't update often for untraced items
                if ((process != null) || ((GameTimeController.getInstance().getGameTicks() % 10) == 0)) {
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

                ItemTable.getInstance().destroyItem(process, item, actor, reference);

                item.updateDatabase();
                refreshWeight();

                item.deleteMe();
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
     * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
     */
    public L2ItemInstance destroyItem(String process, int objectId, long count, Player actor, Object reference) {
        final L2ItemInstance item = getItemByObjectId(objectId);
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
     * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
     */
    public L2ItemInstance destroyItemByItemId(String process, int itemId, long count, Player actor, Object reference) {
        final L2ItemInstance item = getItemByItemId(itemId);
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
        for (L2ItemInstance item : _items.values()) {
            destroyItem(process, item, actor, reference);
        }
    }

    /**
     * @return warehouse Adena.
     */
    public long getAdena() {
        for (L2ItemInstance item : _items.values()) {
            if (item.getId() == CommonItem.ADENA) {
                return item.getCount();
            }
        }
        return 0;
    }

    public long getBeautyTickets() {
        for (L2ItemInstance item : _items.values()) {
            if (item.getId() == Inventory.BEAUTY_TICKET_ID) {
                return item.getCount();
            }
        }
        return 0;
    }

    /**
     * Adds item to inventory for further adjustments.
     *
     * @param item : L2ItemInstance to be added from inventory
     */
    protected void addItem(L2ItemInstance item) {
        _items.put(item.getObjectId(), item);
    }

    /**
     * Removes item from inventory for further adjustments.
     *
     * @param item : L2ItemInstance to be removed from inventory
     * @return
     */
    protected boolean removeItem(L2ItemInstance item) {
        return _items.remove(item.getObjectId()) != null;
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
            for (L2ItemInstance item : _items.values()) {
                item.updateDatabase(true);
                item.deleteMe();
                L2World.getInstance().removeObject(item);
            }
        }
        _items.clear();
    }

    /**
     * Update database with items in inventory
     */
    public void updateDatabase() {
        if (getOwner() != null) {
            for (L2ItemInstance item : _items.values()) {
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
                    final L2ItemInstance item = new L2ItemInstance(rs);
                    L2World.getInstance().addObject(item);

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
        final L2Item template = ItemTable.getInstance().getTemplate(itemId);
        return (template == null) || (template.isStackable() ? validateCapacity(1) : validateCapacity(count));
    }

    /**
     * @param itemId the item Id to verify
     * @param count  amount of item's weight to validate
     * @return {@code true} if the item doesn't exists or it validates its weight
     */
    public boolean validateWeightByItemId(int itemId, long count) {
        final L2Item template = ItemTable.getInstance().getTemplate(itemId);
        return (template == null) || validateWeight(template.getWeight() * count);
    }
}
