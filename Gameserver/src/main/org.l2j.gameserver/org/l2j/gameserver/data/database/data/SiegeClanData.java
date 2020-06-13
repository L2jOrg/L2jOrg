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
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.enums.SiegeClanType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.SiegeFlag;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
@Table("siege_clans")
public class SiegeClanData {

    @NonUpdatable
    private final Set<Npc> flags = ConcurrentHashMap.newKeySet();

    @Column("castle_id")
    private int castleId;

    @Column("clan_id")
    private int clanId;

    private SiegeClanType type;

    public SiegeClanData() {
    }

    public SiegeClanData(int id, SiegeClanType type, int castleId) {
        this.clanId = id;
        this.type = type;
        this.castleId = castleId;
    }

    public int getCastleId() {
        return castleId;
    }

    public int getClanId() {
        return clanId;
    }

    public SiegeClanType getType() {
        return type;
    }

    public void setType(SiegeClanType type) {
        this.type = type;
    }

    public Set<Npc> getFlags() {
        return flags;
    }

    public boolean removeFlag(Npc flag) {
        if (isNull(flag)) {
            return false;
        }

        flag.deleteMe();

        return flags.remove(flag);
    }

    public void removeFlags() {
        flags.forEach(this::removeFlag);
    }

    public void addFlag(SiegeFlag siegeFlag) {
        flags.add(siegeFlag);
    }

    public int getNumFlags() {
        return flags.size();
    }
}
