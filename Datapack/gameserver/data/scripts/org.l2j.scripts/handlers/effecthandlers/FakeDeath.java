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

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ChangeWaitType;
import org.l2j.gameserver.network.serverpackets.Revive;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Fake Death effect implementation.
 * @author mkizub
 */
public final class FakeDeath extends AbstractEffect
{
	private final double _power;
	
	public FakeDeath(StatsSet params)
	{
		_power = params.getDouble("power", 0);
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.FAKE_DEATH.getMask();
	}
	
	@Override
	public boolean onActionTime(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (effected.isDead())
		{
			return false;
		}
		
		final double manaDam = _power * getTicksMultiplier();
		if (manaDam > effected.getCurrentMp())
		{
			if (skill.isToggle())
			{
				effected.sendPacket(SystemMessageId.YOUR_SKILL_WAS_DEACTIVATED_DUE_TO_LACK_OF_MP);
				return false;
			}
		}
		
		effected.reduceCurrentMp(manaDam);
		
		return skill.isToggle();
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		if (isPlayer(effected))
		{
			effected.getActingPlayer().setRecentFakeDeath(true);
		}
		
		effected.broadcastPacket(new ChangeWaitType(effected, ChangeWaitType.WT_STOP_FAKEDEATH));
		effected.broadcastPacket(new Revive(effected));
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		effected.startFakeDeath();
	}
}
