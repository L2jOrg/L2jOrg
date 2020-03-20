package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.enums.ClanWarState;
import org.l2j.gameserver.model.Clan;

/**
 * @author JoeAlisson
 */
@Table("clan_wars")
public class ClanWarData {

    @Column("clan1")
    private int attacker;

    @Column("clan2")
    private int attacked;

    @Column("clan1Kill")
    private int attackerKills;

    @Column("clan2Kill")
    private int attackedKills;

    private int winnerClan;
    private long startTime;
    private long endTime;
    private ClanWarState state;


    public static ClanWarData of(Clan attacker, Clan attacked) {
        var data = new ClanWarData();
        data.attacker = attacker.getId();
        data.attacked = attacked.getId();
        data.startTime = System.currentTimeMillis();
        data.state = ClanWarState.BLOOD_DECLARATION;
        return data;
    }

    public int getAttacker() {
        return attacker;
    }

    public void setAttacker(int attacker) {
        this.attacker = attacker;
    }

    public int getAttacked() {
        return attacked;
    }

    public void setAttacked(int attacked) {
        this.attacked = attacked;
    }

    public int getAttackerKills() {
        return attackerKills;
    }

    public void setAttackerKills(int attackerKills) {
        this.attackerKills = attackerKills;
    }

    public int getAttackedKills() {
        return attackedKills;
    }

    public void setAttackedKills(int attackedKills) {
        this.attackedKills = attackedKills;
    }

    public int getWinnerClan() {
        return winnerClan;
    }

    public void setWinnerClan(int winnerClan) {
        this.winnerClan = winnerClan;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public ClanWarState getState() {
        return state;
    }

    public void setState(ClanWarState state) {
        this.state = state;
    }

    public synchronized void incrementAttackerKill() {
        attackerKills++;
    }

    public synchronized int incrementAttackedKill() {
        return ++attackedKills;
    }
}
