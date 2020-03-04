package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Table;

/**
 * @author JoeAlisson
 */
@Table("castle_functions")
public class CastleFunctionData {

    private int type;
    private int level;
    private int lease;
    private long rate;
    private long endTime;

    public CastleFunctionData() {

    }

    public CastleFunctionData(int type, int level, int lease, long rate, int time) {
        this.type = type;
        this.level = level;
        this.lease = lease;
        this.rate = rate;
        endTime = time;
    }

    public int getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int lvl) {
        level = lvl;
    }

    public int getLease() {
        return lease;
    }

    public void setLease(int lease) {
        this.lease = lease;
    }

    public long getRate() {
        return rate;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long time) {
        endTime = time;
    }

}
