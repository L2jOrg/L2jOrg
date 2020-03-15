package handlers.usercommandhandlers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.handler.IUserCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.world.WorldTimeController;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Time user command.
 */
public class Time implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		77
	};
	
	private static final SimpleDateFormat fmt = new SimpleDateFormat("H:mm.");
	
	@Override
	public boolean useUserCommand(int id, Player player)
	{
		if (COMMAND_IDS[0] != id)
		{
			return false;
		}
		
		final int t = WorldTimeController.getInstance().getGameTime();
		final String h = Integer.toString(((t / 60) % 24));
		String m;
		if ((t % 60) < 10)
		{
			m = "0" + (t % 60);
		}
		else
		{
			m = Integer.toString((t % 60));
		}
		
		SystemMessage sm;
		if (WorldTimeController.getInstance().isNight())
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.THE_CURRENT_TIME_IS_S1_S2);
			sm.addString(h);
			sm.addString(m);
		}
		else
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.THE_CURRENT_TIME_IS_S1_S2);
			sm.addString(h);
			sm.addString(m);
		}
		player.sendPacket(sm);
		if (Config.DISPLAY_SERVER_TIME)
		{
			player.sendMessage("Server time is " + fmt.format(new Date(System.currentTimeMillis())));
		}
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
