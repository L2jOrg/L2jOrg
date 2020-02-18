package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.StatsSet;

public interface ConditionFactory {

    ICondition create(StatsSet data);

    String conditionName();

}
