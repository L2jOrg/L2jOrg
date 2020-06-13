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

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnNpcEventReceived implements IBaseEvent {
    private final String _eventName;
    private final Npc _sender;
    private final Npc _receiver;
    private final WorldObject _reference;

    public OnNpcEventReceived(String eventName, Npc sender, Npc receiver, WorldObject reference) {
        _eventName = eventName;
        _sender = sender;
        _receiver = receiver;
        _reference = reference;
    }

    public String getEventName() {
        return _eventName;
    }

    public Npc getSender() {
        return _sender;
    }

    public Npc getReceiver() {
        return _receiver;
    }

    public WorldObject getReference() {
        return _reference;
    }

    @Override
    public EventType getType() {
        return EventType.ON_NPC_EVENT_RECEIVED;
    }
}
