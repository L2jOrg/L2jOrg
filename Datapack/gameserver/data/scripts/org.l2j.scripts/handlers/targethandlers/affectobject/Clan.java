/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.targethandlers.affectobject;

import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.targets.AffectObject;

import static org.l2j.gameserver.util.GameUtils.isNpc;

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
			final org.l2j.gameserver.model.Clan clan = player.getClan();
			if (clan != null)
			{
				return clan == target.getClan();
			}
		}
		else if (isNpc(activeChar) && isNpc(target))
		{
			return ((Npc) activeChar).isInMyClan(((Npc) target));
		}
		
		return false;
	}
	
	@Override
	public Enum<AffectObject> getAffectObjectType()
	{
		return AffectObject.CLAN;
	}
}
