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

    @Query("DELETE FROM pets WHERE item_obj_id=:itemId:")
    void deleteByItem(int itemId);

    @Query("DELETE FROM pets WHERE item_obj_id IN (SELECT object_id FROM items WHERE items.owner_id=:playerId:)")
    void deleteByOwner(int playerId);
}
