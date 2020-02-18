package handlers.conditions;

import java.util.List;

import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.conditions.ConditionFactory;
import org.l2j.gameserver.model.conditions.ICondition;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class CategoryTypeCondition implements ICondition {

	private final List<CategoryType> _categoryTypes;
	
	private CategoryTypeCondition(StatsSet params)
	{
		_categoryTypes = params.getEnumList("category", CategoryType.class);
	}
	
	@Override
	public boolean test(Creature creature, WorldObject target)
	{
		return _categoryTypes.stream().anyMatch(creature::isInCategory);
	}

	public static class Factory implements ConditionFactory {

		@Override
		public ICondition create(StatsSet data) {
			return new CategoryTypeCondition(data);
		}

		@Override
		public String conditionName() {
			return "CategoryType";
		}
	}
}
