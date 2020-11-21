package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

/**
 * @author JoeAlisson
 */
@Table("olympiad_hero")
public class OlympiadHeroData {

    @Column("player_id")
    private int playerId;
    private int server;

    @Column("class_id")
    private int classId;
    private boolean legend;
    private boolean claimed;
    private String name;

    @Column("clan_name")
    private String clanName;

    @Column("clan_level")
    private int clanLevel;
    private byte sex;
    private byte race;
    private int level;

    @Column("legend_count")
    private int legendCount;

    @Column("hero_count")
    private int heroCount;

    @Column("battles_won")
    private int battlesWon;

    @Column("battles_lost")
    private int battlesLost;
    private int points;

    public int getPlayerId() {
        return playerId;
    }

    public int getServer() {
        return server;
    }

    public int getClassId() {
        return classId;
    }

    public boolean isLegend() {
        return legend;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public String getName() {
        return name;
    }

    public String getClanName() {
        return clanName;
    }

    public int getClanLevel() {
        return clanLevel;
    }

    public byte getSex() {
        return sex;
    }

    public int getLevel() {
        return level;
    }

    public int getLegendCount() {
        return legendCount;
    }

    public int getHeroCount() {
        return heroCount;
    }

    public int getBattlesWon() {
        return battlesWon;
    }

    public int getBattlesLost() {
        return battlesLost;
    }

    public int getPoints() {
        return points;
    }

    public byte getRace() {
        return race;
    }
}
