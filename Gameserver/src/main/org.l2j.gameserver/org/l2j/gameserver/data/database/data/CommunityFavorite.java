package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Table;

import java.time.LocalDateTime;

/**
 * @author JoeAlisson
 */
@Table("bbs_favorites")
public class CommunityFavorite {

    @NonUpdatable
    @Column("favId")
    private int id;

    private int playerId;

    @Column("favTitle")
    private String title;

    @Column("favBypass")
    private String bypass;

    @NonUpdatable
    @Column("favAddDate")
    private LocalDateTime date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBypass() {
        return bypass;
    }

    public void setBypass(String bypass) {
        this.bypass = bypass;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
