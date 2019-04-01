package org.l2j.authserver.data.database.dao;


import org.l2j.authserver.data.database.Account;
import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

public interface AccountDAO extends DAO {

    @Query("SELECT * FROM accounts WHERE login=:login:")
    Account findById(String login);

    @Query("INSERT INTO accounts (login, password, last_access, last_ip) VALUES (:login:, :password:, :lastAccess:, :lastIP:)")
    void save(String login, String password, long lastAccess, String lastIP);

    @Query("UPDATE accounts SET last_access=:lastAccess:, last_ip=:lastIP: WHERE login=:login:")
    void updateAccess(String login, long lastAccess, String lastIP);

    @Query("UPDATE accounts SET access_level=:accessLevel: WHERE login=:login:")
    int updateAccessLevel(String login, short accessLevel);

    @Query("UPDATE accounts SET last_server=:server: WHERE login=:login:")
    int updateLastServer(String login, int server);
}
