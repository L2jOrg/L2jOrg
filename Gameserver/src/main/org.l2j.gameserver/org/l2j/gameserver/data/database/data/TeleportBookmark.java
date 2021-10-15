/*
 * Copyright © 2019 L2J Mobius
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
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.model.interfaces.ILocational;

/**
 * @author JoeAlisson
 */
@Table("character_tpbookmark")
public class TeleportBookmark implements ILocational {

    @Column("charId")
    private int playerId;
    private int id;
    private int x;
    private int y;
    private int z;
    private int icon;
    private String name;
    private String tag;

    public static TeleportBookmark of(int playerId, int id, int x, int y, int z, int icon, String tag, String name) {
        var data = new TeleportBookmark();
        data.id = id;
        data.x = x;
        data.y = y;
        data.z = z;
        data.icon = icon;
        data.name = name;
        data.tag = tag;
        data.playerId = playerId;
        return data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public int getHeading() {
        return 0;
    }

    @Override
    public ILocational getLocation() {
        return null;
    }
}
