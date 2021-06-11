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

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneFactory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.w3c.dom.Node;

/**
 * A castle zone
 *
 * @author durgus
 * @author JoeAlisson
 */
public final class CastleZone extends ResidenceZone {

    private CastleZone(int id, int castleId) {
        super(id, castleId);
    }

    @Override
    protected void onEnter(Creature creature) {
        creature.setInsideZone(ZoneType.CASTLE, true);
    }

    @Override
    protected void onExit(Creature creature) {
        creature.setInsideZone(ZoneType.CASTLE, false);
    }

    public static class Factory implements ZoneFactory {

        @Override
        public Zone create(int id, Node zoneNode, GameXmlReader reader) {
            var castleId = reader.parseInt(zoneNode.getAttributes(), "castle-id");
            var zone = new CastleZone(id, castleId);

            var castle = CastleManager.getInstance().getCastleById(castleId);
            if(castle != null) {
                castle.setResidenceZone(zone);
            }
            return zone;
        }

        @Override
        public String type() {
            return "castle";
        }
    }
}
