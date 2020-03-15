package handlers.usercommandhandlers;

import org.l2j.gameserver.handler.IUserCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadRecord;

/**
 * Olympiad Stat user command.
 * @author JoeAlisson
 */
public class OlympiadStat implements IUserCommandHandler {

	private static final int[] COMMAND_IDS = {
		109
	};
	
	@Override
	public boolean useUserCommand(int id, Player player) {
		if (player.getClassId().level() < 2) {
			player.sendPacket(SystemMessageId.THIS_COMMAND_IS_AVAILABLE_ONLY_WHEN_THE_TARGET_HAS_COMPLETED_THE_2ND_CLASS_TRANSFER);
			return false;
		}
		player.sendPacket(new ExOlympiadRecord());
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
