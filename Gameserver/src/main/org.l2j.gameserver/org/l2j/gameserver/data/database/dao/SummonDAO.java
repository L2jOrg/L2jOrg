package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.SummonData;

import java.util.List;

/**
 * @author JoeAlisson
 */
public interface SummonDAO extends DAO<SummonData> {

    @Query("SELECT ownerId, summonId FROM character_summons")
    List<SummonData> findAllSummonOwners();

    @Query("DELETE FROM character_summons WHERE ownerId = :objectId: and summonId = :id:")
    void deleteByIdAndOwner(int id, int objectId);

    @Query("SELECT summonSkillId, summonId, curHp, curMp, time FROM character_summons WHERE ownerId = :ownerId:")
    List<SummonData> findSummonsByOwner(int ownerId);

    @Query("REPLACE INTO character_summons (ownerId,summonId,summonSkillId,curHp,curMp,time) VALUES (:ownerId:,:id:,:skill:,:currentHp:,:currentMp:,:lifeTime:)")
    void save(int ownerId, int id, int skill, int currentHp, int currentMp, int lifeTime);
}
