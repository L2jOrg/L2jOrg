/*
 * Copyright © 2019-2021 L2JOrg
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
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;

/**
 * @author JoeAlisson
 */
@Table("siege_mercenaries")
public class Mercenary {

    @Column("mercenary")
    private int id;
    @Column("clan_id")
    private int clanId;

    @NonUpdatable
    private int classId;
    @NonUpdatable
    private String name;
    @NonUpdatable
    private boolean online;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getClanId() {
        return clanId;
    }

    public int getClassId() {
        return classId;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isOnline() {
        return online;
    }

    public void update(Player player) {
        this.classId = player.getClassId().getId();
        this.name = player.getName();
    }

    public static Mercenary of(Player player, SiegeParticipant participant) {
        final var mercenary = new Mercenary();
        mercenary.id = player.getId();
        mercenary.name = player.getName();
        mercenary.classId = player.getClassId().getId();
        mercenary.clanId = participant.getClanId();
        return mercenary;
    }
}
