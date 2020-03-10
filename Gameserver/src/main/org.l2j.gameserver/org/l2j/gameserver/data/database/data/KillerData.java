package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;

/**
 * @author JoeAlisson
 */
public class KillerData {
    @Column("killer_id")
    private int killeId;

    private String name;
    private String clan;
    private int level;
    private int race;

    @Column("active_class")
    private int activeClass;

    @Column("kill_time")
    private int killTime;
    private boolean online;

    public int getKilleId() {
        return killeId;
    }

    public String getName() {
        return name;
    }

    public String getClan() {
        return clan;
    }

    public int getLevel() {
        return level;
    }

    public int getRace() {
        return race;
    }

    public int getActiveClass() {
        return activeClass;
    }

    public int getKillTime() {
        return killTime;
    }

    public boolean isOnline() {
        return online;
    }
}
