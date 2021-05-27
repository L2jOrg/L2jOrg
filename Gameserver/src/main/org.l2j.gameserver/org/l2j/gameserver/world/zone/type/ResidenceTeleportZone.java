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
package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneFactory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.w3c.dom.Node;

/**
 * based on Kerberos work for custom L2CastleTeleportZone
 *
 * @author Nyaran
 * @author JoeAlisson
 */
public class ResidenceTeleportZone extends SpawnZone {
    private final int residenceId;

    private ResidenceTeleportZone(int id, int residenceId) {
        super(id);
        this.residenceId =residenceId;
    }


    @Override
    protected void onEnter(Creature creature) {
        creature.setInsideZone(ZoneType.NO_SUMMON_FRIEND, true); // FIXME: Custom ?
    }

    @Override
    protected void onExit(Creature creature) {
        creature.setInsideZone(ZoneType.NO_SUMMON_FRIEND, false); // FIXME: Custom ?
    }

    @Override
    public void oustAllPlayers() {
        forEachPlayer(p -> p.teleToLocation(getSpawnLoc(), 200));
    }

    public int getResidenceId() {
        return residenceId;
    }

    public static class Factory implements ZoneFactory {

        @Override
        public Zone create(int id, Node zoneNode, GameXmlReader reader) {
            var residenceId = reader.parseInt(zoneNode.getAttributes(), "residence-id");
            return new ResidenceTeleportZone(id, residenceId);
        }

        @Override
        public String type() {
            return "residence-teleport";
        }
    }
}
