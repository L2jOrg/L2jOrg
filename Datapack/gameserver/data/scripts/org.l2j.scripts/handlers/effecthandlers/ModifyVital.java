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

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.item.instance.Item;

import static java.lang.Math.max;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Modify vital effect implementation.
 * @author malyelfik
 * @author JoeAlisson
 */
public final class ModifyVital extends AbstractEffect {

	private final int hp;
	private final int mp;
	private final int cp;
	
	private ModifyVital(StatsSet params) {
		hp = params.getInt("hp", 0);
		mp = params.getInt("mp", 0);
		cp = params.getInt("cp", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (effected.isDead()) {
			return;
		}

		if (isPlayer(effector) && isPlayer(effected) && effected.isAffected(EffectFlag.DUELIST_FURY) && !effector.isAffected(EffectFlag.DUELIST_FURY)) {
			return;
		}

		effected.setCurrentCp(max(cp, 0));
		effected.setCurrentHp(max(hp, 0));
		effected.setCurrentMp(max(mp, 0));
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new ModifyVital(data);
		}

		@Override
		public String effectName() {
			return "vital-modify";
		}
	}
}
