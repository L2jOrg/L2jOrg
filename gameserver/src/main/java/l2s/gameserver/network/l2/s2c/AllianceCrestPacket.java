package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.Config;

public class AllianceCrestPacket extends L2GameServerPacket
{
	private int _crestId;
	private byte[] _data;

	public AllianceCrestPacket(int crestId, byte[] data)
	{
		_crestId = crestId;
		_data = data;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(Config.REQUEST_ID);
		writeD(_crestId);
		writeD(_data.length);
		writeB(_data);
	}
}