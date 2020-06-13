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
package handlers.actionshifthandlers;

import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.IActionShiftHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.network.serverpackets.StaticObject;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.HtmlUtil;

/**
 * This class manage shift + click on {@link Door}.
 * @author St3eT
 */
public class DoorActionShift implements IActionShiftHandler
{
	@Override
	public boolean action(Player activeChar, WorldObject target, boolean interact)
	{
		if (activeChar.isGM())
		{
			activeChar.setTarget(target);
			final Door door = (Door) target;
			final ClanHall clanHall = ClanHallManager.getInstance().getClanHallByDoorId(door.getId());
			final Castle castle = door.getCastle();
			activeChar.sendPacket(new StaticObject(door, activeChar.isGM()));
			final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
			html.setFile(activeChar, "data/html/admin/doorinfo.htm");
			// Hp / MP
			html.replace("%hpGauge%", HtmlUtil.getHpGauge(250, (long) door.getCurrentHp(), door.getMaxHp(), false));
			html.replace("%mpGauge%", HtmlUtil.getMpGauge(250, (long) door.getCurrentMp(), door.getMaxMp(), false));
			// Basic info
			html.replace("%doorName%", door.getName());
			html.replace("%objId%", String.valueOf(door.getObjectId()));
			html.replace("%doorId%", String.valueOf(door.getId()));
			// Position info
			html.replace("%position%", door.getX() + ", " + door.getY() + ", " + door.getZ());
			html.replace("%node1%", door.getX(0) + ", " + door.getY(0) + ", " + door.getZMin());
			html.replace("%node2%", door.getX(1) + ", " + door.getY(1) + ", " + door.getZMin());
			html.replace("%node3%", door.getX(2) + ", " + door.getY(2) + ", " + door.getZMax());
			html.replace("%node4%", door.getX(3) + ", " + door.getY(3) + ", " + door.getZMax());
			// Residence info
			html.replace("%clanHall%", clanHall != null ? clanHall.getName() : "None");
			html.replace("%castle%", castle != null ? castle.getName() + " Castle" : "None");
			activeChar.sendPacket(html);
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2DoorInstance;
	}
}