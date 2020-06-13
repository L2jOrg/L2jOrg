/*
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
package handlers.actionhandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.IActionHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;

import static org.l2j.gameserver.util.MathUtil.isInsideRadius2D;

public class StaticWorldObjectAction implements IActionHandler
{
	@Override
	public boolean action(Player activeChar, WorldObject target, boolean interact)
	{
		final StaticWorldObject staticObject = (StaticWorldObject) target;
		if (staticObject.getType() < 0)
		{
			LOGGER.info("StaticWorldObject: StaticObject with invalid type! StaticObjectId: " + staticObject.getId());
		}
		
		// Check if the Player already target the Folk
		if (activeChar.getTarget() != staticObject)
		{
			// Set the target of the Player activeChar
			activeChar.setTarget(staticObject);
		}
		else if (interact)
		{
			// Calculate the distance between the Player and the Folk
			if (!isInsideRadius2D(activeChar, staticObject, Npc.INTERACTION_DISTANCE))
			{
				// Notify the Player AI with AI_INTENTION_INTERACT
				activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, staticObject);
			}
			else if (staticObject.getType() == 2)
			{
				final String filename = (staticObject.getId() == 24230101) ? "data/html/signboards/tomb_of_crystalgolem.htm" : "data/html/signboards/pvp_signboard.htm";
				final String content = HtmCache.getInstance().getHtm(activeChar, filename);
				final NpcHtmlMessage html = new NpcHtmlMessage(staticObject.getObjectId());
				
				if (content == null)
				{
					html.setHtml("<html><body>Signboard is missing:<br>" + filename + "</body></html>");
				}
				else
				{
					html.setHtml(content);
				}
				
				activeChar.sendPacket(html);
			}
			else if (staticObject.getType() == 0)
			{
				activeChar.sendPacket(staticObject.getMap());
			}
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2StaticObjectInstance;
	}
}
