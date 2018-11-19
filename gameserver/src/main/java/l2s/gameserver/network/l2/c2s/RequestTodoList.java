package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExConnectedTimeAndGettableReward;
import l2s.gameserver.network.l2.s2c.ExOneDayReceiveRewardList;
import l2s.gameserver.network.l2.s2c.ExTodoListInzone;

/**
 * @author Bonux
 **/
public class RequestTodoList extends L2GameClientPacket
{
	private int _tab;
	@SuppressWarnings("unused")
	private boolean _showAllLevels;

	@Override
	protected void readImpl()
	{
		_tab = readC(); // Daily Reward = 9, Event = 1, Instance Zone = 2
		_showAllLevels = readC() > 0; // Disabled = 0, Enabled = 1
	}

	@Override
	protected void runImpl()
	{
		switch(_tab)
		{
			case 1:
			case 2:
			{
				sendPacket(new ExTodoListInzone());
				break;
			}
			case 9:
			{
				Player activeChar = getClient().getActiveChar();
				if(activeChar == null)
					sendPacket(new ExOneDayReceiveRewardList());
				else
				{
					sendPacket(ExConnectedTimeAndGettableReward.STATIC);
					sendPacket(new ExOneDayReceiveRewardList(activeChar));
				}
				break;
			}
		}
	}
}