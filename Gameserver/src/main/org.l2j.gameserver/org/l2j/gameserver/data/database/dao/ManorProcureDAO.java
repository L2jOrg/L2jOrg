package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.CropProcure;

import java.util.Collection;
import java.util.List;

/**
 * @author JoeAlisson
 */
public interface ManorProcureDAO extends DAO<CropProcure> {

    @Query("SELECT * FROM castle_manor_procure WHERE castle_id=:id:")
    List<CropProcure> findManorProcureByCastle(int id);

    @Query("TRUNCATE castle_manor_procure")
    void deleteManorProcure();

    boolean save(Collection<CropProcure> procures);

}
