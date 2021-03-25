package org.l2j.gameserver.data.database.dao;

import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.MacroCmdData;
import org.l2j.gameserver.data.database.data.MacroData;
import org.l2j.gameserver.model.Macro;

import java.util.List;

public interface MacroDAO extends DAO<MacroData> {

    void save(List<MacroCmdData> commands);

    @Query("DELETE FROM macros WHERE player_id=:playerId: AND id=:id:")
    void deleteMacro(int playerId, int id);

    @Query(value = """
           SELECT * FROM macros
           JOIN macro_commands mc on macros.id = mc.macro_id
                AND macros.player_id = mc.macro_player_id
           WHERE player_id = :playerId:
           """, scrollResult = true)
    IntMap<Macro> findAllByPlayer(int playerId);
}
