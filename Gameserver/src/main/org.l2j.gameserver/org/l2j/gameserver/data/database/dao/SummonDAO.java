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
import org.l2j.gameserver.data.database.data.SummonData;
import org.l2j.gameserver.data.database.data.SummonSkillData;

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

    @Query("SELECT skill_id, skill_level, remaining_time, buff_index FROM character_summon_skills_save WHERE ownerId=:ownerId: AND summonSkillId=:summonSkillId: ORDER BY buff_index")
    List<SummonSkillData> findSummonSkills(int ownerId, int summonSkillId);

    @Query("DELETE FROM character_summon_skills_save WHERE ownerId=:ownerId: AND summonSkillId=:summonSkillId:")
    void deleteSkillsSave(int ownerId, int summonSkillId);

    void save(List<SummonSkillData> data);
}
