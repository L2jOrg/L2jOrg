package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;

public class RequestFriendDel extends L2GameClientPacket
{
	private String _name;

	@Override
	protected void readImpl()
	{
		_name = readS(16);
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		player.getFriendList().remove(_name);
	}
}