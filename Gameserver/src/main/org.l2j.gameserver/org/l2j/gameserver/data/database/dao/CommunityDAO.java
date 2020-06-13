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
import org.l2j.gameserver.data.database.data.CommunityFavorite;
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

    @Query("SELECT * FROM `bbs_favorites` WHERE `playerId`=:playerId: ORDER BY `favAddDate` DESC")
    List<CommunityFavorite> findFavorites(int playerId);

    @Query("DELETE FROM `bbs_favorites` WHERE `playerId`=:playerId: AND `favId`=:id:")
    void deleteFavorite(int playerId, int id);
}
