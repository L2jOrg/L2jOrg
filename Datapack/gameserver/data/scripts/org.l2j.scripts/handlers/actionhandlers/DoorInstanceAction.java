package handlers.actionhandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.xml.impl.ClanHallData;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.IActionHandler;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2DoorInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.holders.DoorRequestHolder;
import org.l2j.gameserver.network.serverpackets.ConfirmDlg;

public class DoorInstanceAction implements IActionHandler
{
	@Override
	public boolean action(L2PcInstance player, L2Object target, boolean interact)
	{
		// Check if the PlayerInstance already target the NpcInstance
		if (player.getTarget() != target)
		{
			player.setTarget(target);
		}
		else if (interact)
		{
			final L2DoorInstance door = (L2DoorInstance) target;
			final ClanHall clanHall = ClanHallData.getInstance().getClanHallByDoorId(door.getId());
			// MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel());
			// player.sendPacket(my);
			if (target.isAutoAttackable(player))
			{
				if (Math.abs(player.getZ() - target.getZ()) < 400)
				{
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
				}
			}
			else if ((player.getClan() != null) && (clanHall != null) && (player.getClanId() == clanHall.getOwnerId()))
			{
				if (!door.isInsideRadius2D(player, L2Npc.INTERACTION_DISTANCE))
				{
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, target);
				}
				else
				{
					player.addScript(new DoorRequestHolder(door));
					if (!door.isOpen())
					{
						player.sendPacket(new ConfirmDlg(1140));
					}
					else
					{
						player.sendPacket(new ConfirmDlg(1141));
					}
				}
			}
			else if ((player.getClan() != null) && (((L2DoorInstance) target).getFort() != null) && (player.getClan() == ((L2DoorInstance) target).getFort().getOwnerClan()) && ((L2DoorInstance) target).isOpenableBySkill() && !((L2DoorInstance) target).getFort().getSiege().isInProgress())
			{
				if (!((L2Character) target).isInsideRadius2D(player, L2Npc.INTERACTION_DISTANCE))
				{
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, target);
				}
				else
				{
					player.addScript(new DoorRequestHolder((L2DoorInstance) target));
					if (!((L2DoorInstance) target).isOpen())
					{
						player.sendPacket(new ConfirmDlg(1140));
					}
					else
					{
						player.sendPacket(new ConfirmDlg(1141));
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
