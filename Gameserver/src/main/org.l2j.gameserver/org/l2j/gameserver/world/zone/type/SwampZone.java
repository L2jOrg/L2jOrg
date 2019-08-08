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

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.serverpackets.OnEventTrigger;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * another type of zone where your speed is changed
 *
 * @author kerberos, Pandragon
 */
public class SwampZone extends Zone {
    private double _move_bonus;
    private int _castleId;
    private Castle _castle;
    private int _eventId;

    public SwampZone(int id) {
        super(id);

        // Setup default speed reduce (in %)
        _move_bonus = 0.5;

        // no castle by default
        _castleId = 0;
        _castle = null;

        // no event by default
        _eventId = 0;
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equals("move_bonus")) {
            _move_bonus = Double.parseDouble(value);
        } else if (name.equals("castleId")) {
            _castleId = Integer.parseInt(value);
        } else if (name.equals("eventId")) {
            _eventId = Integer.parseInt(value);
        } else {
            super.setParameter(name, value);
        }
    }

    private Castle getCastle() {
        if ((_castleId > 0) && (_castle == null)) {
            _castle = CastleManager.getInstance().getCastleById(_castleId);
        }

        return _castle;
    }

    @Override
    protected void onEnter(Creature character) {
        if (getCastle() != null) {
            // castle zones active only during siege
            if (!getCastle().getSiege().isInProgress() || !isEnabled()) {
                return;
            }

            // defenders not affected
            final Player player = character.getActingPlayer();
            if ((player != null) && player.isInSiege() && (player.getSiegeState() == 2)) {
                return;
            }
        }

        character.setInsideZone(ZoneType.SWAMP, true);
        if (isPlayer(character)) {
            if (_eventId > 0) {
                character.sendPacket(new OnEventTrigger(_eventId, true));
            }
            character.getActingPlayer().broadcastUserInfo();
        }
    }

    @Override
    protected void onExit(Creature character) {
        // don't broadcast info if not needed
        if (character.isInsideZone(ZoneType.SWAMP)) {
            character.setInsideZone(ZoneType.SWAMP, false);
            if (isPlayer(character)) {
                if (_eventId > 0) {
                    character.sendPacket(new OnEventTrigger(_eventId, false));
                }
                character.getActingPlayer().broadcastUserInfo();
            }
        }
    }

    public double getMoveBonus() {
        return _move_bonus;
    }
}