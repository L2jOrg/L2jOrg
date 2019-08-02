package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

public interface ClanDAO extends DAO<Object> {

    @Query("DELETE FROM clan_data WHERE clan_data.clan_id NOT IN (SELECT clanid FROM characters)")
    int deleteWithoutMembers();

    @Query("UPDATE clan_data SET auction_bid_at = 0 WHERE auction_bid_at NOT IN (SELECT auctionId FROM auction_bid)")
    void resetAuctionBidWithoutAction();

    @Query("UPDATE clan_data SET new_leader_id = 0 WHERE new_leader_id <> 0 AND new_leader_id NOT IN (SELECT charId FROM characters)")
    void resetNewLeaderWithoutCharacter();

    @Query("UPDATE clan_subpledges SET leader_id=0 WHERE clan_subpledges.leader_id NOT IN (SELECT charId FROM characters) AND leader_id > 0;")
    void resetSubpledgeLeaderWithoutCharacter();
}
