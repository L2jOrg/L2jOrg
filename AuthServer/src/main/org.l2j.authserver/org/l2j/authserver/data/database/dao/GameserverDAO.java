package org.l2j.authserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.authserver.data.database.GameServer;

import java.util.List;

public interface GameserverDAO extends DAO {

    @Query("SELECT * FROM gameservers")
    List<GameServer> findAll();

    @Query("INSERT INTO gameservers VALUES (:id:, :host:)")
    void save(int id, String host);
}
