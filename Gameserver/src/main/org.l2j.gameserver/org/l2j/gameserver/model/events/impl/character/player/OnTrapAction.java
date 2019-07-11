package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.enums.TrapAction;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.L2TrapInstance;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnTrapAction implements IBaseEvent {
    private final L2TrapInstance _trap;
    private final Creature _trigger;
    private final TrapAction _action;

    public OnTrapAction(L2TrapInstance trap, Creature trigger, TrapAction action) {
        _trap = trap;
        _trigger = trigger;
        _action = action;
    }

    public L2TrapInstance getTrap() {
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
