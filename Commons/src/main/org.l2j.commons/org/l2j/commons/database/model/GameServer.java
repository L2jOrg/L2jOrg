package org.l2j.commons.database.model;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.springframework.data.annotation.Id;

@Table("gameservers")
public class GameServer {

    @Id
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
