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
package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.effects.AbstractEffect;

import static org.l2j.gameserver.util.GameUtils.isNpc;
import static org.l2j.gameserver.util.GameUtils.isSummon;

/**
 * Consume Body effect implementation.
 * @author Mobius
 * @author JoeAlisson
 */
public final class ConsumeBody extends AbstractEffect {
	private ConsumeBody() {
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (!effected.isDead() || (!isNpc(effected) && !isSummon(effected)) || (isSummon(effected) && (effector != effected.getActingPlayer()))) {
			return;
		}
		
		if (isNpc(effected)) {
			((Npc) effected).endDecayTask();
		}
	}

	public static class Factory implements SkillEffectFactory {
		private static final ConsumeBody INSTANCE = new ConsumeBody();

		@Override
		public AbstractEffect create(StatsSet data) {
			return INSTANCE;
		}

		@Override
		public String effectName() {
			return "ConsumeBody";
		}
	}
}
