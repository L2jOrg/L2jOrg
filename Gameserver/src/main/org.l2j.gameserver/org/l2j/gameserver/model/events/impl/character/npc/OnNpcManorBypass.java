/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author malyelfik
 */
public final class OnNpcManorBypass implements IBaseEvent {
    private final Player _activeChar;
    private final Npc _target;
    private final int _request;
    private final int _manorId;
    private final boolean _nextPeriod;

    public OnNpcManorBypass(Player activeChar, Npc target, int request, int manorId, boolean nextPeriod) {
        _activeChar = activeChar;
        _target = target;
        _request = request;
        _manorId = manorId;
        _nextPeriod = nextPeriod;
    }

    public Player getActiveChar() {
        return _activeChar;
    }

    public Npc getTarget() {
        return _target;
    }

    public int getRequest() {
        return _request;
    }

    public int getManorId() {
        return _manorId;
    }

    public boolean isNextPeriod() {
        return _nextPeriod;
    }

    @Override
    public EventType getType() {
        return EventType.ON_NPC_MANOR_BYPASS;
    }
}