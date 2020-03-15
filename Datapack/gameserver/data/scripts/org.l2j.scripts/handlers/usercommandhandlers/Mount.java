package handlers.usercommandhandlers;

import org.l2j.gameserver.handler.IUserCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * Mount user command.
 * @author Tempy
 */
public class Mount implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		61
	};
	
	@Override
	public synchronized boolean useUserCommand(int id, Player player)
	{
		if (id != COMMAND_IDS[0])
		{
			return false;
		}
		return player.mountPlayer(player.getPet());
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
