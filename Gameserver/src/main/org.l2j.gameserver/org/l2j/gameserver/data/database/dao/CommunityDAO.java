package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.CommunityMemo;

import java.util.List;

/**
 * @author JoeAlisson
 */
public interface CommunityDAO extends DAO<Object> {

    @Query("SELECT COUNT(1) FROM `bbs_favorites` WHERE playerId=:playerId:")
    int getFavoritesCount(int playerId);

    @Query("SELECT id, title, date FROM community_memos WHERE owner_id = :playerId:")
    List<CommunityMemo> findMemosBasicInfo(int playerId);

    @Query("INSERT INTO community_memos (owner_id, title, text) VALUE (:playerId:, :title:, :text:)")
    void saveMemo(int playerId, String title, String text);

    @Query("SELECT * FROM community_memos WHERE id=:id: AND owner_id=:playerId:")
    CommunityMemo findMemo(int id, int playerId);

    @Query("UPDATE community_memos SET title=:title:, text=:text: WHERE id=:id: AND owner_id=:playerId:")
    void updateMemo(int playerId, int id, String title, String text);

    @Query("DELETE FROM community_memos WHERE owner_id=:playerId: AND id=:id:")
    void deleteMemo(int playerId, int id);
}
