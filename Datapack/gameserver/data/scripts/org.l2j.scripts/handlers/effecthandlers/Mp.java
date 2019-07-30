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
package handlers.effecthandlers;

import org.l2j.gameserver.enums.StatModifierType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stats;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * MP change effect. It is mostly used for potions and static damage.
 * @author Nik
 */
public final class Mp extends AbstractEffect
{
	private final int _amount;
	private final StatModifierType _mode;

	public Mp(StatsSet params)
	{
		_amount = params.getInt("amount", 0);
		_mode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);
	}

	@Override
	public boolean isInstant()
	{
		return true;
	}

	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (effected.isDead() || isDoor(effected) || effected.isMpBlocked())
		{
			return;
		}

		int basicAmount = _amount;
		if ((item != null) && (item.isPotion() || item.isElixir()))
		{
			basicAmount += effected.getStat().getValue(Stats.ADDITIONAL_POTION_MP, 0);
		}

		double amount = 0;
		switch (_mode)
		{
			case DIFF:
			{
				amount = Math.min(basicAmount, effected.getMaxRecoverableMp() - effected.getCurrentMp());
				break;
			}
			case PER:
			{
				amount = Math.min((effected.getMaxMp() * basicAmount) / 100.0, effected.getMaxRecoverableMp() - effected.getCurrentMp());
				break;
			}
		}

		if (amount >= 0)
		{
			if (amount != 0)
			{
				final double newMp = amount + effected.getCurrentMp();
				effected.setCurrentMp(newMp, false);
				effected.broadcastStatusUpdate(effector);
			}

			SystemMessage sm;
			if (effector.getObjectId() != effected.getObjectId())
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.S2_MP_HAS_BEEN_RESTORED_BY_C1);
				sm.addString(effector.getName());
			}
			else
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.S1_MP_HAS_BEEN_RESTORED);
			}
			sm.addInt((int) amount);
			effected.sendPacket(sm);
		}
		else
		{
			final double damage = -amount;
			effected.reduceCurrentMp(damage);
		}
	}
}
