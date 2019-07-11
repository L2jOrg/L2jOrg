package handlers.actionhandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.xml.impl.ClanHallData;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.IActionHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.holders.DoorRequestHolder;
import org.l2j.gameserver.network.serverpackets.ConfirmDlg;

public class L2DoorInstanceAction implements IActionHandler
{
    @Override
    public boolean action(Player activeChar, WorldObject target, boolean interact)
    {
        // Check if the Player already target the L2NpcInstance
        if (activeChar.getTarget() != target)
        {
            activeChar.setTarget(target);
        }
        else if (interact)
        {
            final Door door = (Door) target;
            final ClanHall clanHall = ClanHallData.getInstance().getClanHallByDoorId(door.getId());
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
                if (!door.isInsideRadius2D(activeChar, L2Npc.INTERACTION_DISTANCE))
                {
                    activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, target);
                }
                else
                {
                    activeChar.addScript(new DoorRequestHolder(door));
                    if (!door.isOpen())
                    {
                        activeChar.sendPacket(new ConfirmDlg(1140));
                    }
                    else
                    {
                        activeChar.sendPacket(new ConfirmDlg(1141));
                    }
                }
            }
            else if ((activeChar.getClan() != null) && (((Door) target).getFort() != null) && (activeChar.getClan() == ((Door) target).getFort().getOwnerClan()) && ((Door) target).isOpenableBySkill() && !((Door) target).getFort().getSiege().isInProgress())
            {
                if (!((Creature) target).isInsideRadius2D(activeChar, L2Npc.INTERACTION_DISTANCE))
                {
                    activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, target);
                }
                else
                {
                    activeChar.addScript(new DoorRequestHolder((Door) target));
                    if (!((Door) target).isOpen())
                    {
                        activeChar.sendPacket(new ConfirmDlg(1140));
                    }
                    else
                    {
                        activeChar.sendPacket(new ConfirmDlg(1141));
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
