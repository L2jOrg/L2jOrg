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
package org.l2j.authserver.data.database.dao;


import org.l2j.authserver.data.database.Account;
import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

public interface AccountDAO extends DAO<Account> {

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
