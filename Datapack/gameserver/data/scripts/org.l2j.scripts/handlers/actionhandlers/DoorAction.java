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
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.IActionHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.holders.DoorRequestHolder;
import org.l2j.gameserver.network.serverpackets.ConfirmDlg;

import static org.l2j.gameserver.network.SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE;
import static org.l2j.gameserver.network.SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius2D;

public class DoorAction implements IActionHandler
{
    @Override
    public boolean action(Player activeChar, WorldObject target, boolean interact)
    {
        // Check if the Player already target the Folk
        if (activeChar.getTarget() != target)
        {
            activeChar.setTarget(target);
        }
        else if (interact)
        {
            final Door door = (Door) target;
            final ClanHall clanHall = ClanHallManager.getInstance().getClanHallByDoorId(door.getId());
            // MyTargetSelected my = new MyTargetSelected(getObjectId(), activeChar.getLevel());
            // activeChar.sendPacket(my);
            if (target.isAutoAttackable(activeChar))
            {
                if (Math.abs(activeChar.getZ() - target.getZ()) < 400)
                {
                    activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
                }
            }
            else if ((activeChar.getClan() != null) && (clanHall != null) && (activeChar.getClanId() == clanHall.getOwnerId()))
            {
                if (!isInsideRadius2D(door, activeChar, Npc.INTERACTION_DISTANCE))
                {
                    activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, target);
                }
                else
                {
                    activeChar.addScript(new DoorRequestHolder(door));
                    if (!door.isOpen())
                    {
                        activeChar.sendPacket(new ConfirmDlg(WOULD_YOU_LIKE_TO_OPEN_THE_GATE));
                    }
                    else
                    {
                        activeChar.sendPacket(new ConfirmDlg(WOULD_YOU_LIKE_TO_CLOSE_THE_GATE));
                    }
                }
            }
        }
        return true;
    }

    @Override
    public InstanceType getInstanceType()
    {
        return InstanceType.L2DoorInstance;
    }
}
