package handlers.actionhandlers;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.IActionHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

public class TrapAction implements IActionHandler
{
	@Override
	public boolean action(Player activeChar, WorldObject target, boolean interact)
	{
		// Aggression target lock effect
		if (activeChar.isLockedTarget() && (activeChar.getLockedTarget() != target))
		{
			activeChar.sendPacket(SystemMessageId.FAILED_TO_CHANGE_ENMITY);
			return false;
		}
		
		activeChar.setTarget(target);
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2TrapInstance;
	}
}
