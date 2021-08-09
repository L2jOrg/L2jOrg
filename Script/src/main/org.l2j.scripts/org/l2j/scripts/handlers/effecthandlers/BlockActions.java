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

import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.util.StreamUtil;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;

import java.util.Arrays;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayable;
import static org.l2j.gameserver.util.GameUtils.isSummon;

/**
 * Block Actions effect implementation.
 * @author mkizub
 * @author JoeAlisson
 */
public final class BlockActions extends AbstractEffect {

	private final IntSet allowedSkills;
	
	private BlockActions(StatsSet params) {
		final String[] allowedSkills = params.getString("allowed-skills", "").split(" ");
		this.allowedSkills = StreamUtil.collectToSet(Arrays.stream(allowedSkills).filter(Util::isInteger).mapToInt(Integer::parseInt));
	}
	
	@Override
	public long getEffectFlags()
	{
		return allowedSkills.isEmpty() ? EffectFlag.BLOCK_ACTIONS.getMask() : EffectFlag.CONDITIONAL_BLOCK_ACTIONS.getMask();
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.BLOCK_ACTIONS;
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		allowedSkills.stream().forEach(effected::addBlockActionsAllowedSkill);
		effected.startParalyze();
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill) {
		allowedSkills.forEach(effected::removeBlockActionsAllowedSkill);
		if (isPlayable(effected)) {
			if (isSummon(effected)) {
				if (nonNull(effector) && !effector.isDead()) {
					((Summon) effected).doAttack(effector);
				} else {
					effected.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, effected.getActingPlayer());
				}
			} else {
				effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			}
		} else {
			effected.getAI().notifyEvent(CtrlEvent.EVT_THINK);
		}
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new BlockActions(data);
		}

		@Override
		public String effectName() {
			return "block-all-actions";
		}
	}

}
