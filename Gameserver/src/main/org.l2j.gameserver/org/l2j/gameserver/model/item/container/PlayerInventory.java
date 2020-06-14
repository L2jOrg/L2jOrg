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

import io.github.joealisson.primitive.IntCollection;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.api.item.PlayerInventoryListener;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.InventoryBlockType;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerItemAdd;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerItemDestroy;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerItemDrop;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerItemTransfer;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.model.item.type.EtcItemType.ARROW;
import static org.l2j.gameserver.model.item.type.EtcItemType.BOLT;

/**
 * @author JoeAlisson
 */
public class PlayerInventory extends Inventory {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerInventory.class);

    private final Player owner;
    private Item _adena;
    private Item _ancientAdena;
    private Item _beautyTickets;
    private Item silverCoin;
    private Item rustyCoin;
    private Item l2Coin;
    private Item currentAmmunition;

    private IntCollection blockItems = null;
    private InventoryBlockType blockMode = InventoryBlockType.NONE;

    public PlayerInventory(Player owner) {
        this.owner = owner;
        ServiceLoader.load(PlayerInventoryListener.class).forEach(this::addPaperdollListener);
    }

    public static int[][] restoreVisibleInventory(int objectId) {
        final int[][] paperdoll = new int[InventorySlot.TOTAL_SLOTS][3];
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement2 = con.prepareStatement("SELECT object_id,item_id,loc_data,enchant_level FROM items WHERE owner_id=? AND loc='PAPERDOLL'")) {
            statement2.setInt(1, objectId);
            try (ResultSet invdata = statement2.executeQuery()) {
                while (invdata.next()) {
                    final int slot = invdata.getInt("loc_data");
                    paperdoll[slot][0] = invdata.getInt("object_id");
                    paperdoll[slot][1] = invdata.getInt("item_id");
                    paperdoll[slot][2] = invdata.getInt("enchant_level");
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Could not restore inventory: " + e.getMessage(), e);
        }
        return paperdoll;
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    protected ItemLocation getBaseLocation() {
        return ItemLocation.INVENTORY;
    }

    @Override
    protected ItemLocation getEquipLocation() {
        return ItemLocation.PAPERDOLL;
    }

    public Item getAdenaInstance() {
        return _adena;
    }

    @Override
    public long getAdena() {
        return _adena != null ? _adena.getCount() : 0;
    }

    public Item getAncientAdenaInstance() {
        return _ancientAdena;
    }

    public long getAncientAdena() {
        return (_ancientAdena != null) ? _ancientAdena.getCount() : 0;
    }

    public Item getBeautyTicketsInstance() {
        return _beautyTickets;
    }

    @Override
    public long getBeautyTickets() {
        return _beautyTickets != null ? _beautyTickets.getCount() : 0;
    }

    /**
     * Returns the list of items in inventory available for transaction
     * @return Item : items in inventory
     */
    public Collection<Item> getUniqueItems(boolean allowAdena, boolean allowAncientAdena) {
        return getUniqueItems(allowAdena, allowAncientAdena, true);
    }

    public Collection<Item> getUniqueItems(boolean allowAdena, boolean allowAncientAdena, boolean onlyAvailable) {
        final Collection<Item> list = new LinkedList<>();
        for (Item item : items.values()) {
            if (!allowAdena && (item.getId() == CommonItem.ADENA)) {
                continue;
            }
            if (!allowAncientAdena && (item.getId() == CommonItem.ANCIENT_ADENA)) {
                continue;
            }
            boolean isDuplicate = false;
            for (Item litem : list) {
                if (litem.getId() == item.getId()) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate && (!onlyAvailable || (item.isSellable() && item.isAvailable(owner, false, false)))) {
                list.add(item);
            }
        }

        return list;
    }


    public Collection<Item> getAllItemsByItemId(int itemId, int enchantment) {
        return getAllItemsByItemId(itemId, enchantment, true);
    }

    /**
     * Returns the list of all items in inventory that have a given item id AND a given enchantment level.
     *
     * @param itemId          : ID of item
     * @param enchantment     : enchant level of item
     * @param includeEquipped : include equipped items
     * @return Item[] : matching items from inventory
     */
    public Collection<Item> getAllItemsByItemId(int itemId, int enchantment, boolean includeEquipped) {
        return getItems(i -> (i.getId() == itemId) && (i.getEnchantLevel() == enchantment) && (includeEquipped || !i.isEquipped()));
    }

    /**
     * @return the list of items in inventory available for transaction
     */
    public Collection<Item> getAvailableItems(boolean allowAdena, boolean allowNonTradeable, boolean feightable) {
        return getItems(i -> {

            if (!i.isAvailable(owner, allowAdena, allowNonTradeable) || isBlocked(i)) {
                return false;
            } else if (feightable) {
                return (i.getItemLocation() == ItemLocation.INVENTORY) && i.isFreightable();
            }
            return true;
        });
    }

    public Collection<Item> getDepositableItems(WarehouseType type) {
        return items.values().stream()
                .filter(item -> isDepositable(item, type))
                .collect(Collectors.toSet());
    }

    private boolean isDepositable(Item item, WarehouseType type) {
        return item.isDepositable(type) && !isBlocked(item) && isNotInUse(item);
    }

    public boolean isNotInUse(Item item) {
        final var pet = owner.getPet();
        return !item.isEquipped() && !owner.isProcessingItem(item.getObjectId())
                && (isNull(pet) || pet.getControlObjectId() != item.getObjectId())
                && !owner.isCastingNow(s -> s.getSkill().getItemConsumeId() != item.getId());
    }

    public final boolean haveItemForSelfResurrection() {
        return items.values().stream().anyMatch(Item::isSelfResurrection);
    }

    /**
     * Returns the list of items in inventory available for transaction adjusted by tradeList
     *
     * @return Item : item in inventory
     */
    public Collection<TradeItem> getAvailableItems(TradeList tradeList) {
        //@formatter:off
        return items.values().stream()
                .filter(i -> i.isAvailable(owner, false, false))
                .map(tradeList::adjustAvailableItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedList::new));
        //@formatter:on
    }

    /**
     * Adjust TradeItem according his status in inventory
     *
     * @param item : Item to be adjusted
     */
    public void adjustAvailableItem(TradeItem item) {
        boolean notAllEquipped = false;
        for (Item adjItem : getItemsByItemId(item.getItem().getId())) {
            if (adjItem.isEquipable()) {
                if (!adjItem.isEquipped()) {
                    notAllEquipped = true;
                }
            } else {
                notAllEquipped = true;
                break;
            }
        }
        if (notAllEquipped) {
            final Item adjItem = getItemByItemId(item.getItem().getId());
            item.setObjectId(adjItem.getObjectId());
            item.setEnchant(adjItem.getEnchantLevel());

            if (adjItem.getCount() < item.getCount()) {
                item.setCount(adjItem.getCount());
            }

            return;
        }

        item.setCount(0);
    }

    /**
     * Adds adena to PlayerInventory
     *
     * @param process   : String Identifier of process triggering this action
     * @param count     : int Quantity of adena to be added
     * @param actor     : Player Player requesting the item add
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     */
    public void addAdena(String process, long count, Player actor, Object reference) {
        if (count > 0) {
            addItem(process, CommonItem.ADENA, count, actor, reference);
        }
    }


    /**
     * Removes adena to PlayerInventory
     *
     * @param process   : String Identifier of process triggering this action
     * @param count     : int Quantity of adena to be removed
     * @param actor     : Player Player requesting the item add
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return boolean : true if adena was reduced
     */
    public boolean reduceAdena(String process, long count, Player actor, Object reference) {
        if (count > 0) {
            return destroyItemByItemId(process, CommonItem.ADENA, count, actor, reference) != null;
        }
        return false;
    }

    /**
     * Removes Beauty Tickets to PlayerInventory
     *
     * @param process   : String Identifier of process triggering this action
     * @param count     : int Quantity of Beauty Tickets to be removed
     * @param actor     : Player Player requesting the item add
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return boolean : true if adena was reduced
     */
    public boolean reduceBeautyTickets(String process, long count, Player actor, Object reference) {
        if (count > 0) {
            return destroyItemByItemId(process, BEAUTY_TICKET_ID, count, actor, reference) != null;
        }
        return false;
    }

    /**
     * Adds specified amount of ancient adena to player inventory.
     *
     * @param process   : String Identifier of process triggering this action
     * @param count     : int Quantity of adena to be added
     * @param actor     : Player Player requesting the item add
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     */
    public void addAncientAdena(String process, long count, Player actor, Object reference) {
        if (count > 0) {
            addItem(process,CommonItem.ANCIENT_ADENA, count, actor, reference);
        }
    }

    /**
     * Removes specified amount of ancient adena from player inventory.
     *
     * @param process   : String Identifier of process triggering this action
     * @param count     : int Quantity of adena to be removed
     * @param actor     : Player Player requesting the item add
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return boolean : true if adena was reduced
     */
    public boolean reduceAncientAdena(String process, long count, Player actor, Object reference) {
        if (count > 0) {
            return destroyItemByItemId(process, CommonItem.ANCIENT_ADENA, count, actor, reference) != null;
        }
        return false;
    }

    /**
     * Adds item in inventory and checks _adena and _ancientAdena
     *
     * @param process   : String Identifier of process triggering this action
     * @param item      : Item to be added
     * @param actor     : Player Player requesting the item add
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the new item or the updated item in inventory
     */
    @Override
    public Item addItem(String process, Item item, Player actor, Object reference) {
        item = super.addItem(process, item, actor, reference);

        if (item != null) {
            if ((item.getId() == CommonItem.ADENA) && !item.equals(_adena)) {
                _adena = item;
            } else if ((item.getId() == CommonItem.ANCIENT_ADENA) && !item.equals(_ancientAdena)) {
                _ancientAdena = item;
            } else if ((item.getId() == BEAUTY_TICKET_ID) && !item.equals(_beautyTickets)) {
                _beautyTickets = item;
            } else if( item.getId() == CommonItem.SILVER_COIN && !item.equals(silverCoin)) {
                silverCoin = item;
            } else if(item.getId() == CommonItem.RUSTY_COIN && !item.equals(rustyCoin)) {
                rustyCoin = item;
            }
             else if(item.getId() == CommonItem.L2_COIN && !item.equals(l2Coin)) {
                l2Coin = item;
            }
            if (actor != null) {
                // Send inventory update packet
                if (!Config.FORCE_INVENTORY_UPDATE) {
                    final InventoryUpdate playerIU = new InventoryUpdate();
                    playerIU.addItem(item);
                    actor.sendInventoryUpdate(playerIU);
                } else {
                    actor.sendItemList();
                }

                // Notify to scripts
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerItemAdd(actor, item), actor, item.getTemplate());
            }
        }

        return item;
    }

    /**
     * Adds item in inventory and checks _adena and _ancientAdena
     *
     * @param process   : String Identifier of process triggering this action
     * @param itemId    : int Item Identifier of the item to be added
     * @param count     : int Quantity of items to be added
     * @param actor     : Player Player requesting the item creation
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the new item or the updated item in inventory
     */
    @Override
    public Item addItem(String process, int itemId, long count, Player actor, Object reference) {
        return addItem(process, itemId, count, actor, reference, true);
    }

    /**
     * Adds item in inventory and checks _adena and _ancientAdena
     *
     * @param process   : String Identifier of process triggering this action
     * @param itemId    : int Item Identifier of the item to be added
     * @param count     : int Quantity of items to be added
     * @param actor     : Player Player requesting the item creation
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @param update    : Update inventory (not used by MultiSellChoose packet / it sends update after finish)
     * @return Item corresponding to the new item or the updated item in inventory
     */
    public Item addItem(String process, int itemId, long count, Player actor, Object reference, boolean update) {
        final Item item = super.addItem(process, itemId, count, actor, reference);
        if (item != null) {
            if ((item.getId() == CommonItem.ADENA) && !item.equals(_adena)) {
                _adena = item;
            } else if ((item.getId() == CommonItem.ANCIENT_ADENA) && !item.equals(_ancientAdena)) {
                _ancientAdena = item;
            } else if ((item.getId() == BEAUTY_TICKET_ID) && !item.equals(_beautyTickets)) {
                _beautyTickets = item;
            } else if (item.getId() == CommonItem.SILVER_COIN && !item.equals(silverCoin)) {
                silverCoin = item;
            } else if (item.getId() == CommonItem.RUSTY_COIN && !item.equals(rustyCoin)) {
                rustyCoin = item;
            } else if (item.getId() == CommonItem.L2_COIN && !item.equals(l2Coin)) {
                l2Coin = item;
            }
        }

        if ((item != null) && (actor != null)) {
            // Send inventory update packet
            if (update) {
                final InventoryUpdate playerIU = new InventoryUpdate();
                playerIU.addItem(item);
                actor.sendInventoryUpdate(playerIU);
            }

            // Notify to scripts
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerItemAdd(actor, item), actor, item.getTemplate());
        }
        return item;
    }

    /**
     * Transfers item to another inventory and checks _adena and _ancientAdena
     *
     * @param process   string Identifier of process triggering this action
     * @param objectId  Item Identifier of the item to be transfered
     * @param count     Quantity of items to be transfered
     * @param target    the item container for the item to be transfered.
     * @param actor     the player requesting the item transfer
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the new item or the updated item in inventory
     */
    @Override
    public Item transferItem(String process, int objectId, long count, ItemContainer target, Player actor, Object reference) {
        final Item item = super.transferItem(process, objectId, count, target, actor, reference);

        if ((_adena != null) && ((_adena.getCount() <= 0) || (_adena.getOwnerId() != getOwnerId()))) {
            _adena = null;
        }

        if ((_ancientAdena != null) && ((_ancientAdena.getCount() <= 0) || (_ancientAdena.getOwnerId() != getOwnerId()))) {
            _ancientAdena = null;
        }

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerItemTransfer(actor, item, target), item.getTemplate());
        return item;
    }

    @Override
    public Item detachItem(String process, Item item, long count, ItemLocation newLocation, Player actor, Object reference) {
        item = super.detachItem(process, item, count, newLocation, actor, reference);

        if ((item != null) && (actor != null)) {
            actor.sendItemList();
        }
        return item;
    }

    /**
     * Destroy item from inventory and checks _adena and _ancientAdena
     *
     * @param process   : String Identifier of process triggering this action
     * @param item      : Item to be destroyed
     * @param actor     : Player Player requesting the item destroy
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the destroyed item or the updated item in inventory
     */
    @Override
    public Item destroyItem(String process, Item item, Player actor, Object reference) {
        return destroyItem(process, item, item.getCount(), actor, reference);
    }

    /**
     * Destroy item from inventory and checks _adena and _ancientAdena
     *
     * TODO change the process String to Enum
     *
     * @param process   : String Identifier of process triggering this action
     * @param item      : Item to be destroyed
     * @param actor     : Player Player requesting the item destroy
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the destroyed item or the updated item in inventory
     */
    @Override
    public Item destroyItem(String process, Item item, long count, Player actor, Object reference) {
        item = super.destroyItem(process, item, count, actor, reference);

        if ((_adena != null) && (_adena.getCount() <= 0)) {
            _adena = null;
        }

        if ((_ancientAdena != null) && (_ancientAdena.getCount() <= 0)) {
            _ancientAdena = null;
        }

        // Notify to scripts
        if (nonNull(item)) {
            if(item.isEquipped()) {
                unEquipItemInBodySlot(item.getBodyPart());
            }
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerItemDestroy(actor, item), item.getTemplate());
        }
        return item;
    }

    /**
     * Destroys item from inventory and checks _adena and _ancientAdena
     *
     * @param process   : String Identifier of process triggering this action
     * @param objectId  : int Item Instance identifier of the item to be destroyed
     * @param count     : int Quantity of items to be destroyed
     * @param actor     : Player Player requesting the item destroy
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the destroyed item or the updated item in inventory
     */
    @Override
    public Item destroyItem(String process, int objectId, long count, Player actor, Object reference) {
        final Item item = getItemByObjectId(objectId);
        if (item == null) {
            return null;
        }
        return destroyItem(process, item, count, actor, reference);
    }

    /**
     * Destroy item from inventory by using its <B>itemId</B> and checks _adena and _ancientAdena
     *
     * @param process   : String Identifier of process triggering this action
     * @param itemId    : int Item identifier of the item to be destroyed
     * @param count     : int Quantity of items to be destroyed
     * @param actor     : Player Player requesting the item destroy
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the destroyed item or the updated item in inventory
     */
    @Override
    public Item destroyItemByItemId(String process, int itemId, long count, Player actor, Object reference) {
        final Item item = getItemByItemId(itemId);
        if (item == null) {
            return null;
        }
        return destroyItem(process, item, count, actor, reference);
    }

    /**
     * Drop item from inventory and checks _adena and _ancientAdena
     *
     * @param process   : String Identifier of process triggering this action
     * @param item      : Item to be dropped
     * @param actor     : Player Player requesting the item drop
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the destroyed item or the updated item in inventory
     */
    @Override
    public Item dropItem(String process, Item item, Player actor, Object reference) {
        item = super.dropItem(process, item, actor, reference);

        if ((_adena != null) && ((_adena.getCount() <= 0) || (_adena.getOwnerId() != getOwnerId()))) {
            _adena = null;
        }

        if ((_ancientAdena != null) && ((_ancientAdena.getCount() <= 0) || (_ancientAdena.getOwnerId() != getOwnerId()))) {
            _ancientAdena = null;
        }

        // Notify to scripts
        if (item != null) {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerItemDrop(actor, item, item.getLocation()), item.getTemplate());
        }
        return item;
    }

    /**
     * Drop item from inventory by using its <B>objectID</B> and checks _adena and _ancientAdena
     *
     * @param process   : String Identifier of process triggering this action
     * @param objectId  : int Item Instance identifier of the item to be dropped
     * @param count     : int Quantity of items to be dropped
     * @param actor     : Player Player requesting the item drop
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the destroyed item or the updated item in inventory
     */
    @Override
    public Item dropItem(String process, int objectId, long count, Player actor, Object reference) {
        final Item item = super.dropItem(process, objectId, count, actor, reference);

        if ((_adena != null) && ((_adena.getCount() <= 0) || (_adena.getOwnerId() != getOwnerId()))) {
            _adena = null;
        }

        if ((_ancientAdena != null) && ((_ancientAdena.getCount() <= 0) || (_ancientAdena.getOwnerId() != getOwnerId()))) {
            _ancientAdena = null;
        }

        // Notify to scripts
        if (item != null) {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerItemDrop(actor, item, item.getLocation()), item.getTemplate());
        }
        return item;
    }

    /**
     * <b>Overloaded</b>, when removes item from inventory, remove also owner shortcuts.
     *
     * @param item : Item to be removed from inventory
     */
    @Override
    protected boolean removeItem(Item item) {
        // Removes any reference to the item from Shortcut bar
        owner.removeItemFromShortCut(item.getObjectId());

        // Removes active Enchant Scroll
        if (owner.isProcessingItem(item.getObjectId())) {
            owner.removeRequestsThatProcessesItem(item.getObjectId());
        }

        if (item.getId() == CommonItem.ADENA) {
            _adena = null;
        } else if (item.getId() == CommonItem.ANCIENT_ADENA) {
            _ancientAdena = null;
        } else if (item.getId() == BEAUTY_TICKET_ID) {
            _beautyTickets = null;
        }

        return super.removeItem(item);
    }

    /**
     * Refresh the weight of equipment loaded
     */
    @Override
    public void refreshWeight() {
        super.refreshWeight();
        owner.refreshOverloaded(true);
    }

    /**
     * Get back items in inventory from database
     */
    @Override
    public void restore() {
        super.restore();
        _adena = getItemByItemId(CommonItem.ADENA);
        _ancientAdena = getItemByItemId(CommonItem.ANCIENT_ADENA);
        _beautyTickets = getItemByItemId(BEAUTY_TICKET_ID);
        rustyCoin = getItemByItemId(CommonItem.RUSTY_COIN);
         silverCoin = getItemByItemId(CommonItem.SILVER_COIN);
         l2Coin = getItemByItemId(CommonItem.L2_COIN);
    }

    /**
     * @param itemList         the items that needs to be validated.
     * @param sendMessage      if {@code true} will send a message of inventory full.
     * @param sendSkillMessage if {@code true} will send a message of skill not available.
     * @return {@code true} if the inventory isn't full after taking new items and items weight add to current load doesn't exceed max weight load.
     */
    public boolean checkInventorySlotsAndWeight(List<ItemTemplate> itemList, boolean sendMessage, boolean sendSkillMessage) {
        int lootWeight = 0;
        int requiredSlots = 0;
        if (itemList != null) {
            for (ItemTemplate item : itemList) {
                // If the item is not stackable or is stackable and not present in inventory, will need a slot.
                if (!item.isStackable() || (getInventoryItemCount(item.getId(), -1) <= 0)) {
                    requiredSlots++;
                }
                lootWeight += item.getWeight();
            }
        }

        final boolean inventoryStatusOK = validateCapacity(requiredSlots) && validateWeight(lootWeight);
        if (!inventoryStatusOK && sendMessage) {
            owner.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
            if (sendSkillMessage) {
                owner.sendPacket(SystemMessageId.WEIGHT_AND_VOLUME_LIMIT_HAVE_BEEN_EXCEEDED_THAT_SKILL_IS_CURRENTLY_UNAVAILABLE);
            }
        }
        return inventoryStatusOK;
    }

    /**
     * If the item is not stackable or is stackable and not present in inventory, will need a slot.
     *
     * @param item the item to validate.
     * @return {@code true} if there is enough room to add the item inventory.
     */
    public boolean validateCapacity(Item item) {
        int slots = 0;
        if (!item.isStackable() || ((getInventoryItemCount(item.getId(), -1) <= 0) && !item.getTemplate().hasExImmediateEffect())) {
            slots++;
        }
        return validateCapacity(slots, item.isQuestItem());
    }

    /**
     * If the item is not stackable or is stackable and not present in inventory, will need a slot.
     *
     * @param itemId the item Id for the item to validate.
     * @return {@code true} if there is enough room to add the item inventory.
     */
    public boolean validateCapacityByItemId(int itemId) {
        int slots = 0;
        final Item invItem = getItemByItemId(itemId);
        if ((invItem == null) || !invItem.isStackable()) {
            slots++;
        }
        return validateCapacity(slots, ItemEngine.getInstance().getTemplate(itemId).isQuestItem());
    }

    @Override
    public boolean validateCapacity(long slots) {
        return validateCapacity(slots, false);
    }

    public boolean validateCapacity(long slots, boolean questItem) {
        return ((slots == 0) && !Config.AUTO_LOOT_SLOT_LIMIT) || questItem ? (getSize(Item::isQuestItem) + slots) <= owner.getQuestInventoryLimit() : (getSize(item -> !item.isQuestItem()) + slots) <= owner.getInventoryLimit();
    }

    @Override
    public boolean validateWeight(long weight) {
        // Disable weight check for GMs.
        if (owner.isGM() && owner.getDietMode() && owner.getAccessLevel().allowTransaction()) {
            return true;
        }
        return ((_totalWeight + weight) <= owner.getMaxLoad());
    }

    /**
     * Set inventory block for specified IDs<br>
     * array reference is used for {@link PlayerInventory#blockItems}
     *
     * @param items array of Ids to block/allow
     * @param mode  blocking mode {@link PlayerInventory#blockMode}
     */
    public void setInventoryBlock(IntCollection items, InventoryBlockType mode) {
        blockMode = mode;
        blockItems = items;
    }

    /**
     * Unblock blocked itemIds
     */
    public void unblock() {
        blockMode = InventoryBlockType.NONE;
        blockItems = null;
    }

    /**
     * Check if player inventory is in block mode.
     *
     * @return true if some itemIds blocked
     */
    public boolean hasInventoryBlock() {
        return (blockMode != InventoryBlockType.NONE && nonNull(blockItems) && !blockItems.isEmpty());
    }

    public InventoryBlockType getBlockMode() {
        return blockMode;
    }

    public IntCollection getBlockItems() {
        return blockItems;
    }

    public boolean isBlocked(Item item) {
        if (nonNull(blockItems)) {
            return switch (blockMode) {
                case NONE -> false;
                case WHITELIST -> blockItems.stream().noneMatch(id -> id == item.getId());
                case BLACKLIST -> blockItems.stream().anyMatch(id -> id == item.getId());
            };
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + owner + "]";
    }

    /**
     * Apply skills of inventory items
     */
    public void applyItemSkills() {
        for (Item item : items.values()) {
            item.giveSkillsToOwner();
            item.applyEnchantStats();
            if (item.isEquipped()) {
                item.applySpecialAbilities();
            }
        }
    }

    public void reduceAmmunitionCount() {
        if(isNull(currentAmmunition) || currentAmmunition.isInfinite()) {
            return;
        }
        updateItemCountNoDbUpdate(null, currentAmmunition, -1, owner, null);
    }

    /**
     * Reduces item count in the stack, destroys item when count reaches 0.
     *
     * @param countDelta Adds items to stack if positive, reduces if negative. If stack count reaches 0 item is destroyed.
     * @return Amount of items left.
     */
    public boolean updateItemCountNoDbUpdate(String process, Item item, long countDelta, Player creator, Object reference) {
        final long left = item.getCount() + countDelta;
        if (left > 0) {
            if (Util.isNotEmpty(process)) {
                item.changeCount(process, countDelta, creator, reference);
            } else {
                item.changeCountWithoutTrace(countDelta, creator, reference);
            }
            item.setLastChange(Item.MODIFIED);
            refreshWeight();
        } else {
            destroyItem(process, item, owner, null);
        }
        owner.sendInventoryUpdate(new InventoryUpdate(item));
        return true;
    }

    /**
     * Reduces item count in the stack, destroys item when count reaches 0.
     *
     * @param countDelta Adds items to stack if positive, reduces if negative. If stack count reaches 0 item is destroyed.
     * @return Amount of items left.
     */
    public boolean updateItemCount(String process, Item item, long countDelta, Player creator, Object reference) {
        if (item != null) {
            try {
                return updateItemCountNoDbUpdate(process, item, countDelta, creator, reference);
            } finally {
                if (item.getCount() > 0) {
                    item.updateDatabase();
                }
            }
        }
        return false;
    }

    public long getRustyCoin() {
        return nonNull(rustyCoin) ? rustyCoin.getCount() : 0;
    }

    public long getSilverCoin() {
        return nonNull(silverCoin) ? silverCoin.getCount() : 0;
    }

    public long getLCoin() {
        return nonNull(l2Coin) ? l2Coin.getCount() : 0;
    }

    public void addLCoin(long count) {
        l2Coin.setCount(getLCoin() + count);
    }

    public boolean findAmmunitionForCurrentWeapon() {
        final var currentWeapon = getPaperdollItem(InventorySlot.RIGHT_HAND);

        if(isNull(currentWeapon)) {
            return false;
        }

        return matchesAmmunition(currentAmmunition, currentWeapon) || nonNull(currentAmmunition = findAmmunition(currentWeapon));
    }

    private Item findAmmunition(Item currentWeapon) {
        return items.values().stream().filter(i -> matchesAmmunition(i, currentWeapon)).findFirst().orElse(null);
    }

    private boolean matchesAmmunition(Item ammunition, Item weapon) {
            if(isNull(ammunition) || ammunition.getCrystalType() != weapon.getCrystalType() || ammunition.getCount() < 1
                    || !items.containsKey(ammunition.getObjectId())) {
            return false;
        }

        final var itemType = weapon.getItemType();

        return (ammunition.getItemType() == ARROW && itemType == WeaponType.BOW) ||
               (ammunition.getItemType() == BOLT && itemType == WeaponType.CROSSBOW || itemType == WeaponType.TWO_HAND_CROSSBOW);
    }
}
