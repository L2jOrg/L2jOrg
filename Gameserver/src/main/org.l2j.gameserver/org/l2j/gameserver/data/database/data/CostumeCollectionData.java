package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.model.actor.instance.Player;

import java.time.Duration;
import java.time.Instant;

/**
 * @author JoeAlisson
 */
@Table("player_costume_collection")
public class CostumeCollectionData {

    public static CostumeCollectionData DEFAULT = new CostumeCollectionData();

    @Column("player_id")
    private int playerId;
    private int id;
    private int reuse;

    public static CostumeCollectionData of(Player player, int id) {
        var collection = new CostumeCollectionData();
        collection.playerId = player.getObjectId();
        collection.id = id;
        return collection;
    }

    public int getId() {
        return id;
    }

    public void updateReuseTime() {
        reuse = (int) Instant.now().plus(Duration.ofMinutes(10)).getEpochSecond();
    }

    public int getReuseTime() {
        return (int) Math.max(0, reuse - Instant.now().getEpochSecond());
    }
}
