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
package org.l2j.gameserver.model.holders;


import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.model.Location;

/**
 * @author St3eT
 */
public class CastleSpawnHolder extends Location {
    private final int _npcId;
    private final CastleSide _side;

    public CastleSpawnHolder(int npcId, CastleSide side, int x, int y, int z, int heading) {
        super(x, y, z, heading);
        _npcId = npcId;
        _side = side;
    }

    public final int getNpcId() {
        return _npcId;
    }

    public final CastleSide getSide() {
        return _side;
    }
}
