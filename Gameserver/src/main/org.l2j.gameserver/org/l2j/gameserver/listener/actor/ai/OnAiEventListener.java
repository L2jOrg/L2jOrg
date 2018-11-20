package org.l2j.gameserver.listener.actor.ai;

import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.listener.AiListener;
import org.l2j.gameserver.model.Creature;

public interface OnAiEventListener extends AiListener
{
	public void onAiEvent(Creature actor, CtrlEvent evt, Object[] args);
}