package org.l2j.gameserver.data.database.dao;

import io.github.joealisson.primitive.ConcurrentIntMap;
import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.PledgeApplicantData;
import org.l2j.gameserver.data.database.data.PledgeRecruitData;
import org.l2j.gameserver.data.database.data.PledgeWaitingData;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author JoeAlisson
 */
public interface PledgeRecruitDAO extends DAO<PledgeRecruitData> {

    @Query("SELECT * FROM pledge_recruit")
    ConcurrentIntMap<PledgeRecruitData> findAll(Consumer<PledgeRecruitData> onLoadAction);

    @Query("SELECT a.char_id as char_id, karma, base_class, level, char_name FROM pledge_waiting_list as a " +
            "LEFT JOIN characters as b ON a.char_id = b.charId")
    ConcurrentIntMap<PledgeWaitingData> findAllWaiting();

    @Query("SELECT a.charId as charId, a.clanId as clanId, karma, message, base_class, level, char_name FROM pledge_applicant as a " +
            "LEFT JOIN characters as b ON a.charId = b.charId")
    List<PledgeApplicantData> findAllApplicant();

    @Query("DELETE FROM pledge_applicant WHERE charId=:playerId: AND clanId=:clanId:")
    void deleteApplicant(int playerId, int clanId);

    void save(PledgeApplicantData info);

    void save(PledgeWaitingData info);

    @Query("DELETE FROM pledge_waiting_list WHERE char_id = :playerId:")
    void deleteWaiting(int playerId);

    @Query("DELETE FROM pledge_recruit WHERE clan_id = :clanId:")
    void deleteRecruit(int clanId);
}
