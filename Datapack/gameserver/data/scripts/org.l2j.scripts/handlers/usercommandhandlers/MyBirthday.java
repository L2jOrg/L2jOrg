package handlers.usercommandhandlers;

import org.l2j.gameserver.handler.IUserCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * My Birthday user command.
 * @author JIV
 * @author JoeAlisson
 */
public class MyBirthday implements IUserCommandHandler {
	private static final int[] COMMAND_IDS = {
		126
	};
	
	@Override
	public boolean useUserCommand(int id, Player player) {
		if (id != COMMAND_IDS[0]) {
			return false;
		}
		
		var date = player.getCreateDate();
		player.sendPacket(getSystemMessage(SystemMessageId.C1_S_BIRTHDAY_IS_S3_S4_S2)
				.addPcName(player)
				.addInt(date.getYear()).addInt(date.getMonthValue()).addInt(date.getDayOfMonth()));
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
