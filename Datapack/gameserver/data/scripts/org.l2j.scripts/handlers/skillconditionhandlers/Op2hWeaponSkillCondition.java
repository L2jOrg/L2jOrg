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

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.items.L2Weapon;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;

/**
 * @author UnAfraid
 */
public class Op2hWeaponSkillCondition implements ISkillCondition
{
	private final List<WeaponType> _weaponTypes = new ArrayList<>();
	
	public Op2hWeaponSkillCondition(StatsSet params)
	{
		final List<String> weaponTypes = params.getList("weaponType", String.class);
		if (weaponTypes != null)
		{
			weaponTypes.stream().map(WeaponType::valueOf).forEach(_weaponTypes::add);
		}
	}
	
	@Override
	public boolean canUse(L2Character caster, Skill skill, L2Object target)
	{
		final L2Weapon weapon = caster.getActiveWeaponItem();
		if (weapon == null)
		{
			return false;
		}
		return _weaponTypes.stream().anyMatch(weaponType -> (weapon.getItemType() == weaponType) && ((weapon.getBodyPart() & L2Item.SLOT_LR_HAND) != 0));
	}
}
