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
package org.l2j.gameserver.engine.siege;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;

/**
 * @author JoeAlisson
 */
public class Mercenary {

    private int id;
    private String name;
    private ClassId classId;
    private boolean online;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClassId getClassId() {
        return classId;
    }

    public void setClassId(ClassId classId) {
        this.classId = classId;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isOnline() {
        return online;
    }

    public void update(Player player) {
        this.classId = player.getClassId();
        this.name = player.getName();
    }

    public static Mercenary of(Player player) {
        final var mercenary = new Mercenary();
        mercenary.setId(player.getId());
        mercenary.setName(player.getName());
        mercenary.setClassId(player.getClassId());
        return mercenary;
    }
}
