package org.l2j.gameserver.templates.fakeplayer.actions;

import org.l2j.gameserver.ai.FakeAI;
import org.l2j.gameserver.model.Player;

public abstract class MoveAction extends AbstractAction
{
	public MoveAction(double chance)
	{
		super(chance);
	}

	@Override
	public boolean checkCondition(FakeAI ai, boolean force)
	{
		Player player = ai.getActor();
		if(player.isMovementDisabled())
			return false;

		if(!force)
		{
			if(player.isMoving)
				return false;
		}
		return true;
	}
}