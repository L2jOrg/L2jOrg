package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.ResidenceFunctionData;

import java.util.List;

/**
 * @author JoeAlisson
 */
public interface ResidenceDAO extends DAO<ResidenceFunctionData> {

    @Query("REPLACE INTO residence_functions (id, level, expiration, residenceId) VALUES (:id:, :level:, :expiration:, :residence:)")
    void saveFunction(int id, int level, long expiration, int residence);

    @Query("SELECT * FROM residence_functions WHERE residenceId = :residenceId:")
    List<ResidenceFunctionData> findFunctionsByResidence(int residenceId);

    @Query("DELETE FROM residence_functions WHERE residenceId = :residenceId: and id = :functionId:")
    void deleteFunction(int functionId, int residenceId);

    @Query("DELETE FROM residence_functions WHERE residenceId = :id:")
    void deleteFunctionsByResidence(int id);
}
