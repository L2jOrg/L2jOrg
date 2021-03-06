/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.teleporter.TeleportHolder;
import org.l2j.gameserver.model.teleporter.TeleportLocation;

/**
 * Player teleport request listner - called from {@link TeleportHolder#doTeleport(Player, Npc, int)}
 *
 * @author malyelfik
 */
public final class OnNpcTeleportRequest implements IBaseEvent {
    private final Player _player;
    private final Npc _npc;
    private final TeleportLocation _loc;

    public OnNpcTeleportRequest(Player player, Npc npc, TeleportLocation loc) {
        _player = player;
        _npc = npc;
        _loc = loc;
    }

    public Player getPlayer() {
        return _player;
    }

    public Npc getNpc() {
        return _npc;
    }

    public TeleportLocation getLocation() {
        return _loc;
    }

    @Override
    public EventType getType() {
        return EventType.ON_NPC_TELEPORT_REQUEST;
    }
}
