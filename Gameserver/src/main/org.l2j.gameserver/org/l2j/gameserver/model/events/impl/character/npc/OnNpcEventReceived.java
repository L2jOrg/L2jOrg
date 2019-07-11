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
package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnNpcEventReceived implements IBaseEvent {
    private final String _eventName;
    private final L2Npc _sender;
    private final L2Npc _receiver;
    private final WorldObject _reference;

    public OnNpcEventReceived(String eventName, L2Npc sender, L2Npc receiver, WorldObject reference) {
        _eventName = eventName;
        _sender = sender;
        _receiver = receiver;
        _reference = reference;
    }

    public String getEventName() {
        return _eventName;
    }

    public L2Npc getSender() {
        return _sender;
    }

    public L2Npc getReceiver() {
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
