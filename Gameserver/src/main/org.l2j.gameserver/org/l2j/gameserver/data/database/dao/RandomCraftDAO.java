package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.RandomCraftDAOData;

public interface RandomCraftDAO extends DAO<RandomCraftDAOData> {
    @Query("SELECT * FROM character_random_craft WHERE charId = :charId:")
    RandomCraftDAOData findByCharId(int charId);

    @Query("UPDATE character_random_craft SET random_craft_full_points = :random_craft_full_points:,random_craft_points = :random_craft_points:,sayha_roll = :sayha_roll:,item_1_id = :item_1_id:,item_1_count = :item_1_count:,item_1_locked = :item_1_locked:,item_1_lock_left = :item_1_lock_left:,item_2_id = :item_2_id:,item_2_count = :item_2_count:,item_2_locked = :item_2_locked:,item_2_lock_left = :item_2_lock_left:,item_3_id = :item_3_id:,item_3_count = :item_3_count:,item_3_locked = :item_3_locked:,item_3_lock_left = :item_3_lock_left:,item_4_id = :item_4_id:,item_4_count = :item_4_count:,item_4_locked = :item_4_locked:,item_4_lock_left = :item_4_lock_left:,item_5_id = :item_5_id:,item_5_count = :item_5_count:,item_5_locked = :item_5_locked:,item_5_lock_left = :item_5_lock_left: WHERE charId = :charId:")
    void updateRandomCraft(int charId, int random_craft_full_points,int random_craft_points,boolean sayha_roll,int item_1_id,long item_1_count, boolean item_1_locked, int item_1_lock_left, int item_2_id,long item_2_count,boolean item_2_locked, int item_2_lock_left,int item_3_id,long item_3_count, boolean item_3_locked, int item_3_lock_left, int item_4_id, long item_4_count, boolean item_4_locked, int item_4_lock_left, int item_5_id, long item_5_count, boolean item_5_locked,int item_5_lock_left);

    @Query("INSERT INTO character_random_craft VALUES(:charId:,:random_craft_full_points:,:random_craft_points:,:sayha_roll:,:item_1_id:,:item_1_count:,:item_1_locked:,:item_1_lock_left:,:item_2_id:,:item_2_count:,:item_2_locked:,:item_2_lock_left:,:item_3_id:,:item_3_count:,:item_3_locked:,:item_3_lock_left:,:item_4_id:,:item_4_count:,:item_4_locked:,:item_4_lock_left:,:item_5_id:,:item_5_count:,:item_5_locked:,:item_5_lock_left:)")
    void storeNew(int charId, int random_craft_full_points,int random_craft_points,boolean sayha_roll,int item_1_id,long item_1_count, boolean item_1_locked, int item_1_lock_left, int item_2_id,long item_2_count,boolean item_2_locked, int item_2_lock_left,int item_3_id,long item_3_count, boolean item_3_locked, int item_3_lock_left, int item_4_id, long item_4_count, boolean item_4_locked, int item_4_lock_left, int item_5_id, long item_5_count, boolean item_5_locked,int item_5_lock_left);

}
