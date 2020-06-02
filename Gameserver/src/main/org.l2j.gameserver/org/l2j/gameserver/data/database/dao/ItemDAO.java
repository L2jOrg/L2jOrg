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

    @Query("DELETE FROM items WHERE items.owner_id = -1 AND loc LIKE 'MAIL' AND loc_data NOT IN (SELECT messageId FROM messages WHERE senderId = -1)")
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
}
