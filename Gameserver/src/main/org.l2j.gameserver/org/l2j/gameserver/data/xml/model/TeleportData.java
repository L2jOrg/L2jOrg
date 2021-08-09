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
package org.l2j.gameserver.data.xml.model;

import org.l2j.gameserver.model.Location;

public class TeleportData {

    private final byte castleId;
    private final long price;
    private final Location location;

    public TeleportData(long price, Location location, byte castleId) {
        this.price = price;
        this.location = location;
        this.castleId = castleId;
    }

    public long getPrice() {
        return price;
    }

    public Location getLocation() {
        return location;
    }

    public byte getCastleId() {
        return castleId;
    }
}