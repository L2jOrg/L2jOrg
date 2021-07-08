/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.scripts.handlers.conditions;

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
