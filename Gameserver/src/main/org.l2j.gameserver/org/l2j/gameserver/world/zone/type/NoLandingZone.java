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

import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneFactory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A no landing zone
 *
 * @author durgus
 */
public class NoLandingZone extends Zone {
    private int dismountDelay = 5;

    private NoLandingZone(int id, int dismountDelay) {
        super(id);
        this.dismountDelay = dismountDelay;
    }

    @Override
    protected void onEnter(Creature creature) {
        if (isPlayer(creature)) {
            creature.setInsideZone(ZoneType.NO_LANDING, true);
            if (creature.getActingPlayer().getMountType() == MountType.WYVERN) {
                creature.sendPacket(SystemMessageId.THIS_AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_ATOP_OF_A_WYVERN_YOU_WILL_BE_DISMOUNTED_FROM_YOUR_WYVERN_IF_YOU_DO_NOT_LEAVE);
                creature.getActingPlayer().enteredNoLanding(dismountDelay);
            }
        }
    }

    @Override
    protected void onExit(Creature creature) {
        if (isPlayer(creature)) {
            creature.setInsideZone(ZoneType.NO_LANDING, false);
            if (creature.getActingPlayer().getMountType() == MountType.WYVERN) {
                creature.getActingPlayer().exitedNoLanding();
            }
        }
    }

    public static class Factory implements ZoneFactory {

        @Override
        public Zone create(int id, Node zoneNode, GameXmlReader reader) {
            var dismountDelay = reader.parseInt(zoneNode.getAttributes(), "dismount-delay");
            return new NoLandingZone(id, dismountDelay);
        }

        @Override
        public String type() {
            return "no-landing";
        }
    }
}
