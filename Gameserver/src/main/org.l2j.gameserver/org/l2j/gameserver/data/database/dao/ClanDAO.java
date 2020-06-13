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

import io.github.joealisson.primitive.ConcurrentIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.*;

import java.sql.ResultSet;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author JoeAlisson
 */
public interface ClanDAO extends DAO<ClanData> {

    @Query("DELETE FROM clan_data WHERE clan_data.clan_id NOT IN (SELECT clanid FROM characters)")
    int deleteWithoutMembers();

    @Query("UPDATE clan_data SET auction_bid_at = 0 WHERE auction_bid_at NOT IN (SELECT auctionId FROM auction_bid)")
    void resetAuctionBidWithoutAction();

    @Query("UPDATE clan_data SET new_leader_id = 0 WHERE new_leader_id <> 0 AND new_leader_id NOT IN (SELECT charId FROM characters)")
    void resetNewLeaderWithoutCharacter();

    @Query("UPDATE clan_subpledges SET leader_id=0 WHERE clan_subpledges.leader_id NOT IN (SELECT charId FROM characters) AND leader_id > 0;")
    void resetSubpledgeLeaderWithoutCharacter();

    @Query("SELECT clan_id FROM clan_data WHERE hasCastle = :castleId:")
    int findOwnerClanIdByCastle(int castleId);

    @Query("UPDATE clan_data SET hasCastle = 0 WHERE hasCastle = :castleId:")
    void removeOwnerClanByCastle(int castleId);

    @Query("UPDATE clan_data SET hasCastle = :castleId: WHERE clan_id = :id:")
    void updateOwnedCastle(int id, int castleId);

    @Query("DELETE FROM clan_wars WHERE (clan1=:clan1: AND clan2=:clan2:) OR (clan1=:clan2: AND clan2=:clan1:)")
    void deleteClanWar(int clan1, int clan2);

    @Query("""
            SELECT c.clan_name, c.ally_name
            FROM clan_wars w
            INNER JOIN clan_data c ON c.clan_id = w.clan2
            WHERE w.clan1 = :clanId: AND
                w.clan2 NOT IN ( SELECT clan1 FROM clan_wars WHERE clan2 = :clanId:)
            """)
    List<ClanData> findAttackList(int clanId);

    @Query("""
            SELECT c.clan_name, c.ally_name
            FROM clan_wars w
            INNER JOIN clan_data c ON c.clan_id = w.clan1
            WHERE w.clan2 = :clanId: AND
                w.clan1 NOT IN ( SELECT clan2 FROM clan_wars WHERE clan1 = :clanId:)
            """)
    List<ClanData> findUnderAttackList(int clanId);

    @Query("""
            SELECT c.clan_name, c.ally_name
            FROM clan_wars w
            INNER JOIN clan_data c ON c.clan_id = w.clan2
            WHERE w.clan1 = :clanId: AND
                w.clan2 IN ( SELECT clan1 FROM clan_wars WHERE clan2 = :clanId:)
            """)
    List<ClanData> findWarList(int clanId);

    @Query("SELECT * FROM clan_data")
    List<ClanData> findAll();

    @Query("SELECT enabled, notice FROM clan_notices WHERE clan_id=:id:")
    void withNoticesDo(int id, Consumer<ResultSet> action);

    @Query("REPLACE INTO clan_notices (clan_id, notice, enabled) values (:id:,:notice:,:enabled:)")
    void saveNotice(int id, String notice, boolean enabled);

    @Query("SELECT skill_id, skill_level, sub_pledge_id FROM clan_skills WHERE clan_id=:id:")
    List<ClanSkillData> findSkillsByClan(int id);

    @Query("UPDATE clan_skills SET skill_level= :level: WHERE skill_id=:skillId: AND clan_id=:id:")
    void updateClanSkill(int id, int skillId, int level);

    @Query("REPLACE INTO clan_skills (clan_id,skill_id,skill_level ,sub_pledge_id) VALUES (:id:, :skillId:, :level:, :subType:)")
    void saveClanSkill(int id, int skillId, int level, int subType);

    @Query("SELECT sub_pledge_id, name, leader_id FROM clan_subpledges WHERE clan_id=:id:")
    ConcurrentIntMap<SubPledgeData> findClanSubPledges(int id);

    void save(SubPledgeData subPledgeData);

    @Query("SELECT privs,`rank` FROM clan_privs WHERE clan_id=:id:")
    void withClanPrivs(int id, Consumer<ResultSet> action);

    @Query("REPLACE INTO clan_privs (clan_id,`rank`, privs) VALUES (:id:,:rank:,:privs:)")
    void saveClanPrivs(int id, int rank, int privs);

    @Query("UPDATE clan_data SET clan_level = :level: WHERE clan_id = :id:")
    void updateClanLevel(int id, int level);

    @Query("UPDATE clan_data SET crest_id = :crestId: WHERE clan_id = :id:")
    void updateClanCrest(int id, int crestId);

    @Query("UPDATE clan_data SET ally_crest_id = :crestId: WHERE ally_id = :allyId:")
    void updateAllyCrestByAlly(int allyId, int crestId);

    @Query("UPDATE clan_data SET ally_crest_id = :crestId: WHERE clan_id = :id:")
    void updateAllyCrest(int id, int crestId);

    @Query("UPDATE clan_data SET crest_large_id = :crestId: WHERE clan_id = :id:")
    void updateClanCrestLarge(int id, int crestId);

    @Query("""
            DELETE FROM crests WHERE id NOT IN (
                SELECT crest_id AS id FROM clan_data
                UNION ALL
                SELECT ally_crest_id AS id FROM clan_data
                UNION ALL
                SELECT crest_large_id AS id FROM clan_data)
            """)
    void removeUnusedCrests();

    @Query("SELECT * FROM crests")
    IntMap<CrestData> findAllCrests();

    void save(CrestData crest);

    @Query("DELETE FROM crests where id = :crestId:")
    void deleteCrest(int crestId);

    @Query("DELETE FROM clan_data WHERE clan_id = :clanId:")
    void deleteClan(int clanId);

    @Query("SELECT * FROM clan_wars")
    List<ClanWarData> findAllWars();

    void save(ClanWarData war);

    @Query("DELETE FROM clan_skills WHERE clan_id=:clanId: AND skill_id=:skillId:")
    void removeSkill(int clanId, int skillId);
}
