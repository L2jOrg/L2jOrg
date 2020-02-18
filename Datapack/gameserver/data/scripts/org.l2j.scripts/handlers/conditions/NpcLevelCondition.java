package handlers.conditions;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.conditions.ConditionFactory;
import org.l2j.gameserver.model.conditions.ICondition;

import static org.l2j.gameserver.util.GameUtils.isNpc;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class NpcLevelCondition implements ICondition {
	private final int _minLevel;
	private final int _maxLevel;
	
	private NpcLevelCondition(StatsSet params) {
		_minLevel = params.getInt("minLevel");
		_maxLevel = params.getInt("maxLevel");
	}
	
	@Override
	public boolean test(Creature creature, WorldObject object)
	{
		return isNpc(object) && (((Creature) object).getLevel() >= _minLevel) && (((Creature) object).getLevel() < _maxLevel);
	}

	public static class Factory implements ConditionFactory {

		@Override
		public ICondition create(StatsSet data) {
			return new NpcLevelCondition(data);
		}

		@Override
		public String conditionName() {
			return "NpcLevel";
		}
	}
	
}
