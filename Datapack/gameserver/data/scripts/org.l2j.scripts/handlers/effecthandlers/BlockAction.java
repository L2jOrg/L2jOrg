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

import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.util.StreamUtil;
import org.l2j.gameserver.datatables.ReportTable;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;

import java.util.Arrays;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Block Action effect implementation.
 * @author BiggBoss
 * @author JoeAlisson
 */
public final class BlockAction extends AbstractEffect {

	private IntSet blockedActions;
	
	private BlockAction(StatsSet params) {
		blockedActions = StreamUtil.collectToSet(Arrays.stream(params.getString("actions").split(" ")).mapToInt(Integer::parseInt));
	}
	
	@Override
	public boolean canStart(Creature effector, Creature effected, Skill skill)
	{
		return isPlayer(effected);
	}
	
	@Override
	public boolean checkCondition(Object id) {
		return !(id instanceof Integer) || !blockedActions.contains((int) id);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		if (blockedActions.contains(ReportTable.PARTY_ACTION_BLOCK_ID)) {
			PunishmentManager.getInstance().startPunishment(new PunishmentTask(0, effected.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.PARTY_BAN, 0, "block action debuff", "system", true));
		}
		
		if (blockedActions.contains(ReportTable.CHAT_BLOCK_ID)) {
			PunishmentManager.getInstance().startPunishment(new PunishmentTask(0, effected.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN, 0, "block action debuff", "system", true));
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill) {
		if (blockedActions.contains(ReportTable.PARTY_ACTION_BLOCK_ID)) {
			PunishmentManager.getInstance().stopPunishment(effected.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.PARTY_BAN);
		}

		if (blockedActions.contains(ReportTable.CHAT_BLOCK_ID)) {
			PunishmentManager.getInstance().stopPunishment(effected.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN);
		}
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new BlockAction(data);
		}

		@Override
		public String effectName() {
			return "block-action";
		}
	}
}
