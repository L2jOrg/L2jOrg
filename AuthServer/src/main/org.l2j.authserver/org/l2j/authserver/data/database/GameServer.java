package org.l2j.authserver.data.database;

import org.l2j.commons.database.annotation.Column;

public class GameServer {

    @Column("server_id")
    private int serverId;
    private String host;

    public GameServer() { }

    public GameServer(int id, String host) {
        this.serverId = id;
        this.host = host;
    }

    public int getId() {
        return serverId;
    }

    public String getHost() {
        return host;
    }
}
