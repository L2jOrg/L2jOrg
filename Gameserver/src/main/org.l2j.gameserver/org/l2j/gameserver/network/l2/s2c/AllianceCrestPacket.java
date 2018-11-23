package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.Config;

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
		writeInt(Config.REQUEST_ID);
		writeInt(_crestId);
		writeInt(_data.length);
		writeBytes(_data);
	}
}