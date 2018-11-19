package l2s.gameserver.handler.usercommands.impl;

import l2s.gameserver.data.xml.holder.InstantZoneHolder;
import l2s.gameserver.handler.usercommands.IUserCommandHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.network.l2.components.SystemMsg;

public class InstanceZone implements IUserCommandHandler
{
	/**
	 * Field COMMAND_IDS.
	 */
	private static final int[] COMMAND_IDS =
	{
		114
	};
	
	/**
	 * Method useUserCommand.
	 * @param id int
	 * @param activeChar Player
	 * @return boolean * @see l2s.gameserver.handler.usercommands.IUserCommandHandler#useUserCommand(int, Player)
	 */
	@Override
	public boolean useUserCommand(int id, Player activeChar)
	{
		if (COMMAND_IDS[0] != id)
		{
			return false;
		}
		if (activeChar.getActiveReflection() != null)
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.INSTANT_ZONE_CURRENTLY_IN_USE_S1).addInstanceName(activeChar.getActiveReflection().getInstancedZoneId()));
		}
		int limit;
		boolean noLimit = true;
		boolean showMsg = false;
		for (int i : activeChar.getInstanceReuses().keySet())
		{
			limit = InstantZoneHolder.getInstance().getMinutesToNextEntrance(i, activeChar);
			if (limit > 0)
			{
				noLimit = false;
				if (!showMsg)
				{
					activeChar.sendPacket(SystemMsg.INSTANCE_ZONE_TIME_LIMIT);
					showMsg = true;
				}
				activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_WILL_BE_AVAILABLE_FOR_REUSE_AFTER_S2_HOURS_S3_MINUTES).addInstanceName(i).addInteger(limit / 60).addInteger(limit % 60));
			}
		}
		if (noLimit)
		{
			activeChar.sendPacket(SystemMsg.THERE_IS_NO_INSTANCE_ZONE_UNDER_A_TIME_LIMIT);
		}
		return true;
	}
	
	/**
	 * Method getUserCommandList.
	 * @return int[] * @see l2s.gameserver.handler.usercommands.IUserCommandHandler#getUserCommandList()
	 */
	@Override
	public final int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}