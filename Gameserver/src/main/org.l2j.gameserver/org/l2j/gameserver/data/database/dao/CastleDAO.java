package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.CastleData;
import org.l2j.gameserver.data.database.data.CastleFunctionData;

import java.sql.ResultSet;
import java.util.List;
import java.util.function.Consumer;

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

    @Query("SELECT type, castle_id, lvl, lease, endTime FROM castle_functions WHERE castle_id = ?")
    List<CastleFunctionData> findFunctionsByCastle(int id);

    @Query("DELETE FROM castle_functions WHERE castle_id=:id: AND type=:type:")
    void deleteFunction(int id, int type);

    @Query("SELECT doorId, ratio FROM castle_doorupgrade WHERE castleId= :castleId:")
    void findDoorUpgrade(int castleId, Consumer<ResultSet> action);
}
