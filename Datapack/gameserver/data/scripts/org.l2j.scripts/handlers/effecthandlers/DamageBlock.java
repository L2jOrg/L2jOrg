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
 * Effect that blocks damage and heals to HP/MP. <BR>
 * Regeneration or DOT shouldn't be blocked, Vampiric Rage and Balance Life as well.
 * @author Nik
 * @author JoeAlisson
 */
public final class DamageBlock extends AbstractEffect {
	private final boolean blockHp;
	private final boolean blockMp;
	
	private DamageBlock(StatsSet params) {
		blockHp = params.getBoolean("block-hp");
		blockMp = params.getBoolean("block-mp");
	}
	
	@Override
	public long getEffectFlags() {
		int mask =0;
		if(blockHp) {
			mask |= EffectFlag.HP_BLOCK.getMask();
		}
		if(blockMp) {
			mask |= EffectFlag.MP_BLOCK.getMask();
		}
		return mask;
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new DamageBlock(data);
		}

		@Override
		public String effectName() {
			return "damage-block";
		}
	}
}
