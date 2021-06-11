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
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.serverpackets.OnEventTrigger;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneFactory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * another type of zone where your speed is changed
 *
 * @author kerberos, Pandragon
 */
public class SwampZone extends Zone {
    private double moveBonus;
    private Castle castle;
    private int eventId;

    public SwampZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature creature) {
        if (castle != null) {
            if (!isEnabled() || !castle.getSiege().isInProgress()) {
                return;
            }

            final Player player = creature.getActingPlayer();
            if ((player != null) && player.isInSiege() && (player.getSiegeState() == 2)) {
                return;
            }
        }

        creature.setInsideZone(ZoneType.SWAMP, true);
        if (isPlayer(creature)) {
            if (eventId > 0) {
                creature.sendPacket(new OnEventTrigger(eventId, true));
            }
            creature.getActingPlayer().broadcastUserInfo();
        }
    }

    @Override
    protected void onExit(Creature creature) {
        // don't broadcast info if not needed
        if (creature.isInsideZone(ZoneType.SWAMP)) {
            creature.setInsideZone(ZoneType.SWAMP, false);
            if (isPlayer(creature)) {
                if (eventId > 0) {
                    creature.sendPacket(new OnEventTrigger(eventId, false));
                }
                creature.getActingPlayer().broadcastUserInfo();
            }
        }
    }

    public double getMoveBonus() {
        return moveBonus;
    }

    public static class Factory implements ZoneFactory {

        @Override
        public Zone create(int id, Node zoneNode, GameXmlReader reader) {
            var zone = new SwampZone(id);

            for(var node = zoneNode.getFirstChild(); node != null; node = node.getNextSibling()) {
                if(node.getNodeName().equals("attributes")) {
                    var attr = node.getAttributes();
                    zone.moveBonus = reader.parseFloat(attr, "move-bonus");
                    zone.eventId = reader.parseInt(attr, "event-id");
                    zone.setEnabled(reader.parseBoolean(attr, "enabled"));

                    var castleId = reader.parseInt(attr, "castle-id");
                    if(castleId != 0) {
                        zone.castle = CastleManager.getInstance().getCastleById(castleId);
                    }
                    break;
                }
            }
            return zone;
        }

        @Override
        public String type() {
            return "swamp";
        }
    }
}