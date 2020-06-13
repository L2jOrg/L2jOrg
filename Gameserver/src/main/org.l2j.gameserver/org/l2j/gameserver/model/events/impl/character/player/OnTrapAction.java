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
package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.enums.TrapAction;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Trap;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnTrapAction implements IBaseEvent {
    private final Trap _trap;
    private final Creature _trigger;
    private final TrapAction _action;

    public OnTrapAction(Trap trap, Creature trigger, TrapAction action) {
        _trap = trap;
        _trigger = trigger;
        _action = action;
    }

    public Trap getTrap() {
        return _trap;
    }

    public Creature getTrigger() {
        return _trigger;
    }

    public TrapAction getAction() {
        return _action;
    }

    @Override
    public EventType getType() {
        return EventType.ON_TRAP_ACTION;
    }

}
