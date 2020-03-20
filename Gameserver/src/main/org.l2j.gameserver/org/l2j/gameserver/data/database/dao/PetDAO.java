package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.PetData;

import java.util.List;

/**
 * @author JoeAlisson
 */
public interface PetDAO extends DAO<PetData> {

    @Query("SELECT ownerId, item_obj_id FROM pets WHERE restore = 'true'")
    List<PetData> findAllPetOwnersByRestore();

    @Query("SELECT EXISTS (SELECT 1 FROM pets p, items i WHERE p.item_obj_id = i.object_id AND name= :name: AND i.item_id = :petItem:)")
    boolean existsPetName(String name, int petItem);
}
