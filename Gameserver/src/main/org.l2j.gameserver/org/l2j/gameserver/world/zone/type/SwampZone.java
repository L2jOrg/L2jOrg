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
package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.serverpackets.OnEventTrigger;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * another type of zone where your speed is changed
 *
 * @author kerberos, Pandragon
 */
public class SwampZone extends Zone {
    private double moveBonus;
    private int castleId;
    private Castle castle;
    private int eventId;

    public SwampZone(int id) {
        super(id);

        // Setup default speed reduce (in %)
        moveBonus = 0.5;

        // no castle by default
        castleId = 0;
        castle = null;

        // no event by default
        eventId = 0;
    }

    @Override
    public void setParameter(String name, String value) {
        switch (name) {
            case "move_bonus" -> moveBonus = Double.parseDouble(value);
            case "castleId" -> castleId = Integer.parseInt(value);
            case "eventId" -> eventId = Integer.parseInt(value);
            default -> super.setParameter(name, value);
        }
    }

    private Castle getCastle() {
        if ((castleId > 0) && (castle == null)) {
            castle = CastleManager.getInstance().getCastleById(castleId);
        }

        return castle;
    }

    @Override
    protected void onEnter(Creature creature) {
        if (getCastle() != null) {
            // castle zones active only during siege
            if (!getCastle().getSiege().isInProgress() || !isEnabled()) {
                return;
            }

            // defenders not affected
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
}