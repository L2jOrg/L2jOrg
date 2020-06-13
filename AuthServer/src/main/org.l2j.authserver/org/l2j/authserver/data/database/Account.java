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
package org.l2j.authserver.data.database;

import org.l2j.authserver.settings.AuthServerSettings;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

import static org.l2j.commons.configuration.Configurator.getSettings;

@Table("accounts")
public class Account  {

    private String login;
    private String password;
    @Column("last_access")
    private long lastAccess;
    @Column("access_level")
    private int accessLevel;
    @Column("last_server")
    private int lastServer;
    @Column("last_ip")
    private String lastIP;

    public Account() { }

    public Account(String login, String password, long lastAccess, String lastIP) {
        this.login = login;
        this.password = password;
        this.lastAccess = lastAccess;
        this.lastIP = lastIP;
        this.accessLevel = 0;
        this.lastServer = 1;
    }

    public String getLogin() {
        return login;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public String getPassword() {
        return password;
    }

    public int getLastServer() {
        return lastServer;
    }

    public boolean isBanned() {
        return accessLevel < 0;
    }

    public void setLastAccess(long lastAccess) {
        this.lastAccess= lastAccess;
    }

    public Long getLastAccess() {
        return lastAccess;
    }

    public void setLastIP(String lastIP) {
        this.lastIP = lastIP;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public boolean isGM() {
        return accessLevel >= getSettings(AuthServerSettings.class).gmMinimumLevel();
    }
}
