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
package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.*;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.commission.CommissionItem;

import java.util.Collection;
import java.util.List;

/**
 * @author JoeAlisson
 */
public interface ItemDAO extends DAO<Object> {

    @Query("DELETE FROM items WHERE items.owner_id NOT IN (SELECT charId FROM characters) AND items.owner_id NOT IN (SELECT clan_id FROM clan_data) AND items.owner_id != -1")
    int deleteWithoutOwner();

    @Query("DELETE FROM items WHERE items.owner_id = -1 AND loc LIKE 'MAIL' AND loc_data NOT IN (SELECT id FROM mail WHERE sender = -1)")
    int deleteFromEmailWithoutMessage();

    @Query("UPDATE items SET loc='INVENTORY' WHERE owner_id=:owner:")
    void updateToInventory(int owner);

    @Query("DELETE FROM items WHERE item_id= :itemId:")
    void deleteAllItemsById(int itemId);

    @Query("DELETE FROM items WHERE item_id = :itemId: AND  owner_id = :owner:")
    void deleteByIdAndOwner(int itemId, int owner);

    @Query("UPDATE itemsonground SET drop_time = :dropTime: WHERE drop_time = -1")
    void updateDropTimeByNonDestroyable(long dropTime);

    @Query("UPDATE itemsonground SET drop_time = :dropTime: WHERE drop_time = -1 AND equipable = 0")
    void updateNonEquipDropTimeByNonDestroyable(long dropTime);

    @Query("TRUNCATE  itemsonground")
    void deleteItemsOnGround();

    @Query("SELECT * FROM itemsonground")
    List<ItemOnGroundData> findAllItemsOnGround();

    @Query("SELECT * FROM item_variations WHERE itemId = :itemId:")
    ItemVariationData findItemVariationByItem(int itemId);

    @Query("SELECT * FROM items WHERE owner_id = :ownerId: AND loc=:loc:")
    List<ItemData> findItemsByOwnerAndLoc(int ownerId, ItemLocation loc);

    @Query("SELECT * FROM items WHERE owner_id=:ownerId: AND loc='MAIL' AND loc_data=:mailId:")
    List<ItemData> findItemsAttachment(int ownerId, int mailId);

    @Query("SELECT * FROM items WHERE owner_id=:ownerId: AND (loc=:baseLoc: OR loc=:equipLoc:) ORDER BY loc_data")
    List<ItemData> findInventoryItems(int ownerId,  ItemLocation baseLoc, ItemLocation equipLoc);

    void save(Collection<ItemOnGroundData> datas);

    @Query("DELETE FROM item_variations WHERE itemId IN (SELECT object_id FROM items WHERE items.owner_id=:playerId:)")
    void deleteVariationsByOwner(int playerId);

    @Query("DELETE FROM items WHERE owner_id=:playerId:")
    void deleteByOwner(int playerId);

    @Query("""
            INSERT INTO items (owner_id, object_id, item_id, count, loc, loc_data, time)
            VALUES (:owner:, :objectId:, :itemId:, :count:, :loc:, :locData:, -1 )""")
    void saveItem(int owner, int objectId, int itemId, long count, ItemLocation loc, int locData);

    @Query("DELETE FROM item_variations WHERE itemId = :objectId:")
    void deleteVariations(int objectId);

    @Query("DELETE FROM items WHERE object_id = :objectId:")
    void deleteItem(int objectId);

    @Query("DELETE FROM `commission_items` WHERE `commission_id` = :commissionId:")
    boolean deleteCommission(long commissionId);

    @Query("DELETE FROM item_auction WHERE auction=:auction:")
    void deleteItemAuction(int auction);

    @Query("DELETE FROM item_auction_bid WHERE auction=:auction:")
    void deleteItemAuctionBid(int auction);

    @Query("SELECT * FROM item_auction_bid WHERE auction = :auction:")
    List<ItemAuctionBid> findAuctionBids(int auctionId);

    @Query("DELETE FROM item_auction_bid WHERE auction = :auction: AND player_id = :playerId:")
    void deleteItemAuctionBid(int auction, int playerId);

    @Query("SELECT * FROM item_auction WHERE instance = :instance:")
    List<ItemAuctionData> findItemAuctionsByInstance(int instance);

    @Query("SELECT auction FROM item_auction ORDER BY auction DESC LIMIT 1")
    int findLastAuctionId();

    @Query("""
           SELECT * FROM items i
           JOIN commission_items ci ON ci.item_object_id = i.object_id
           WHERE i.loc = 'COMMISSION'
           """)
    List<CommissionItem> findCommissionItems();

    @Query("UPDATE items SET ensoul = :ensoul: WHERE object_id = :objectId:")
    void updateEnsoul(int objectId, int ensoul);

    @Query("UPDATE items SET special_ensoul = :ensoul: WHERE object_id = :objectId:")
    void updateSpecialEnsoul(int objectId, int ensoul);


    @Query("SELECT EXISTS (SELECT 1 FROM `items` WHERE `owner_id`=:playerId: AND object_id > 0 AND (`loc`='PET' OR `loc`='PET_EQUIP'))")
    boolean hasPetItems(int playerId);
}
