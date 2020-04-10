package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.annotation.Transient;
import org.l2j.gameserver.engine.costume.Costume;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author JoeAlisson
 */
@Table("player_costumes")
public class CostumeData {

    @Column("player_id")
    private int playerId;
    private int id;
    private int amount;

    @Transient
    private Costume costume;

    public void increaseAmount() {
        amount++;
    }

    public static CostumeData of(Player player, Costume costume) {
        var data = new CostumeData();
        data.playerId = player.getObjectId();
        data.costume = costume;
        data.id = costume.getId();
        return data;
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }
}
