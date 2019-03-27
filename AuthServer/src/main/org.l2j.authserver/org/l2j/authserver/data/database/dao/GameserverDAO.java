package org.l2j.authserver.data.database.dao;

import org.l2j.authserver.data.database.ServerInfo;
import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

import java.util.List;

public interface GameserverDAO extends DAO {

    @Query("SELECT * FROM gameservers")
    List<ServerInfo> findAll();

    @Query("INSERT INTO gameservers VALUES (:id:, :host:, :serverType:)")
    void save(int id, String host, int serverType);

    @Query("UPDATE gameservers  SET server_type = :serverType: WHERE server_id = :id:")
    void updateServerType(int id, int serverType);
}
