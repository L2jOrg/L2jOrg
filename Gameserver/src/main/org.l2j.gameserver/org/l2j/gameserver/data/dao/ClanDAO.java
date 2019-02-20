package org.l2j.gameserver.data.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

public interface ClanDAO extends DAO {

    @Query("DELETE FROM clan_data WHERE clan_data.clan_id NOT IN (SELECT clanid FROM characters)")
    int deleteWithoutPlayers();

    @Query("DELETE FROM clan_subpledges WHERE clan_subpledges.type = 0 AND clan_subpledges.leader_id NOT IN (SELECT obj_Id FROM characters)")
    int deleteMainSubpledgeWithoutLeader();

    @Query("DELETE FROM clan_data WHERE clan_data.clan_id NOT IN (SELECT clan_id FROM clan_subpledges WHERE clan_subpledges.type = 0)")
    int deleteClanWithoutMainSubpledge();

    @Query("DELETE FROM ally_data WHERE ally_data.ally_id NOT IN (SELECT ally_id FROM clan_data)")
    int deleteAllyWithoutClan();

    @Query("DELETE FROM clan_subpledges WHERE clan_subpledges.clan_id NOT IN (SELECT clan_id FROM clan_data)")
    int deleteSubpledgeWithoutClan();

    @Query("UPDATE clan_subpledges SET leader_id=0 WHERE leader_id > 0 AND clan_subpledges.leader_id NOT IN (SELECT obj_Id FROM characters)")
    void updateSubpledgeWithoutLeader();

    @Query("UPDATE characters SET clanid = '0', title = '', pledge_type = '0', pledge_rank = '0', lvl_joined_academy = '0', apprentice = '0' WHERE characters.clanid > 0 AND characters.clanid NOT IN (SELECT clan_id FROM clan_data)")
    void updateMemberInfoOfMissingClan();
}
