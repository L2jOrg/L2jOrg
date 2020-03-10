package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

/**
 * @author JoeAlisson
 */
public interface ClanDAO extends DAO<Object> {

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

    @Query("DELETE FROM clan_wars WHERE clan1=:clan1: AND clan2=:clan2:")
    void deleteClanWar(int clan1, int clan2);
}
