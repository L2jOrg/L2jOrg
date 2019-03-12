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
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stats;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Sdw, Mobius
 */
public class GetMomentum extends AbstractEffect
{
	private static int _ticks;
	
	public GetMomentum(StatsSet params)
	{
		_ticks = params.getInt("ticks", 0);
	}
	
	@Override
	public int getTicks()
	{
		return _ticks;
	}
	
	@Override
	public boolean onActionTime(L2Character effector, L2Character effected, Skill skill)
	{
		if (effected.isPlayer())
		{
			final L2PcInstance player = effected.getActingPlayer();
			final int maxCharge = (int) player.getStat().getValue(Stats.MAX_MOMENTUM, 0);
			final int newCharge = Math.min(player.getCharges() + 1, maxCharge);
			
			player.setCharges(newCharge);
			
			if (newCharge == maxCharge)
			{
				player.sendPacket(SystemMessageId.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY);
			}
			else
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_FORCE_HAS_INCREASED_TO_LEVEL_S1);
				sm.addInt(newCharge);
				player.sendPacket(sm);
			}
			
			player.sendPacket(new EtcStatusUpdate(player));
		}
		
		return skill.isToggle();
	}
}
