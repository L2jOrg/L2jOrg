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

import java.util.HashSet;
import java.util.Set;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.Skill;

/**
 * Dispel By Slot Probability effect implementation.
 * @author Adry_85, Zoey76
 */
public final class DispelBySlotProbability extends AbstractEffect
{
	private final Set<AbnormalType> _dispelAbnormals;
	private final int _rate;
	
	public DispelBySlotProbability(StatsSet params)
	{
		final String[] dispelEffects = params.getString("dispel").split(";");
		_rate = params.getInt("rate", 100);
		_dispelAbnormals = new HashSet<>(dispelEffects.length);
		for (String slot : dispelEffects)
		{
			_dispelAbnormals.add(Enum.valueOf(AbnormalType.class, slot));
		}
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.DISPEL;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (effected == null)
		{
			return;
		}
		
		// The effectlist should already check if it has buff with this abnormal type or not.
		effected.getEffectList().stopEffects(info -> !info.getSkill().isIrreplacableBuff() && (Rnd.get(100) < _rate) && _dispelAbnormals.contains(info.getSkill().getAbnormalType()), true, true);
	}
}
