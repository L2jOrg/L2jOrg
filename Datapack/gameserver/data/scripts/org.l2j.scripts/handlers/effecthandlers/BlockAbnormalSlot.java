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
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.Skill;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Block Buff Slot effect implementation.
 * @author Zoey76
 */
public final class BlockAbnormalSlot extends AbstractEffect
{
	private final Set<AbnormalType> _blockAbnormalSlots;
	
	public BlockAbnormalSlot(StatsSet params)
	{
		_blockAbnormalSlots = Arrays.stream(params.getString("slot").split(";")).map(slot -> Enum.valueOf(AbnormalType.class, slot)).collect(Collectors.toSet());
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		effected.getEffectList().addBlockedAbnormalTypes(_blockAbnormalSlots);
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.getEffectList().removeBlockedAbnormalTypes(_blockAbnormalSlots);
	}
}
