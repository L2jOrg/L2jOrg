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

import org.l2j.commons.xml.XmlParser;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.conditions.ConditionFactory;
import org.l2j.gameserver.engine.item.drop.ExtendDropCondition;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public record PlayerLevelCondition(int minLevel, int maxLevel) implements ExtendDropCondition {

	@Override
	public boolean test(Creature creature, WorldObject object) {
		return isPlayer(creature) && creature.getLevel() >= minLevel && creature.getLevel() < maxLevel;
	}

	public static class Factory extends XmlParser implements ConditionFactory {

		@Override
		public ExtendDropCondition create(Node data) {
			var attr = data.getAttributes();
			var minLevel = parseInt(attr, "min-level");
			var maxLevel = parseInt(attr, "max-level");
			return new PlayerLevelCondition(minLevel, maxLevel);
		}

		@Override
		public String conditionName() {
			return "player-level";
		}
	}
}
