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
package handlers.targethandlers.affectobject;

import com.l2jmobius.gameserver.handler.IAffectObjectHandler;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.skills.targets.AffectObject;

/**
 * Undead enemy npc affect object implementation.
 * @author Nik
 */
public class UndeadRealEnemy implements IAffectObjectHandler
{
	@Override
	public boolean checkAffectedObject(L2Character activeChar, L2Character target)
	{
		// You are not an enemy of yourself.
		if (activeChar == target)
		{
			return false;
		}
		
		return target.isUndead() && target.isAutoAttackable(activeChar);
	}
	
	@Override
	public Enum<AffectObject> getAffectObjectType()
	{
		return AffectObject.UNDEAD_REAL_ENEMY;
	}
}
