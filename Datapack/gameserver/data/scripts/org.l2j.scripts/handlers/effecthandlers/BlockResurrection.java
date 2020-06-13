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

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;

/**
 * Block Resurrection effect implementation.
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class BlockResurrection extends AbstractEffect {
	private BlockResurrection() {
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.BLOCK_RESURRECTION.getMask();
	}

	public static final class Factory implements SkillEffectFactory {
		private static final BlockResurrection INSTANCE = new BlockResurrection();
		@Override
		public AbstractEffect create(StatsSet data) {
			return null;
		}

		@Override
		public String effectName() {
			return "BlockResurrection";
		}
	}
}