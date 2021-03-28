/*
 * Copyright © 2019-2021 L2JOrg
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
import org.l2j.gameserver.data.database.data.QuestData;

import java.util.List;

/**
 * @author JoeAlisson
 */
public interface QuestDAO extends DAO<QuestData> {

    @Query("SELECT DISTINCT name FROM character_quests WHERE charId=:playerId: AND var='<state>' ORDER by name")
    List<String> findQuestNameByPlayerAndState(int playerId);

    @Query("SELECT var, value FROM character_quests WHERE charId=:playerId: AND name=:name: AND var != '<state>'")
    List<QuestData> findByPlayerAndNameExcludeState(int playerId, String name);

    @Query("SELECT DISTINCT name FROM character_quests WHERE charId=:playerId: AND var='<state>' AND value=:value:")
    List<String> findQuestNameByPlayerAndStateValue(int playerId, String value);

    @Query("DELETE FROM character_quests WHERE charId=:playerId: AND name=:name: AND var=:var:")
    void deleteQuestVar(int playerId, String name, String var);

    @Query("DELETE FROM character_quests WHERE charId=:playerId: AND name=:name:")
    void deleteQuest(int playerId, String name);

    @Query("DELETE FROM character_quests WHERE charId=:playerId: AND name=:name: AND var != '<state>'")
    void deleteNonRepeatable(int playerId, String name);

    @Query("UPDATE character_quests SET value=:value: WHERE charId=:playerId: AND name=:questName: AND var = :var:")
    void updateQuestVar(int playerId, String questName, String var, String value);

    @Query("SELECT * FROM character_quests WHERE charId = :playerId: AND var = '<state>'")
    List<QuestData> findPlayerQuestsByState(int playerId);

    @Query("SELECT * FROM character_quests WHERE charId = :playerId: AND var <> '<state>'")
    List<QuestData> findPlayerQuestsByNonState(int playerId);

    @Query("INSERT INTO character_quests (charId,name,var,value) VALUES (:playerId:,:name:,:var:,:value:) ON DUPLICATE KEY UPDATE value=:value:")
    void saveQuestVar(int playerId, String name, String var, String value);
}
