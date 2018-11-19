package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

/**
 * @author Bonux
**/
public class ExWorldChatCnt extends L2GameServerPacket
{
	private final int _count;

	public ExWorldChatCnt(Player player)
	{
		_count = player.getWorldChatPoints();
	}

	@Override
	protected void writeImpl()
	{
		writeD(_count);
	}
}
