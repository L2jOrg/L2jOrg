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
package org.l2j.gameserver.mobius.gameserver.model.zone.type;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.zone.L2ZoneRespawn;
import org.l2j.gameserver.mobius.gameserver.model.zone.ZoneId;

/**
 * based on Kerberos work for custom L2CastleTeleportZone
 *
 * @author Nyaran
 */
public class L2ResidenceTeleportZone extends L2ZoneRespawn {
    private int _residenceId;

    public L2ResidenceTeleportZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equals("residenceId")) {
            _residenceId = Integer.parseInt(value);
        } else {
            super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(L2Character character) {
        character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true); // FIXME: Custom ?
    }

    @Override
    protected void onExit(L2Character character) {
        character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false); // FIXME: Custom ?
    }

    @Override
    public void oustAllPlayers() {
        for (L2PcInstance player : getPlayersInside()) {
            if ((player != null) && player.isOnline()) {
                player.teleToLocation(getSpawnLoc(), 200);
            }
        }
    }

    public int getResidenceId() {
        return _residenceId;
    }
}
