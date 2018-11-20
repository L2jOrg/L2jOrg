package org.l2j.commons.database.model;

import org.l2j.commons.Config;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.springframework.data.annotation.Id;

@Table("accounts")
public class Account extends Entity<String> {

    @Id
    private String login;
    private String password;
    private Long lastActive;
    @Column("access_level")
    private Integer accessLevel;
    private Integer lastServer;
    private String lastIP;
    private Integer newbieCharacterId;

    public Account() { }

    public Account(String login, String password, long lastActive, String lastIP) {
        this.login = login;
        this.password = password;
        this.lastActive = lastActive;
        this.lastIP = lastIP;
        this.accessLevel = 0;
        this.lastServer = 1;
    }

    @Override
    public String getId() {
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

    public void setLastActive(long lastActive) {
        this.lastActive = lastActive;
    }

    public void setLastIP(String lastIP) {
        this.lastIP = lastIP;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public boolean isGM() {
        return accessLevel >= Config.GM_MIN;
    }

    public int getNewbieCharacterId() {
        return newbieCharacterId;
    }

    public void setNewbieCharacterId(int newbieCharacterId) {
        this.newbieCharacterId = newbieCharacterId;
    }
}
