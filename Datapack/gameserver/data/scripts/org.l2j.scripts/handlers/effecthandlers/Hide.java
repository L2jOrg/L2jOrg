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

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.world.World;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Hide effect implementation.
 * @author ZaKaX, nBd
 * @author JoeAlisson
 */
public final class Hide extends AbstractEffect {

	private Hide() {
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		if (isPlayer(effected)) {
			effected.setInvisible(true);

			if ((effected.getAI().getNextIntention() != null) && (effected.getAI().getNextIntention().getCtrlIntention() == CtrlIntention.AI_INTENTION_ATTACK)) {
				effected.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			}

			World.getInstance().forEachVisibleObject(effected, Creature.class, target -> {
				target.setTarget(null);
				target.abortAttack();
				target.abortCast();
				target.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			}, target -> target.getTarget() == effected);
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill) {
		if (isPlayer(effected)) {
			final Player activeChar = effected.getActingPlayer();
			if (!activeChar.inObserverMode()) {
				activeChar.setInvisible(false);
			}
		}
	}

	public static class Factory implements SkillEffectFactory {
		private static final Hide INSTANCE = new Hide();
		@Override
		public AbstractEffect create(StatsSet data) {
			return INSTANCE;
		}

		@Override
		public String effectName() {
			return "Hide";
		}
	}
}