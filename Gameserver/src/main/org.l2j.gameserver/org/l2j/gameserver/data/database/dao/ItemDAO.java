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
package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.ItemOnGroundData;

import java.util.Collection;
import java.util.List;

/**
 * @author JoeAlisson
 */
public interface ItemDAO extends DAO<Object> {

    @Query( "DELETE FROM items WHERE items.owner_id NOT IN (SELECT charId FROM characters) AND items.owner_id NOT IN (SELECT clan_id FROM clan_data) AND items.owner_id != -1")
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

    void save(Collection<ItemOnGroundData> datas);

    @Query("DELETE FROM item_variations WHERE itemId IN (SELECT object_id FROM items WHERE items.owner_id=:playerId:)")
    void deleteVariationsByOwner(int playerId);

    @Query("DELETE FROM item_special_abilities WHERE objectId IN (SELECT object_id FROM items WHERE items.owner_id=:playerId:)")
    void deleteSpecialAbilitiesByOwner(int playerId);

    @Query("DELETE FROM items WHERE owner_id=:playerId:")
    void deleteByOwner(int playerId);
}
