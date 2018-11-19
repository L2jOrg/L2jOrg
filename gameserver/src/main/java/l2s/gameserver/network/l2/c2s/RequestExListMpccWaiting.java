package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExListMpccWaiting;

/**
 * @author VISTALL
 */
public class RequestExListMpccWaiting extends L2GameClientPacket
{
	private int _listId;
	private int _locationId;
	private boolean _allLevels;

	@Override
	protected void readImpl() throws Exception
	{
		_listId = readD();
		_locationId = readD();
		_allLevels = readD() == 1;
	}

	@Override
	protected void runImpl() throws Exception
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		player.sendPacket(new ExListMpccWaiting(player, _listId, _locationId, _allLevels));
	}
}