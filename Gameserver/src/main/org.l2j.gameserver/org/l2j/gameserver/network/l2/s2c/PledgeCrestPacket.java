package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.Config;

public class PledgeCrestPacket extends L2GameServerPacket
{
	private int _crestId;
	private int _crestSize;
	private byte[] _data;

	public PledgeCrestPacket(int crestId, byte[] data)
	{
		_crestId = crestId;
		_data = data;
		_crestSize = _data.length;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(Config.REQUEST_ID);
		writeD(_crestId);
		writeD(_crestSize);
		writeB(_data);
	}
}