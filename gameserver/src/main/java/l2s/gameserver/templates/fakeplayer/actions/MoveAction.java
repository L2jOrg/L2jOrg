package l2s.gameserver.templates.fakeplayer.actions;

import l2s.gameserver.ai.FakeAI;
import l2s.gameserver.model.Player;

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