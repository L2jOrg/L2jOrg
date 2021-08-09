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
package org.l2j.scripts.ai.areas.cruma;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.scripts.ai.AbstractNpcAI;

import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

/**
 * Summon Pc AI.<br>
 * Summon the player to the NPC on attack.
 * @author Zoey76
 */
public final class SummonPc extends AbstractNpcAI
{
	private static final int PORTA = 20213;
	private static final int PERUM = 20221;
	private static final SkillHolder SUMMON_PC = new SkillHolder(4161, 1);
	
	private SummonPc()
	{
		addAttackId(PORTA, PERUM);
		addSpellFinishedId(PORTA, PERUM);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		final int chance = Rnd.get(100);
		final boolean attacked = npc.getVariables().getBoolean("attacked", false);
		if (!attacked && !isInsideRadius3D(npc,  attacker,  300)) {
			onDistantAttack(npc, attacker);
		}
		else if (!attacked && !isInsideRadius3D(npc,  attacker, 100)) {
			onCloseAttack(npc, attacker, chance);
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}

	private void onCloseAttack(Npc npc, Player attacker, int chance) {
		final Attackable monster = (Attackable) npc;
		if (monster.getMostHated() != null) {
			if (((monster.getMostHated() == attacker) && (chance < 50)) || (chance < 10))
			{
				castSummonPc(npc, attacker, true);
			}
		}
	}

	private void onDistantAttack(Npc npc, Player attacker) {
		if (Rnd.chance(50)) {
			castSummonPc(npc, attacker, false);
			castSummonPc(npc, attacker, true);
		}
	}

	private void castSummonPc(Npc npc, Player attacker, boolean attacked) {
		if ((SUMMON_PC.getSkill().getMpConsume() < npc.getCurrentMp()) && (SUMMON_PC.getSkill().getHpConsume() < npc.getCurrentHp()) && !npc.isSkillDisabled(SUMMON_PC.getSkill())) {
			npc.setTarget(attacker);
			npc.doCast(SUMMON_PC.getSkill());

			if(attacked) {
				npc.getVariables().set("attacked", true);
			}
		}
	}

	@Override
	public String onSpellFinished(Npc npc, Player player, Skill skill)
	{
		if ((skill.getId() == SUMMON_PC.getSkillId()) && !npc.isDead() && npc.getVariables().getBoolean("attacked", false))
		{
			player.teleToLocation(npc);
			npc.getVariables().set("attacked", false);
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	public static AbstractNpcAI provider()
	{
		return new SummonPc();
	}
}
