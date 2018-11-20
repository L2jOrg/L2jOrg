package org.l2j.gameserver.templates.fakeplayer.actions;

import org.l2j.gameserver.ai.FakeAI;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.RestartType;
import org.l2j.gameserver.network.l2.c2s.RequestRestartPoint;

public class ReviveAction extends AbstractAction
{
	public ReviveAction()
	{
		super(100);
	}

	@Override
	public boolean performAction(FakeAI ai)
	{
		Player player = ai.getActor();
		if(player.isDead())
			RequestRestartPoint.requestRestart(player, RestartType.TO_VILLAGE);
		return true;
	}
}