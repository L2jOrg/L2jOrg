package handler.usercommands;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.components.SystemMsg;

/**
 * @author VISTALL
 * @date 21:54/08.03.2011
 */
public class SiegeStatus extends ScriptUserCommand
{
	public static final int[] COMMANDS = {99};

	@Override
	public boolean useUserCommand(int id, Player player)
	{
		if(!player.isClanLeader())
		{
			player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_MAY_ISSUE_COMMANDS);
			return false;
		}

		Castle castle = player.getCastle();
		if(castle == null)
			return false;

		if(castle.getSiegeEvent().isInProgress())
			//TODO[Classic]: if(!player.isNoble())
			{
				player.sendPacket(SystemMsg.ONLY_A_CLAN_LEADER_THAT_IS_A_NOBLESSE_CAN_VIEW_THE_SIEGE_WAR_STATUS_WINDOW_DURING_A_SIEGE_WAR);
				return false;
			}

		HtmlMessage msg = new HtmlMessage(5);
		msg.setFile("siege_status.htm");
		msg.replace("%name%", player.getName());
		msg.replace("%kills%", String.valueOf(0));
		msg.replace("%deaths%", String.valueOf(0));
		msg.replace("%type%", String.valueOf(0));

		player.sendPacket(msg);
		return true;
	}

	@Override
	public int[] getUserCommandList()
	{
		return COMMANDS;
	}
}
