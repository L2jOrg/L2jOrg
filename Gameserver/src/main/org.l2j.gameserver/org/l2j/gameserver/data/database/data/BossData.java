package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.model.Spawn;

@Table("boss_data")
public class BossData {
    @Column("boss_id")
    private int bossId;

    @Column("respawn_time")
    private long respawnTime;

    private int x;
    private int y;
    private int z;
    private int heading;
    private double hp;
    private double mp;

    public int getBossId() {
        return bossId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public long getRespawnTime() {
        return respawnTime;
    }

    public void setRespawnTime(long respawnTime) {
        this.respawnTime = respawnTime;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public double getHp() {
        return hp;
    }

    public void setHp(double hp) {
        this.hp = hp;
    }

    public double getMp() {
        return mp;
    }

    public void setMp(double mp) {
        this.mp = mp;
    }

    public static BossData of(int id, Spawn spawn) {
        var data = new BossData();
        data.bossId = id;
        data.x = spawn.getX();
        data.y = spawn.getY();
        data.z = spawn.getZ();
        data.heading = spawn.getHeading();
        return data;
    }
}
