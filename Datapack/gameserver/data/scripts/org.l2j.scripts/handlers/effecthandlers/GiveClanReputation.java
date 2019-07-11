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

import org.l2j.gameserver.model.L2ClanMember;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * Give Clan reputation effect implementation.
 * @author Mobius
 */
public final class GiveClanReputation extends AbstractEffect
{
	private final int _reputation;
	
	public GiveClanReputation(StatsSet params)
	{
		_reputation = params.getInt("reputation", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, L2ItemInstance item)
	{
		if (!effector.isPlayer() || !effected.isPlayer() || effected.isAlikeDead() || (effector.getActingPlayer().getClan() == null))
		{
			return;
		}
		
		effector.getActingPlayer().getClan().addReputationScore(_reputation, true);
		
		for (L2ClanMember member : effector.getActingPlayer().getClan().getMembers())
		{
			if (member.isOnline())
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_CLAN_HAS_ADDED_S1_POINT_S_TO_ITS_CLAN_REPUTATION);
				sm.addInt(_reputation);
				member.getPlayerInstance().sendPacket(sm);
			}
		}
	}
}