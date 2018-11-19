package l2s.gameserver.listener.actor.ai;

import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.listener.AiListener;
import l2s.gameserver.model.Creature;

public interface OnAiIntentionListener extends AiListener
{
	public void onAiIntention(Creature actor, CtrlIntention intention, Object arg0, Object arg1);
}