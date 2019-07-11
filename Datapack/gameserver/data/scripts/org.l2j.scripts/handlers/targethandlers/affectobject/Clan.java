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

import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.targets.AffectObject;

/**
 * @author Nik
 */
public class Clan implements IAffectObjectHandler
{
	@Override
	public boolean checkAffectedObject(Creature activeChar, Creature target)
	{
		if (activeChar == target)
		{
			return true;
		}
		
		final Player player = activeChar.getActingPlayer();
		if (player != null)
		{
			final L2Clan clan = player.getClan();
			if (clan != null)
			{
				return clan == target.getClan();
			}
		}
		else if (activeChar.isNpc() && target.isNpc())
		{
			return ((L2Npc) activeChar).isInMyClan(((L2Npc) target));
		}
		
		return false;
	}
	
	@Override
	public Enum<AffectObject> getAffectObjectType()
	{
		return AffectObject.CLAN;
	}
}
