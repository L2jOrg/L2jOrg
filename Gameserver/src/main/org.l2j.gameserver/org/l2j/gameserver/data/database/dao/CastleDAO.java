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
import org.l2j.gameserver.data.database.data.CastleData;
import org.l2j.gameserver.data.database.data.CastleFunctionData;
import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.enums.CastleSide;

import java.sql.ResultSet;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author JoeAlisson
 */
public interface CastleDAO extends DAO<CastleData> {

    @Query("UPDATE castle SET side='NEUTRAL' WHERE castle.id NOT IN (SELECT hasCastle FROM clan_data);")
    void updateToNeutralWithoutOwner();

    @Query("SELECT * FROM castle WHERE id = :id:")
    CastleData findById(int id);

    @Query("UPDATE castle SET treasury = :treasury: WHERE id = :id:")
    void updateTreasury(int id, long treasury);

    @Query("UPDATE castle SET show_npc_crest = :showNpcCrest: WHERE id = :id:")
    void updateShowNpcCrest(int id, boolean showNpcCrest);

    @Query("UPDATE castle SET ticket_buy_count = :ticketBuyCount: WHERE id = :id:")
    void updateTicketBuyCount(int id, int ticketBuyCount);

    @Query("SELECT type, castle_id, level, lease, endTime FROM castle_functions WHERE castle_id = :id:")
    List<CastleFunctionData> findFunctionsByCastle(int id);

    @Query("DELETE FROM castle_functions WHERE castle_id=:id: AND type=:type:")
    void deleteFunction(int id, int type);

    @Query("SELECT doorId, ratio FROM castle_doorupgrade WHERE castleId= :castleId:")
    void withDoorUpgradeDo(int castleId, Consumer<ResultSet> action);

    @Query("REPLACE INTO castle_doorupgrade (doorId, ratio, castleId) values (:doorId:, :ratio:, :castleId:)")
    void saveDoorUpgrade(int castleId, int doorId, int ratio);

    @Query("DELETE FROM castle_doorupgrade WHERE castleId=:castleId:")
    void deleteDoorUpgradeByCastle(int castleId);

    @Query("REPLACE INTO castle_trap_upgrade (castle_id, tower_index, level) values (:id:,:towerIndex:,:level:)")
    void saveTrapUpgrade(int id, int towerIndex, int level);

    @Query("DELETE FROM castle_trap_upgrade WHERE castle_id= :id:")
    void deleteTrapUpgradeByCastle(int id);

    @Query("UPDATE castle SET side = :side: WHERE id = :id:")
    void updateSide(int id, CastleSide side);

    void save(CastleFunctionData functionData);

    @Query("DELETE FROM siege_clans WHERE castle_id=:castleId:")
    void deleteSiegeByCastle(int castleId);

    @Query("DELETE FROM siege_clans WHERE clan_id=:clanId:")
    void deleteSiegeByClan(int clanId);

    @Query("DELETE FROM siege_clans WHERE castle_id=:castleId: and type = 2")
    void deleteWaintingClansByCastle(int castleId);

    @Query("SELECT clan_id,type FROM siege_clans where castle_id=:castleId:")
    List<SiegeClanData> findSiegeClansByCastle(int castleId);

    void save(SiegeClanData siegeClanData);

    @Query("DELETE FROM siege_clans WHERE castle_id=:castleId: and clan_id=:clanId:")
    void deleteSiegeClanByCastle(int clanId, int castleId);

    @Query("SELECT * FROM castle")
    List<CastleData> findAll();
}
