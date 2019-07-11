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

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.L2Party;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.serverpackets.FlyToLocation;
import org.l2j.gameserver.network.serverpackets.FlyToLocation.FlyType;
import org.l2j.gameserver.network.serverpackets.ValidateLocation;

/**
 * Teleport player/party to summoned npc effect implementation.
 * @author Nik
 */
public final class TeleportToNpc extends AbstractEffect
{
	private final int _npcId;
	private final boolean _party;
	
	public TeleportToNpc(StatsSet params)
	{
		_npcId = params.getInt("npcId");
		_party = params.getBoolean("party", false);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.TELEPORT_TO_TARGET;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		final ILocational teleLocation = effector.getSummonedNpcs().stream().filter(npc -> npc.getId() == _npcId).findAny().orElse(null);
		if (teleLocation != null)
		{
			final L2Party party = effected.getParty();
			if (_party && (party != null))
			{
				party.getMembers().forEach(p -> teleport(p, teleLocation));
			}
			else
			{
				teleport(effected, teleLocation);
			}
		}
	}
	
	private void teleport(Creature effected, ILocational location)
	{
		if (effected.isInsideRadius2D(location, 900))
		{
			effected.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			effected.broadcastPacket(new FlyToLocation(effected, location, FlyType.DUMMY));
			effected.abortAttack();
			effected.abortCast();
			effected.setXYZ(location);
			effected.broadcastPacket(new ValidateLocation(effected));
			effected.revalidateZone(true);
		}
		else
		{
			effected.teleToLocation(location);
		}
	}
}
