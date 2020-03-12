package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.ClanData;

import java.util.List;

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
}
