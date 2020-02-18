package handlers.conditions;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.conditions.ConditionFactory;
import org.l2j.gameserver.model.conditions.ICondition;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Sdw
 */
public class PlayerLevelCondition implements ICondition {

	private final int _minLevel;
	private final int _maxLevel;
	
	private PlayerLevelCondition(StatsSet params) {
		_minLevel = params.getInt("minLevel");
		_maxLevel = params.getInt("maxLevel");
	}
	
	@Override
	public boolean test(Creature creature, WorldObject object)
	{
		return isPlayer(creature) && (creature.getLevel() >= _minLevel) && (creature.getLevel() < _maxLevel);
	}

	public static class Factory implements ConditionFactory {

		@Override
		public ICondition create(StatsSet data) {
			return new PlayerLevelCondition(data);
		}

		@Override
		public String conditionName() {
			return "PlayerLevel";
		}
	}
}
