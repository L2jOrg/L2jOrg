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
}
