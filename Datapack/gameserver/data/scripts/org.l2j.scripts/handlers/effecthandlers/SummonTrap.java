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

import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Trap;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;

/**
 * Summon Trap effect implementation.
 * @author Zoey76
 */
public final class SummonTrap extends AbstractEffect
{
	private final int _despawnTime;
	private final int _npcId;
	
	public SummonTrap(StatsSet params)
	{
		_despawnTime = params.getInt("despawnTime", 0);
		_npcId = params.getInt("npcId", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (!effected.isPlayer() || effected.isAlikeDead() || effected.getActingPlayer().inObserverMode())
		{
			return;
		}
		
		if (_npcId <= 0)
		{
			LOGGER.warn(SummonTrap.class.getSimpleName() + ": Invalid NPC ID:" + _npcId + " in skill ID: " + skill.getId());
			return;
		}
		
		final Player player = effected.getActingPlayer();
		if (player.inObserverMode() || player.isMounted())
		{
			return;
		}
		
		// Unsummon previous trap
		if (player.getTrap() != null)
		{
			player.getTrap().unSummon();
		}
		
		final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(_npcId);
		if (npcTemplate == null)
		{
			LOGGER.warn(SummonTrap.class.getSimpleName() + ": Spawn of the non-existing Trap ID: " + _npcId + " in skill ID:" + skill.getId());
			return;
		}
		
		final Trap trap = new Trap(npcTemplate, player, _despawnTime);
		trap.setCurrentHp(trap.getMaxHp());
		trap.setCurrentMp(trap.getMaxMp());
		trap.setIsInvul(true);
		trap.setHeading(player.getHeading());
		trap.spawnMe(player.getX(), player.getY(), player.getZ());
		player.addSummonedNpc(trap); // player.setTrap(trap);
	}
}
