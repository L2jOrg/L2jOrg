/*
 * Copyright © 2019-2020 L2JOrg
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
package handlers.targethandlers.affectscope;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.world.World;

import java.util.function.Consumer;

/**
 * Square affect scope implementation (actually more like a rectangle).
 * @author Nik
 */
public class Square extends SquarePB {

	@Override
	public void forEachAffected(Creature activeChar, WorldObject target, Skill skill, Consumer<? super WorldObject> action) {
		final int squareLength = skill.getFanRadius();
		final int squareWidth = skill.getFanAngle();
		final int radius = (int) Math.sqrt((squareLength * squareLength) + (squareWidth * squareWidth));
		var filter = squareFilterOf(activeChar, skill, squareLength, squareWidth);

		World.getInstance().forEachVisibleObjectInRange(activeChar, Creature.class, radius, action::accept, filter);

		// Add object of origin since its skipped in the forEachVisibleObjectInRange method.
		if (filter.test(activeChar)) {
			action.accept(activeChar);
		}

	}
	
	@Override
	public Enum<AffectScope> getAffectScopeType()
	{
		return AffectScope.SQUARE;
	}
}
