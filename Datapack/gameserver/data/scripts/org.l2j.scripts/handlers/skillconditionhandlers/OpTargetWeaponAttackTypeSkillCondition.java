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

import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.Weapon;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;

/**
 * @author UnAfraid
 */
public class OpTargetWeaponAttackTypeSkillCondition implements ISkillCondition
{
	private final List<WeaponType> _weaponTypes = new ArrayList<>();
	
	public OpTargetWeaponAttackTypeSkillCondition(StatsSet params)
	{
		final List<String> weaponTypes = params.getList("weaponType", String.class);
		if (weaponTypes != null)
		{
			weaponTypes.stream().map(WeaponType::valueOf).forEach(_weaponTypes::add);
		}
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		if ((target == null) || !target.isCharacter())
		{
			return false;
		}
		final Creature targetCreature = (Creature) target;
		final Weapon weapon = targetCreature.getActiveWeaponItem();
		return _weaponTypes.stream().anyMatch(weaponType -> weaponType == weapon.getItemType());
	}
}
