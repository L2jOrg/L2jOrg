/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
