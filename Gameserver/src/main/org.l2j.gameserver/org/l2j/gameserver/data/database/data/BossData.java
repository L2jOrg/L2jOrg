package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.instancemanager.BossStatus;

/**
 * @author JoeAlisson
 */
@Table("grandboss_data")
public class BossData {

    @Column("boss_id")
    private int bossId;

    @Column("loc_x")
    private int x;

    @Column("loc_y")
    private int y;

    @Column("loc_z")
    private int z;

    @Column("respawn_time")
    private long respawnTime;

    private int heading;
    private double hp;
    private double mp;
    private BossStatus status;


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

    public BossStatus getStatus() {
        return status;
    }

    public void setStatus(BossStatus status) {
        this.status = status;
    }
}
