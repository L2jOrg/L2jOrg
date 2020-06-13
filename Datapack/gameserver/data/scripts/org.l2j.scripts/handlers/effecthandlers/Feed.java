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
package handlers.effecthandlers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.instance.Item;

import static org.l2j.gameserver.util.GameUtils.isPet;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Sdw
 */
public class Feed extends AbstractEffect {
	private final int normal;
	private final int ride;
	private final int wyvern;
	
	private Feed(StatsSet params) {
		normal = params.getInt("normal", 0);
		ride = params.getInt("ride", 0);
		wyvern = params.getInt("wyvern", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (isPet(effected)) {
			final Pet pet = (Pet) effected;
			pet.setCurrentFed(pet.getCurrentFed() + (normal * Config.PET_FOOD_RATE));
		} else if (isPlayer(effected)) {
			final Player player = effected.getActingPlayer();
			if (player.getMountType() == MountType.WYVERN) {
				player.setCurrentFeed(player.getCurrentFeed() + wyvern);
			} else {
				player.setCurrentFeed(player.getCurrentFeed() + ride);
			}
		}
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new Feed(data);
		}

		@Override
		public String effectName() {
			return "feed";
		}
	}
}
