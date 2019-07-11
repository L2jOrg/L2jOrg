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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.Skill;

/**
 * Dispel By Slot effect implementation.
 * @author Gnacik, Zoey76, Adry_85
 */
public final class DispelBySlotMyself extends AbstractEffect
{
	private final Set<AbnormalType> _dispelAbnormals;
	
	public DispelBySlotMyself(StatsSet params)
	{
		String dispel = params.getString("dispel");
		if ((dispel != null) && !dispel.isEmpty())
		{
			_dispelAbnormals = new HashSet<>();
			for (String slot : dispel.split(";"))
			{
				_dispelAbnormals.add(AbnormalType.getAbnormalType(slot));
			}
		}
		else
		{
			_dispelAbnormals = Collections.<AbnormalType> emptySet();
		}
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.DISPEL_BY_SLOT;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (_dispelAbnormals.isEmpty())
		{
			return;
		}
		
		// The effectlist should already check if it has buff with this abnormal type or not.
		effected.getEffectList().stopEffects(info -> !info.getSkill().isIrreplacableBuff() && _dispelAbnormals.contains(info.getSkill().getAbnormalType()), true, true);
	}
}
