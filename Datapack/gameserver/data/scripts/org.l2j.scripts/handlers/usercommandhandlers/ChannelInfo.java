package handlers.usercommandhandlers;

import org.l2j.gameserver.handler.IUserCommandHandler;
import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExMultiPartyCommandChannelInfo;

/**
 * Channel Info user command.
 * @author chris_00
 */
public class ChannelInfo implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		97
	};
	
	@Override
	public boolean useUserCommand(int id, Player player)
	{
		if (id != COMMAND_IDS[0])
		{
			return false;
		}
		
		if ((player.getParty() == null) || (player.getParty().getCommandChannel() == null))
		{
			return false;
		}
		
		final CommandChannel channel = player.getParty().getCommandChannel();
		player.sendPacket(new ExMultiPartyCommandChannelInfo(channel));
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
