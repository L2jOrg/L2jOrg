/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.ZoneRespawn;

/**
 * @author xban1x
 */
public abstract class ResidenceZone extends ZoneRespawn {
    private int _residenceId;

    protected ResidenceZone(int id) {
        super(id);
    }

    public void banishForeigners(int owningClanId) {
        for (Player temp : getPlayersInside()) {
            if ((owningClanId != 0) && (temp.getClanId() == owningClanId)) {
                continue;
            }
            temp.teleToLocation(getBanishSpawnLoc(), true);
        }
    }

    public int getResidenceId() {
        return _residenceId;
    }

    protected void setResidenceId(int residenceId) {
        _residenceId = residenceId;
    }
}
