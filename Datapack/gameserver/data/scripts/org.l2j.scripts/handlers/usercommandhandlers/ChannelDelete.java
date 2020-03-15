package handlers.usercommandhandlers;

import org.l2j.gameserver.handler.IUserCommandHandler;
import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * Channel Delete user command.
 * @author Chris
 */
public class ChannelDelete implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		93
	};
	
	@Override
	public boolean useUserCommand(int id, Player player)
	{
		if (id != COMMAND_IDS[0])
		{
			return false;
		}
		
		if (player.isInParty())
		{
			if (player.getParty().isLeader(player) && player.getParty().isInCommandChannel() && player.getParty().getCommandChannel().getLeader().equals(player))
			{
				final CommandChannel channel = player.getParty().getCommandChannel();
				
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_COMMAND_CHANNEL_HAS_BEEN_DISBANDED);
				channel.broadcastPacket(sm);
				
				channel.disbandChannel();
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
