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
package org.l2j.gameserver.world.zone;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameXmlReader;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author JoeAlisson
 */
public class NaiveZone extends Zone {

    private final ZoneType type;
    private final int enterMessage;
    private final int leaveMessage;

    protected NaiveZone(int id, ZoneType type, int enterMessage, int leaveMessage) {
        super(id);
        this.type = type;
        this.enterMessage = enterMessage;
        this.leaveMessage = leaveMessage;
    }

    @Override
    protected void onEnter(Creature creature) {
        if(enterMessage != 0 && isPlayer(creature) && !creature.isInsideZone(type)) {
            creature.sendPacket(SystemMessage.getSystemMessage(enterMessage));
        }
        creature.setInsideZone(type, true);
    }

    @Override
    protected void onExit(Creature creature) {
        creature.setInsideZone(type, false);
        if(leaveMessage != 0 && isPlayer(creature) && !creature.isInsideZone(type)) {
            creature.sendPacket(SystemMessage.getSystemMessage(leaveMessage));
        }
    }

    public static class Factory implements ZoneFactory {

        @Override
        public Zone create(int id, Node zoneNode, GameXmlReader reader) {
            var attr = zoneNode.getAttributes();
            var type = reader.parseEnum(attr, ZoneType.class, "type");
            var leaveMessage = reader.parseInt(attr, "leave-message");
            var enterMessage = reader.parseInt(attr, "enter-message");
            return new NaiveZone(id, type, enterMessage, leaveMessage);
        }

        @Override
        public String type() {
            return "zone";
        }
    }
}
