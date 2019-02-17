package org.l2j.gameserver.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

// TODO rename to ItemsDAO
public interface IItemsDAO extends DAO {

    @Query("WITH ids AS (SELECT item_id FROM items_to_delete) " +
            "DELETE i, id, ide FROM items i JOIN items_delayed id ON i.item_id = id.item_id JOIN items_to_delete ide ON i.item_id = ide.item_id " +
            "WHERE  i.item_id IN (SELECT item_id FROM ids)")
    int deleteGlobalItemsToRemove();

    @Query("DELETE FROM items WHERE items.loc != 'MAIL' AND items.owner_id NOT IN (SELECT obj_Id FROM characters) AND items.owner_id NOT IN (SELECT clan_id FROM clan_data);")
    int deleteItemsWithoutOwner();
}
