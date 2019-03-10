package org.l2j.gameserver.mobius.gameserver.model.actor.tasks.character;

import org.l2j.gameserver.mobius.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;

/**
 * Task dedicated to notify character's AI
 *
 * @author xban1x
 */
public final class NotifyAITask implements Runnable {
    private final L2Character _character;
    private final CtrlEvent _event;

    public NotifyAITask(L2Character character, CtrlEvent event) {
        _character = character;
        _event = event;
    }

    @Override
    public void run() {
        if (_character != null) {
            _character.getAI().notifyEvent(_event, null);
        }
    }
}
