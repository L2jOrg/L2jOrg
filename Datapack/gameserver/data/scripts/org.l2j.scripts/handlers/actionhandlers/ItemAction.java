package handlers.actionhandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.IActionHandler;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.SiegeGuardManager;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.SystemMessageId;

public class ItemAction implements IActionHandler
{
	@Override
	public boolean action(Player activeChar, WorldObject target, boolean interact)
	{
		final Castle castle = CastleManager.getInstance().getCastle(target);
		if ((castle != null) && (SiegeGuardManager.getInstance().getSiegeGuardByItem(castle.getId(), target.getId()) != null))
		{
			if ((activeChar.getClan() == null) || (castle.getOwnerId() != activeChar.getClanId()) || !activeChar.hasClanPrivilege(ClanPrivilege.CS_MERCENARIES))
			{
				activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_CANCEL_MERCENARY_POSITIONING);
				activeChar.setTarget(target);
				activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				return false;
			}
		}
		
		if (!activeChar.isFlying())
		{
			activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, target);
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2ItemInstance;
	}
}