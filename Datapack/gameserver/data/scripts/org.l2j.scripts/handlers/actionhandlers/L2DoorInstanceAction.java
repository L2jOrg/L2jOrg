package handlers.actionhandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.xml.impl.ClanHallData;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.IActionHandler;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2DoorInstance;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.holders.DoorRequestHolder;
import org.l2j.gameserver.network.serverpackets.ConfirmDlg;

public class L2DoorInstanceAction implements IActionHandler
{
    @Override
    public boolean action(Player activeChar, L2Object target, boolean interact)
    {
        // Check if the Player already target the L2NpcInstance
        if (activeChar.getTarget() != target)
        {
            activeChar.setTarget(target);
        }
        else if (interact)
        {
            final L2DoorInstance door = (L2DoorInstance) target;
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
            else if ((activeChar.getClan() != null) && (((L2DoorInstance) target).getFort() != null) && (activeChar.getClan() == ((L2DoorInstance) target).getFort().getOwnerClan()) && ((L2DoorInstance) target).isOpenableBySkill() && !((L2DoorInstance) target).getFort().getSiege().isInProgress())
            {
                if (!((L2Character) target).isInsideRadius2D(activeChar, L2Npc.INTERACTION_DISTANCE))
                {
                    activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, target);
                }
                else
                {
                    activeChar.addScript(new DoorRequestHolder((L2DoorInstance) target));
                    if (!((L2DoorInstance) target).isOpen())
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
