package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.Player;
import l2s.gameserver.stats.Env;

public class ConditionPlayerState extends Condition
{
	public enum CheckPlayerState
	{
		RESTING,
		MOVING,
		RUNNING,
		STANDING,
		FLYING,
		FLYING_TRANSFORM
	}

	private final CheckPlayerState _check;

	private final boolean _required;

	public ConditionPlayerState(CheckPlayerState check, boolean required)
	{
		_check = check;
		_required = required;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		switch(_check)
		{
			case RESTING:
				if(env.character.isPlayer())
					return ((Player) env.character).isSitting() == _required;
				return !_required;
			case MOVING:
				return env.character.isMoving == _required;
			case RUNNING:
				return (env.character.isMoving && env.character.isRunning()) == _required;
			case STANDING:
				if(env.character.isPlayer())
					return ((Player) env.character).isSitting() != _required && env.character.isMoving != _required;
				return env.character.isMoving != _required;
			case FLYING:
				if(env.character.isPlayer())
					return env.character.isFlying() == _required;
				return !_required;
			case FLYING_TRANSFORM:
				if(env.character.isPlayer())
					return ((Player) env.character).isInFlyingTransform() == _required;
				return !_required;
		}
		return !_required;
	}
}
