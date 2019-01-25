package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.settings.ServerSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

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
		writeInt(getSettings(ServerSettings.class).serverId());
		writeInt(_crestId);
		writeInt(_crestSize);
		writeBytes(_data);
	}
}