/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.skillconditionhandlers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.l2j.gameserver.util.GameUtils.isAttackable;

/**
 * @author Sdw
 */
public class OpSweeperSkillCondition implements ISkillCondition
{
	public OpSweeperSkillCondition(StatsSet params)
	{
		
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		final AtomicBoolean canSweep = new AtomicBoolean(false);
		if (caster.getActingPlayer() != null)
		{
			final Player sweeper = caster.getActingPlayer();
			if (skill != null)
			{
				skill.forEachTargetAffected(sweeper, target, o ->
				{
					if (isAttackable(o))
					{
						final Attackable a = (Attackable) o;
						if (a.isDead())
						{
							if (a.isSpoiled())
							{
								canSweep.set(a.checkSpoilOwner(sweeper, true));
								if (canSweep.get())
								{
									canSweep.set(!a.isOldCorpse(sweeper, Config.CORPSE_CONSUME_SKILL_ALLOWED_TIME_BEFORE_DECAY, true));
								}
								if (canSweep.get())
								{
									canSweep.set(sweeper.getInventory().checkInventorySlotsAndWeight(a.getSpoilLootItems(), true, true));
								}
							}
							else
							{
								sweeper.sendPacket(SystemMessageId.SWEEPER_FAILED_TARGET_NOT_SPOILED);
							}
						}
					}
				});
			}
		}
		return canSweep.get();
	}
}
