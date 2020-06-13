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
package org.l2j.gameserver.model;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExPrivateStoreBuyingResult;
import org.l2j.gameserver.network.serverpackets.ExPrivateStoreSellingResult;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.l2j.gameserver.util.GameUtils.isItem;

/**
 * @author Advi
 * @author JoeAlisson
 */
public class TradeList {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradeList.class);

    private final Player owner;
    private final Set<TradeItem> items = ConcurrentHashMap.newKeySet();
    private Player partner;
    private String title;
    private boolean packaged;

    private boolean confirmed = false;
    private boolean locked = false;

    public TradeList(Player owner, Player partner) {
        this.owner = owner;
        this.partner = partner;
    }

    public TradeList(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public Player getPartner() {
        return partner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isPackaged() {
        return packaged;
    }

    public void setPackaged(boolean value) {
        packaged = value;
    }

    /**
     * @return all items from TradeList
     */
    public TradeItem[] getItems() {
        return items.toArray(TradeItem[]::new);
    }

    /**
     * Returns the list of items in inventory available for transaction
     *
     * @param inventory
     * @return Item : items in inventory
     */
    public Collection<TradeItem> getAvailableItems(PlayerInventory inventory) {
        final List<TradeItem> list = new LinkedList<>();
        for (TradeItem item : items) {
            item = new TradeItem(item, item.getCount(), item.getPrice());
            inventory.adjustAvailableItem(item);
            list.add(item);
        }
        return list;
    }

    /**
     * @return Item List size
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * Adjust available item from Inventory by the one in this list
     *
     * @param item : Item to be adjusted
     * @return TradeItem representing adjusted item
     */
    public TradeItem adjustAvailableItem(Item item) {
        if (item.isStackable()) {
            for (TradeItem exclItem : items) {
                if (exclItem.getItem().getId() == item.getId()) {
                    return item.getCount() <= exclItem.getCount() ? null : new TradeItem(item, item.getCount() - exclItem.getCount(), item.getReferencePrice());
                }
            }
        }
        return new TradeItem(item, item.getCount(), item.getReferencePrice());
    }

    /**
     * Add simplified item to TradeList
     *
     * @param objectId : int
     * @param count    : int
     * @return
     */
    public TradeItem addItem(int objectId, long count) {
        return addItem(objectId, count, 0);
    }

    /**
     * Add item to TradeList
     *
     * @param objectId : int
     * @param count    : long
     * @param price    : long
     * @return
     */
    public synchronized TradeItem addItem(int objectId, long count, long price) {
        if (locked) {
            LOGGER.warn("{} Attempt to modify locked TradeList!", owner);
            return null;
        }

        final WorldObject o = World.getInstance().findObject(objectId);
        if (!isItem(o)) {
            LOGGER.warn("{} Trying to add something other than an item!: ObjectId {} ", owner, objectId);
            return null;
        }

        final Item item = (Item) o;
        if (!(item.isTradeable() || (owner.isGM() && Config.GM_TRADE_RESTRICTED_ITEMS)) || item.isQuestItem()) {
            LOGGER.warn("{} Attempt to add a restricted item!", owner);
            return null;
        }

        if (owner.getInventory().isBlocked(item)) {
            LOGGER.warn("{} Attempt to add an item that can't manipulate!", owner);
            return null;
        }

        if ((count <= 0) || (count > item.getCount())) {
            LOGGER.warn("{} Attempt to add an item with invalid item count!", owner);
            return null;
        }

        if (!item.isStackable() && (count > 1)) {
            LOGGER.warn("{} Attempt to add non-stackable item to TradeList with count > 1!", owner);
            return null;
        }

        if (Inventory.MAX_ADENA / count < price) {
            LOGGER.warn("{} Attempt to overflow adena !", owner);
            return null;
        }

        for (TradeItem checkitem : items) {
            if (checkitem.getObjectId() == objectId) {
                LOGGER.warn("{} Attempt to add an item that is already present!", owner);
                return null;
            }
        }

        final TradeItem titem = new TradeItem(item, count, price);
        items.add(titem);

        // If Player has already confirmed this trade, invalidate the confirmation
        invalidateConfirmation();
        return titem;
    }

    /**
     * Add item to TradeList
     *
     * @param itemId
     * @param count
     * @param price
     * @return
     */
    public synchronized TradeItem addItemByItemId(int itemId, long count, long price) {
        if (locked) {
            LOGGER.warn("{} Attempt to modify locked TradeList!", owner);
            return null;
        }

        final ItemTemplate item = ItemEngine.getInstance().getTemplate(itemId);
        if (item == null) {
            LOGGER.warn("{} Attempt to add invalid item to TradeList!", owner);
            return null;
        }

        if (!item.isTradeable() || item.isQuestItem()) {
            return null;
        }

        if (!item.isStackable() && (count > 1)) {
            LOGGER.warn("{} Attempt to add non-stackable item to TradeList with count > 1!", owner);
            return null;
        }

        if ((Inventory.MAX_ADENA / count) < price) {
            LOGGER.warn("{} Attempt to overflow adena !", owner);
            return null;
        }

        final TradeItem titem = new TradeItem(item, count, price);
        items.add(titem);

        // If Player has already confirmed this trade, invalidate the confirmation
        invalidateConfirmation();
        return titem;
    }

    /**
     * Remove item from TradeList
     *
     * @param objectId : int
     * @param itemId
     * @param count    : int
     * @return
     */
    private synchronized TradeItem removeItem(int objectId, int itemId, long count) {
        if (locked) {
            LOGGER.warn(owner.getName() + ": Attempt to modify locked TradeList!");
            return null;
        }

        if (count < 0) {
            LOGGER.warn(owner.getName() + ": Attempt to remove " + count + " items from TradeList!");
            return null;
        }

        for (TradeItem titem : items) {
            if ((titem.getObjectId() == objectId) || (titem.getItem().getId() == itemId)) {
                // If Partner has already confirmed this trade, invalidate the confirmation
                if (partner != null) {
                    final TradeList partnerList = partner.getActiveTradeList();
                    if (partnerList == null) {
                        LOGGER.warn(partner.getName() + ": Trading partner (" + partner.getName() + ") is invalid in this trade!");
                        return null;
                    }
                    partnerList.invalidateConfirmation();
                }

                // Reduce item count or complete item
                if ((count != -1) && (titem.getCount() > count)) {
                    titem.setCount(titem.getCount() - count);
                } else {
                    items.remove(titem);
                }

                return titem;
            }
        }
        return null;
    }

    /**
     * Update items in TradeList according their quantity in owner inventory
     */
    public synchronized void updateItems() {
        for (TradeItem titem : items) {
            final Item item = owner.getInventory().getItemByObjectId(titem.getObjectId());
            if ((item == null) || (titem.getCount() < 1)) {
                removeItem(titem.getObjectId(), -1, -1);
            } else if (item.getCount() < titem.getCount()) {
                titem.setCount(item.getCount());
            }
        }
    }

    /**
     * Lockes TradeList, no further changes are allowed
     */
    public void lock() {
        locked = true;
    }

    /**
     * Clears item list
     */
    public synchronized void clear() {
        items.clear();
        locked = false;
    }

    /**
     * Confirms TradeList
     *
     * @return : boolean
     */
    public boolean confirm() {
        if (confirmed) {
            return true; // Already confirmed
        }

        // If Partner has already confirmed this trade, proceed exchange
        if (partner != null) {
            final TradeList partnerList = partner.getActiveTradeList();
            if (partnerList == null) {
                LOGGER.warn(partner.getName() + ": Trading partner (" + partner.getName() + ") is invalid in this trade!");
                return false;
            }

            // Synchronization order to avoid deadlock
            TradeList sync1;
            TradeList sync2;
            if (getOwner().getObjectId() > partnerList.getOwner().getObjectId()) {
                sync1 = partnerList;
                sync2 = this;
            } else {
                sync1 = this;
                sync2 = partnerList;
            }

            synchronized (sync1) {
                synchronized (sync2) {
                    confirmed = true;
                    if (partnerList.isConfirmed()) {
                        partnerList.lock();
                        lock();
                        if (!partnerList.validate() || !validate()) {
                            return false;
                        }

                        doExchange(partnerList);
                    } else {
                        partner.onTradeConfirm(owner);
                    }
                }
            }
        } else {
            confirmed = true;
        }

        return confirmed;
    }

    /**
     * Cancels TradeList confirmation
     */
    private void invalidateConfirmation() {
        confirmed = false;
    }

    /**
     * Validates TradeList with owner inventory
     *
     * @return
     */
    private boolean validate() {
        // Check for Owner validity
        if ((owner == null) || (World.getInstance().findPlayer(owner.getObjectId()) == null)) {
            LOGGER.warn("Invalid owner of TradeList");
            return false;
        }

        // Check for Item validity
        for (TradeItem titem : items) {
            final Item item = owner.checkItemManipulation(titem.getObjectId(), titem.getCount(), "transfer");
            if ((item == null) || (item.getCount() < 1)) {
                LOGGER.warn(owner.getName() + ": Invalid Item in TradeList");
                return false;
            }
        }

        return true;
    }

    /**
     * Transfers all TradeItems from inventory to partner
     *
     * @param partner
     * @param ownerIU
     * @param partnerIU
     * @return
     */
    private boolean TransferItems(Player partner, InventoryUpdate ownerIU, InventoryUpdate partnerIU) {
        for (TradeItem titem : items) {
            final Item oldItem = owner.getInventory().getItemByObjectId(titem.getObjectId());
            if (oldItem == null) {
                return false;
            }
            final Item newItem = owner.getInventory().transferItem("Trade", titem.getObjectId(), titem.getCount(), partner.getInventory(), owner, this.partner);
            if (newItem == null) {
                return false;
            }

            // Add changes to inventory update packets
            if (ownerIU != null) {
                if ((oldItem.getCount() > 0) && (oldItem != newItem)) {
                    ownerIU.addModifiedItem(oldItem);
                } else {
                    ownerIU.addRemovedItem(oldItem);
                }
            }

            if (partnerIU != null) {
                if (newItem.getCount() > titem.getCount()) {
                    partnerIU.addModifiedItem(newItem);
                } else {
                    partnerIU.addNewItem(newItem);
                }
            }
        }
        return true;
    }

    /**
     * @param partner
     * @return item slots count
     */
    private int countItemsSlots(Player partner) {
        int slots = 0;

        for (TradeItem item : items) {
            if (item == null) {
                continue;
            }
            final ItemTemplate template = ItemEngine.getInstance().getTemplate(item.getItem().getId());
            if (template == null) {
                continue;
            }
            if (!template.isStackable()) {
                slots += item.getCount();
            } else if (partner.getInventory().getItemByItemId(item.getItem().getId()) == null) {
                slots++;
            }
        }

        return slots;
    }

    /**
     * @return the weight of items in tradeList
     */
    private int calcItemsWeight() {
        long weight = 0;

        for (TradeItem item : items) {
            if (item == null) {
                continue;
            }
            final ItemTemplate template = ItemEngine.getInstance().getTemplate(item.getItem().getId());
            if (template == null) {
                continue;
            }
            weight += item.getCount() * template.getWeight();
        }

        return (int) Math.min(weight, Integer.MAX_VALUE);
    }

    /**
     * Proceeds with trade
     *
     * @param partnerList
     */
    private void doExchange(TradeList partnerList) {
        boolean success = false;

        // check weight and slots
        if ((!owner.getInventory().validateWeight(partnerList.calcItemsWeight())) || !(partnerList.getOwner().getInventory().validateWeight(calcItemsWeight()))) {
            partnerList.getOwner().sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            owner.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
        } else if ((!owner.getInventory().validateCapacity(partnerList.countItemsSlots(getOwner()))) || (!partnerList.getOwner().getInventory().validateCapacity(countItemsSlots(partnerList.getOwner())))) {
            partnerList.getOwner().sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
            owner.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
        } else {
            // Prepare inventory update packet
            final InventoryUpdate ownerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
            final InventoryUpdate partnerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();

            // Transfer items
            partnerList.TransferItems(owner, partnerIU, ownerIU);
            TransferItems(partnerList.getOwner(), ownerIU, partnerIU);

            // Send inventory update packet
            if (ownerIU != null) {
                owner.sendInventoryUpdate(ownerIU);
            } else {
                owner.sendItemList();
            }

            if (partnerIU != null) {
                partner.sendInventoryUpdate(partnerIU);
            } else {
                partner.sendItemList();
            }
            success = true;
        }
        // Finish the trade
        partnerList.getOwner().onTradeFinish(success);
        owner.onTradeFinish(success);
    }

    /**
     * Buy items from this PrivateStore list
     *
     * @param player
     * @param items
     * @return int: result of trading. 0 - ok, 1 - canceled (no adena), 2 - failed (item error)
     */
    public synchronized int privateStoreBuy(Player player, Set<ItemRequest> items) {
        if (locked) {
            return 1;
        }

        if (!validate()) {
            lock();
            return 1;
        }

        if (!owner.isOnline() || !player.isOnline()) {
            return 1;
        }

        int slots = 0;
        int weight = 0;
        long totalPrice = 0;

        final PlayerInventory ownerInventory = owner.getInventory();
        final PlayerInventory playerInventory = player.getInventory();

        for (ItemRequest item : items) {
            boolean found = false;

            for (TradeItem ti : this.items) {
                if (ti.getObjectId() == item.getObjectId()) {
                    if (ti.getPrice() == item.getPrice()) {
                        if (ti.getCount() < item.getCount()) {
                            item.setCount(ti.getCount());
                        }
                        found = true;
                    }
                    break;
                }
            }
            // item with this objectId and price not found in tradelist
            if (!found) {
                if (packaged) {
                    GameUtils.handleIllegalPlayerAction(player, "[TradeList.privateStoreBuy()] Player " + player.getName() + " tried to cheat the package sell and buy only a part of the package! Ban this player for bot usage!");
                    return 2;
                }

                item.setCount(0);
                continue;
            }

            // check for overflow in the single item
            if ((Inventory.MAX_ADENA / item.getCount()) < item.getPrice()) {
                // private store attempting to overflow - disable it
                lock();
                return 1;
            }

            totalPrice += item.getCount() * item.getPrice();
            // check for overflow of the total price
            if ((Inventory.MAX_ADENA < totalPrice) || (totalPrice < 0)) {
                // private store attempting to overflow - disable it
                lock();
                return 1;
            }

            // Check if requested item is available for manipulation
            final Item oldItem = owner.checkItemManipulation(item.getObjectId(), item.getCount(), "sell");
            if ((oldItem == null) || !oldItem.isTradeable()) {
                // private store sell invalid item - disable it
                lock();
                return 2;
            }

            final ItemTemplate template = ItemEngine.getInstance().getTemplate(item.getItemId());
            if (template == null) {
                continue;
            }
            weight += item.getCount() * template.getWeight();
            if (!template.isStackable()) {
                slots += item.getCount();
            } else if (playerInventory.getItemByItemId(item.getItemId()) == null) {
                slots++;
            }
        }

        if (totalPrice > playerInventory.getAdena()) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            return 1;
        }

        if (!playerInventory.validateWeight(weight)) {
            player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            return 1;
        }

        if (!playerInventory.validateCapacity(slots)) {
            player.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
            return 1;
        }

        // Prepare inventory update packets
        final InventoryUpdate ownerIU = new InventoryUpdate();
        final InventoryUpdate playerIU = new InventoryUpdate();

        final Item adenaItem = playerInventory.getAdenaInstance();
        if (!playerInventory.reduceAdena("PrivateStore", totalPrice, player, owner)) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            return 1;
        }
        playerIU.addItem(adenaItem);
        ownerInventory.addAdena("PrivateStore", totalPrice, owner, player);
        // ownerIU.addItem(ownerInventory.getAdenaInstance());

        boolean ok = true;

        // Transfer items
        for (ItemRequest item : items) {
            if (item.getCount() == 0) {
                continue;
            }

            // Check if requested item is available for manipulation
            final Item oldItem = owner.checkItemManipulation(item.getObjectId(), item.getCount(), "sell");
            if (oldItem == null) {
                // should not happens - validation already done
                lock();
                ok = false;
                break;
            }

            // Proceed with item transfer
            final Item newItem = ownerInventory.transferItem("PrivateStore", item.getObjectId(), item.getCount(), playerInventory, owner, player);
            if (newItem == null) {
                ok = false;
                break;
            }
            removeItem(item.getObjectId(), -1, item.getCount());

            // Add changes to inventory update packets
            if ((oldItem.getCount() > 0) && (oldItem != newItem)) {
                ownerIU.addModifiedItem(oldItem);
            } else {
                ownerIU.addRemovedItem(oldItem);
            }
            if (newItem.getCount() > item.getCount()) {
                playerIU.addModifiedItem(newItem);
            } else {
                playerIU.addNewItem(newItem);
            }

            // Send messages about the transaction to both players
            if (newItem.isStackable()) {
                SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_PURCHASED_S3_S2_S);
                msg.addString(player.getName());
                msg.addItemName(newItem);
                msg.addLong(item.getCount());
                owner.sendPacket(msg);

                msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_PURCHASED_S3_S2_S_FROM_C1);
                msg.addString(owner.getName());
                msg.addItemName(newItem);
                msg.addLong(item.getCount());
                player.sendPacket(msg);
            } else {
                SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_PURCHASED_S2);
                msg.addString(player.getName());
                msg.addItemName(newItem);
                owner.sendPacket(msg);

                msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_PURCHASED_S2_FROM_C1);
                msg.addString(owner.getName());
                msg.addItemName(newItem);
                player.sendPacket(msg);
            }

            owner.sendPacket(new ExPrivateStoreSellingResult(item.getObjectId(), item.getCount(), player.getAppearance().getVisibleName()));
        }

        // Send inventory update packet
        owner.sendInventoryUpdate(ownerIU);
        player.sendInventoryUpdate(playerIU);
        return ok ? 0 : 2;
    }

    /**
     * Sell items to this PrivateStore list
     *
     * @param player
     * @param requestedItems
     * @return : boolean true if success
     */
    public synchronized boolean privateStoreSell(Player player, ItemRequest[] requestedItems) {
        if (locked || !owner.isOnline() || !player.isOnline()) {
            return false;
        }

        boolean ok = false;

        final PlayerInventory ownerInventory = owner.getInventory();
        final PlayerInventory playerInventory = player.getInventory();

        // Prepare inventory update packet
        final InventoryUpdate ownerIU = new InventoryUpdate();
        final InventoryUpdate playerIU = new InventoryUpdate();

        long totalPrice = 0;

        final TradeItem[] sellerItems = items.toArray(new TradeItem[0]);

        for (ItemRequest item : requestedItems) {
            // searching item in tradelist using itemId
            boolean found = false;

            for (TradeItem ti : sellerItems) {
                if (ti.getItem().getId() == item.getItemId()) {
                    // price should be the same
                    if (ti.getPrice() == item.getPrice()) {
                        // if requesting more than available - decrease count
                        if (ti.getCount() < item.getCount()) {
                            item.setCount(ti.getCount());
                        }
                        found = item.getCount() > 0;
                    }
                    break;
                }
            }
            // not found any item in the tradelist with same itemId and price
            // maybe another player already sold this item ?
            if (!found) {
                continue;
            }

            // check for overflow in the single item
            if ((Inventory.MAX_ADENA / item.getCount()) < item.getPrice()) {
                lock();
                break;
            }

            final long _totalPrice = totalPrice + (item.getCount() * item.getPrice());
            // check for overflow of the total price
            if ((Inventory.MAX_ADENA < _totalPrice) || (_totalPrice < 0)) {
                lock();
                break;
            }

            if (ownerInventory.getAdena() < _totalPrice) {
                continue;
            }

            if ((item.getObjectId() < 1) || (item.getObjectId() > sellerItems.length)) {
                continue;
            }

            final TradeItem tradeItem = sellerItems[item.getObjectId() - 1];
            if ((tradeItem == null) || (tradeItem.getItem().getId() != item.getItemId())) {
                continue;
            }

            // Check if requested item is available for manipulation
            int objectId = tradeItem.getObjectId();
            Item oldItem = player.checkItemManipulation(objectId, item.getCount(), "sell");
            // private store - buy use same objectId for buying several non-stackable items
            if (oldItem == null) {
                // searching other items using same itemId
                oldItem = playerInventory.getItemByItemId(item.getItemId());
                if (oldItem == null) {
                    continue;
                }
                objectId = oldItem.getObjectId();
                oldItem = player.checkItemManipulation(objectId, item.getCount(), "sell");
                if (oldItem == null) {
                    continue;
                }
            }
            if (oldItem.getId() != item.getItemId()) {
                GameUtils.handleIllegalPlayerAction(player, player + " is cheating with sell items");
                return false;
            }

            if (!oldItem.isTradeable()) {
                continue;
            }

            // Proceed with item transfer
            final Item newItem = playerInventory.transferItem("PrivateStore", objectId, item.getCount(), ownerInventory, player, owner);
            if (newItem == null) {
                continue;
            }

            removeItem(-1, item.getItemId(), item.getCount());
            ok = true;

            // increase total price only after successful transaction
            totalPrice = _totalPrice;

            // Add changes to inventory update packets
            if ((oldItem.getCount() > 0) && (oldItem != newItem)) {
                playerIU.addModifiedItem(oldItem);
            } else {
                playerIU.addRemovedItem(oldItem);
            }
            if (newItem.getCount() > item.getCount()) {
                ownerIU.addModifiedItem(newItem);
            } else {
                ownerIU.addNewItem(newItem);
            }

            // Send messages about the transaction to both players
            if (newItem.isStackable()) {
                SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_PURCHASED_S3_S2_S_FROM_C1);
                msg.addString(player.getName());
                msg.addItemName(newItem);
                msg.addLong(item.getCount());
                owner.sendPacket(msg);

                msg = SystemMessage.getSystemMessage(SystemMessageId.C1_PURCHASED_S3_S2_S);
                msg.addString(owner.getName());
                msg.addItemName(newItem);
                msg.addLong(item.getCount());
                player.sendPacket(msg);
            } else {
                SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_PURCHASED_S2_FROM_C1);
                msg.addString(player.getName());
                msg.addItemName(newItem);
                owner.sendPacket(msg);

                msg = SystemMessage.getSystemMessage(SystemMessageId.C1_PURCHASED_S2);
                msg.addString(owner.getName());
                msg.addItemName(newItem);
                player.sendPacket(msg);
            }

            owner.sendPacket(new ExPrivateStoreBuyingResult(item.getObjectId(), item.getCount(), player.getAppearance().getVisibleName()));
        }

        if (totalPrice > 0) {
            // Transfer adena
            if (totalPrice > ownerInventory.getAdena()) {
                // should not happens, just a precaution
                return false;
            }
            final Item adenaItem = ownerInventory.getAdenaInstance();
            ownerInventory.reduceAdena("PrivateStore", totalPrice, owner, player);
            ownerIU.addItem(adenaItem);
            playerInventory.addAdena("PrivateStore", totalPrice, player, owner);
            playerIU.addItem(playerInventory.getAdenaInstance());
        }

        if (ok) {
            // Send inventory update packet
            owner.sendInventoryUpdate(ownerIU);
            player.sendInventoryUpdate(playerIU);
        }
        return ok;
    }
}
