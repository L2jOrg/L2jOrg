package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.SeedProduction;

import java.util.Collection;
import java.util.List;

/**
 * @author JoeAlisson
 */
public interface ManorProductionDAO extends DAO<SeedProduction> {

    @Query("SELECT * FROM castle_manor_production WHERE castle_id=:id:")
    List<SeedProduction> findManorProductionByCastle(int id);

    @Query("TRUNCATE castle_manor_production")
    void deleteManorProduction();

    boolean save(Collection<SeedProduction> productions);
}
