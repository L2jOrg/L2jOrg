package org.l2j.gameserver.listener.actor.ai;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.listener.AiListener;
import org.l2j.gameserver.model.Creature;

public interface OnAiIntentionListener extends AiListener
{
	public void onAiIntention(Creature actor, CtrlIntention intention, Object arg0, Object arg1);
}