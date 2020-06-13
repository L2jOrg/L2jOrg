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

/**
 * @author JoeAlisson
 */
public class KillerData {
    @Column("killer_id")
    private int killeId;

    private String name;
    private String clan;
    private int level;
    private int race;

    @Column("active_class")
    private int activeClass;

    @Column("kill_time")
    private int killTime;
    private boolean online;

    public int getKilleId() {
        return killeId;
    }

    public String getName() {
        return name;
    }

    public String getClan() {
        return clan;
    }

    public int getLevel() {
        return level;
    }

    public int getRace() {
        return race;
    }

    public int getActiveClass() {
        return activeClass;
    }

    public int getKillTime() {
        return killTime;
    }

    public boolean isOnline() {
        return online;
    }
}
