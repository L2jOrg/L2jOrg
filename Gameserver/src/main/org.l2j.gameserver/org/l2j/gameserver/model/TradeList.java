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
package org.l2j.gameserver.model;

import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.item.ItemTemplate;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExPrivateStoreBuyingResult;
import org.l2j.gameserver.network.serverpackets.ExPrivateStoreSellingResult;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.settings.AdminSettings;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author Advi
 * @author JoeAlisson
 */
public class TradeList {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradeList.class);
    public static final String PRIVATE_STORE = "PrivateStore";

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

    public synchronized TradeItem addItem(int objectId, long count, long price) {
        if (locked) {
            LOGGER.warn("{} Attempt to modify locked TradeList!", owner);
            return null;
        }

        final WorldObject object = World.getInstance().findObject(objectId);

        if(!(object instanceof Item item)) {
            LOGGER.warn("{} Trying to add something other than an item!: ObjectId {} ", owner, objectId);
            return null;
        }

        if (!validateItem(objectId, count, price, item)) {
            return null;
        }

        final TradeItem titem = new TradeItem(item, count, price);
        items.add(titem);
        invalidateConfirmation();
        return titem;
    }

    private boolean validateItem(int objectId, long count, long price, Item item) {
        if (!(item.isTradeable() || (owner.isGM() && !AdminSettings.tradeRestrictItem())) || item.isQuestItem()) {
            LOGGER.warn("{} Attempt to add a restricted item!", owner);
            return false;
        }

        if (owner.getInventory().isBlocked(item)) {
            LOGGER.warn("{} Attempt to add an item that can't manipulate!", owner);
            return false;
        }

        if ((count <= 0) || (count > item.getCount())) {
            LOGGER.warn("{} Attempt to add an item with invalid item count!", owner);
            return false;
        }

        if (!item.isStackable() && (count > 1)) {
            LOGGER.warn("{} Attempt to add non-stackable item to TradeList with count > 1!", owner);
            return false;
        }

        if (MathUtil.checkMulOverFlow(price, count, CharacterSettings.maxAdena())) {
            LOGGER.warn("{} Attempt to overflow adena !", owner);
            return false;
        }

        for (TradeItem checkitem : items) {
            if (checkitem.getObjectId() == objectId) {
                LOGGER.warn("{} Attempt to add an item that is already present!", owner);
                return false;
            }
        }
        return true;
    }

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

        if (MathUtil.checkMulOverFlow(price, count, CharacterSettings.maxAdena())) {
            LOGGER.warn("{} Attempt to overflow adena !", owner);
            return null;
        }

        final TradeItem titem = new TradeItem(item, count, price);
        items.add(titem);
        invalidateConfirmation();
        return titem;
    }

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

    private boolean validate() {
        if (owner == null || World.getInstance().findPlayer(owner.getObjectId()) == null) {
            LOGGER.warn("Invalid owner of TradeList");
            return false;
        }

        for (var tradeItem : items) {
            final var item = owner.checkItemManipulation(tradeItem.getObjectId(), tradeItem.getCount(), "transfer");
            if (item == null || item.getCount() < 1) {
                LOGGER.warn("{}: Invalid Item in TradeList", owner);
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
            final var ownerIU = new InventoryUpdate();
            final var partnerIU = new InventoryUpdate();

            partnerList.TransferItems(owner, partnerIU, ownerIU);
            TransferItems(partnerList.getOwner(), ownerIU, partnerIU);

            owner.sendInventoryUpdate(ownerIU);
            partner.sendInventoryUpdate(partnerIU);
            success = true;
        }
        // Finish the trade
        partnerList.getOwner().onTradeFinish(success);
        owner.onTradeFinish(success);
    }

    public synchronized TradeResult privateStoreBuy(Player player, Set<ItemRequest> items) {
        if(!canBuy(player)) {
            return TradeResult.CANCELED;
        }

        int slots = 0;
        int weight = 0;
        long totalPrice = 0;

        final PlayerInventory ownerInventory = owner.getInventory();
        final PlayerInventory playerInventory = player.getInventory();

        for (var itemRequested : items) {
            if (!checkExists(itemRequested)) {
                if (packaged) {
                    GameUtils.handleIllegalPlayerAction(player, "[TradeList.privateStoreBuy()]  " + player.getName() + " tried to cheat the package sell and buy only a part of the package! Ban this player for bot usage!");
                    return TradeResult.FAILED;
                }

                itemRequested.setCount(0);
                continue;
            }

            final var template =  ItemEngine.getInstance().getTemplate(itemRequested.getItemId());
            if (template == null) {
                return TradeResult.CANCELED;
            }

            totalPrice += itemRequested.getCount() * itemRequested.getPrice();

            if(!checkItemRequested(totalPrice, itemRequested)) {
                return TradeResult.CANCELED;
            }

            weight += itemRequested.getCount() * template.getWeight();

            if (!template.isStackable()) {
                slots += itemRequested.getCount();
            } else if (playerInventory.getItemByItemId(itemRequested.getItemId()) == null) {
                slots++;
            }
        }

        if(!checkTransaction(player, slots, weight, totalPrice, playerInventory)) {
            return TradeResult.CANCELED;
        }

        return doTransaction(player, items, totalPrice, ownerInventory, playerInventory);
    }

    private TradeResult doTransaction(Player player, Set<ItemRequest> items, long totalPrice, PlayerInventory ownerInventory, PlayerInventory playerInventory) {
        final var ownerIU = new InventoryUpdate();
        final var playerIU = new InventoryUpdate();

        playerIU.addItem(playerInventory.getAdenaInstance());
        ownerInventory.addAdena(PRIVATE_STORE, totalPrice, owner, player);

        var result = TradeResult.OK;

        for (ItemRequest item : items) {
            if (item.getCount() == 0) {
                continue;
            }

            // Check if requested item is available for manipulation
            final Item oldItem = owner.checkItemManipulation(item.getObjectId(), item.getCount(), "sell");
            if (oldItem == null) {
                // should not happens - validation already done
                lock();
                result = TradeResult.FAILED;
                break;
            }

            // Proceed with item transfer
            final Item newItem = ownerInventory.transferItem(PRIVATE_STORE, item.getObjectId(), item.getCount(), playerInventory, owner, player);
            if (newItem == null) {
                result = TradeResult.FAILED;
                break;
            }
            removeItem(item.getObjectId(), -1, item.getCount());

            addItemToInventoryUpdate(ownerIU, playerIU, item, oldItem, newItem);

            sendTransactionMessage(player, item, newItem);
            owner.sendPacket(new ExPrivateStoreSellingResult(item.getObjectId(), item.getCount(), player.getAppearance().getVisibleName()));
        }

        owner.sendInventoryUpdate(ownerIU);
        player.sendInventoryUpdate(playerIU);
        return result;
    }

    private boolean checkTransaction(Player player, int slots, int weight, long totalPrice, PlayerInventory playerInventory) {
        if (totalPrice > playerInventory.getAdena()) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            return false;
        }

        if (!playerInventory.validateWeight(weight)) {
            player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            return false;
        }

        if (!playerInventory.validateCapacity(slots)) {
            player.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
            return false;
        }

        if (!playerInventory.reduceAdena(PRIVATE_STORE, totalPrice, player, owner)) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            return false;
        }
        return true;
    }

    private boolean checkItemRequested(long totalPrice, ItemRequest itemRequested) {
        if (MathUtil.checkMulOverFlow(itemRequested.getPrice(), itemRequested.getCount(), CharacterSettings.maxAdena())) {
            lock();
            return false;
        }

        if ((CharacterSettings.maxAdena() < totalPrice) || (totalPrice < 0)) {
            lock();
            return false;
        }

        final var oldItem = owner.checkItemManipulation(itemRequested.getObjectId(), itemRequested.getCount(), "sell");
        if (oldItem == null || !oldItem.isTradeable()) {
            lock();
            return false;
        }
        return true;
    }

    private boolean checkExists(ItemRequest itemRequested) {
        boolean found = false;

        for (var item : items) {
            if (item.getObjectId() == itemRequested.getObjectId()) {
                if (item.getPrice() == itemRequested.getPrice()) {
                    if (item.getCount() < itemRequested.getCount()) {
                        itemRequested.setCount(item.getCount());
                    }
                    found = true;
                }
                break;
            }
        }
        return found;
    }

    private boolean canBuy(Player player) {
        if (locked) {
            return false;
        }

        if (!validate()) {
            lock();
            return false;
        }

        return owner.isOnline() && player.isOnline();
    }

    private void addItemToInventoryUpdate(InventoryUpdate ownerIU, InventoryUpdate playerIU, ItemRequest item, Item oldItem, Item newItem) {
        if (oldItem.getCount() > 0 && oldItem != newItem) {
            ownerIU.addModifiedItem(oldItem);
        } else {
            ownerIU.addRemovedItem(oldItem);
        }
        if (newItem.getCount() > item.getCount()) {
            playerIU.addModifiedItem(newItem);
        } else {
            playerIU.addNewItem(newItem);
        }
    }

    private void sendTransactionMessage(Player player, ItemRequest item, Item newItem) {
        var msg = getSystemMessage(SystemMessageId.C1_PURCHASED_S3_S2_S).addString(player.getName()).addItemName(newItem);
        if(newItem.isStackable()) {
            msg.addLong(item.getCount());
        }
        owner.sendPacket(msg);

        msg = getSystemMessage(SystemMessageId.YOU_HAVE_PURCHASED_S2_FROM_C1).addString(owner.getName()).addItemName(newItem);
        if(newItem.isStackable()) {
            msg.addLong(item.getCount());
        }
        player.sendPacket(msg);
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

        final PlayerInventory playerInventory = player.getInventory();

        final InventoryUpdate ownerIU = new InventoryUpdate();
        final InventoryUpdate playerIU = new InventoryUpdate();

        long totalPrice = 0;

        final TradeItem[] sellerItems = items.toArray(new TradeItem[0]);
        var ok = false;
        for (ItemRequest itemRequest : requestedItems) {
            if(!checkExists(itemRequest)) {
                continue;
            }

            final long _totalPrice = totalPrice + itemRequest.getCount() * itemRequest.getPrice();

            if (requestPriceOverflow(itemRequest, _totalPrice)) {
                break;
            }

            var oldItem = itemRequestToItem(player, sellerItems, itemRequest, _totalPrice);
            if (oldItem == null) {
                continue;
            }

            if (oldItem.getId() != itemRequest.getItemId()) {
                GameUtils.handleIllegalPlayerAction(player, player + " is cheating with sell items");
                return false;
            }

            final var newItem = playerInventory.transferItem(PRIVATE_STORE, oldItem.getObjectId(), itemRequest.getCount(), owner.getInventory(), player, owner);
            if (newItem == null) {
                continue;
            }

            ok = true;
            removeItem(-1, itemRequest.getItemId(), itemRequest.getCount());
            totalPrice = _totalPrice;

            addItemToInventoryUpdate(playerIU, ownerIU, itemRequest, oldItem, newItem);
            sendTransactionMessage(player, itemRequest, newItem);
            owner.sendPacket(new ExPrivateStoreBuyingResult(newItem.getObjectId(), itemRequest.getCount(), player.getAppearance().getVisibleName()));
        }

        if(!ok) {
            return false;
        }

        return chargeTransaction(player, playerInventory, ownerIU, playerIU, totalPrice);
    }

    private Item itemRequestToItem(Player player, TradeItem[] sellerItems, ItemRequest itemRequest, long totalPrice) {
        final var tradeItem = itemRequestToTradeItem(sellerItems, itemRequest, totalPrice);
        if (tradeItem == null) {
            return null;
        }

        // Check if requested item is available for manipulation
        int objectId = tradeItem.getObjectId();
        var oldItem = player.checkItemManipulation(objectId, itemRequest.getCount(), "sell");
        // private store - buy use same objectId for buying several non-stackable items
        if (oldItem == null) {
            // searching other items using same itemId
            oldItem = player.getInventory().getItemByItemId(itemRequest.getItemId());
            if (oldItem == null) {
                return null;
            }
            objectId = oldItem.getObjectId();
            oldItem = player.checkItemManipulation(objectId, itemRequest.getCount(), "sell");
            if (oldItem == null) {
                return null;
            }
        }

        if (!oldItem.isTradeable()) {
            return null;
        }
        return oldItem;
    }

    private boolean chargeTransaction(Player player, PlayerInventory playerInventory, InventoryUpdate ownerIU, InventoryUpdate playerIU, long totalPrice) {
        if (totalPrice > 0) {
            var ownerInventory = owner.getInventory();
            if (totalPrice > ownerInventory.getAdena()) {
                return false;
            }
            final var adenaItem = ownerInventory.getAdenaInstance();
            ownerInventory.reduceAdena(PRIVATE_STORE, totalPrice, owner, player);
            ownerIU.addItem(adenaItem);
            playerInventory.addAdena(PRIVATE_STORE, totalPrice, player, owner);
            playerIU.addItem(playerInventory.getAdenaInstance());
        }
        owner.sendInventoryUpdate(ownerIU);
        player.sendInventoryUpdate(playerIU);
        return true;
    }

    private TradeItem itemRequestToTradeItem(TradeItem[] sellerItems, ItemRequest itemRequest, long totalPrice) {
        if (owner.getInventory().getAdena() < totalPrice) {
            return null;
        }

        if (itemRequest.getObjectId() < 1 || itemRequest.getObjectId() > sellerItems.length) {
            return null;
        }

        final var tradeItem = sellerItems[itemRequest.getObjectId() - 1];
        if (tradeItem == null || tradeItem.getItem().getId() != itemRequest.getItemId()) {
            return null;
        }
        return tradeItem;
    }

    private boolean requestPriceOverflow(ItemRequest itemRequest, long totalPrice) {
        if (MathUtil.checkMulOverFlow(itemRequest.getPrice(), itemRequest.getCount(), CharacterSettings.maxAdena())) {
            lock();
            return true;
        }

        if (CharacterSettings.maxAdena() < totalPrice || totalPrice < 0) {
            lock();
            return true;
        }
        return false;
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

    public enum TradeResult {
        OK,
        CANCELED,
        FAILED
    }
}
