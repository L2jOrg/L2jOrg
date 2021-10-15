/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.scripts.handlers.targethandlers.affectscope;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.handler.AffectObjectHandler;
import org.l2j.gameserver.handler.IAffectScopeHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.world.World;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Nik
 * @author JoeAlisson
 */
public class Pledge implements IAffectScopeHandler {

	@Override
	public void forEachAffected(Creature creature, WorldObject target, Skill skill, Consumer<? super WorldObject> action) {

		if (target instanceof Playable playable) {
			var affectObject = AffectObjectHandler.getInstance().getHandler(skill.getAffectObject());

			var targetPlayer = playable.getActingPlayer();

			Predicate<Playable> filter = p -> {
				
				var player = p.getActingPlayer();
				if (player == null || !isAffected(targetPlayer, player)) {
					return false;
				}
				if (player.getClanId() == 0 || player.getClanId() != targetPlayer.getClanId()) {
					return false;
				}
				return affectObject == null || affectObject.checkAffectedObject(creature, p);
			};

			World.getInstance().forVisibleObjectsInRange(playable, Playable.class, skill.getAffectRange(), skill.getAffectLimit(), true, filter, action);
		}
	}

	protected boolean isAffected(Player initialTarget, Player player) {
		return !player.isDead();
	}

	@Override
	public Enum<AffectScope> getAffectScopeType()
	{
		return AffectScope.DEAD_PLEDGE;
	}
}
