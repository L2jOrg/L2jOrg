package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.Player;
import l2s.gameserver.stats.Env;

/**
 * @author Bonux
**/
public class ConditionPlayerCanUntransform extends Condition
{
	private final boolean _val;

	public ConditionPlayerCanUntransform(boolean val)
	{
		_val = val;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return !_val;

		Player player = env.character.getPlayer();
		if(!player.isTransformed())
			return !_val;

		// Нельзя отменять летающую трансформу слишком высоко над землей
		if(player.isInFlyingTransform() && Math.abs(player.getZ() - player.getLoc().correctGeoZ().z) > 333)
			return !_val;

		return _val;
	}
}