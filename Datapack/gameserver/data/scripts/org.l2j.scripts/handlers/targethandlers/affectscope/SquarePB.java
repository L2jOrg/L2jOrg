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

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.handler.AffectObjectHandler;
import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.handler.IAffectScopeHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.world.World;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.l2j.gameserver.util.MathUtil.convertHeadingToDegree;

/**
 * Square point blank affect scope implementation (actually more like a rectangle). Gathers objects around yourself except target itself.
 * @author Nik
 * @author JoeAlisson
 */
public class SquarePB implements IAffectScopeHandler {

	@Override
	public void forEachAffected(Creature activeChar, WorldObject target, Skill skill, Consumer<? super WorldObject> action) {
		final int squareLength = skill.getFanRadius();
		final int squareWidth = skill.getFanAngle();
		final int radius = (int) Math.sqrt(squareLength * squareLength + squareWidth * squareWidth);

		World.getInstance().forEachVisibleObjectInRange(activeChar, Creature.class, radius, action::accept, squareFilterOf(activeChar, skill, squareLength, squareWidth));
	}

	protected Predicate<Creature> squareFilterOf(Creature activeChar, Skill skill, int squareLength, int squareWidth) {
		final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler(skill.getAffectObject());
		final int squareStartAngle = skill.getFanStartAngle();
		final int affectLimit = skill.getAffectLimit();

		final int rectX = activeChar.getX();
		final int rectY = activeChar.getY() - (squareWidth / 2);
		final double heading = Math.toRadians(squareStartAngle + convertHeadingToDegree(activeChar.getHeading()));
		final double cos = Math.cos(-heading);
		final double sin = Math.sin(-heading);

		final AtomicInteger affected = new AtomicInteger(0);
		return creature -> {
			if (creature.isDead() || ((affectLimit > 0) && (affected.get() >= affectLimit))) {
				return false;
			}

			// Check if inside square.
			final int xp = creature.getX() - activeChar.getX();
			final int yp = creature.getY() - activeChar.getY();
			final int xr = (int) ((activeChar.getX() + (xp * cos)) - (yp * sin));
			final int yr = (int) (activeChar.getY() + (xp * sin) + (yp * cos));

			if ((xr > rectX) && (xr < (rectX + squareLength)) && (yr > rectY) && (yr < (rectY + squareWidth))) {
				if ((affectObject != null) && !affectObject.checkAffectedObject(activeChar, creature)) {
					return false;
				}
				if (!GeoEngine.getInstance().canSeeTarget(activeChar, creature)) {
					return false;
				}
				affected.incrementAndGet();
				return true;
			}
			return false;
		};
	}

	@Override
	public Enum<AffectScope> getAffectScopeType()
	{
		return AffectScope.SQUARE_PB;
	}
}
