package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

public class ExCuriousHouseMemberUpdate extends L2GameServerPacket
{
	private Player _player;

	public ExCuriousHouseMemberUpdate(Player player)
	{
		_player = player;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_player.getObjectId());
		writeD(_player.getMaxHp());
		writeD(_player.getMaxCp());
		writeD((int)_player.getCurrentHp());
		writeD((int)_player.getCurrentCp());
	}
}

