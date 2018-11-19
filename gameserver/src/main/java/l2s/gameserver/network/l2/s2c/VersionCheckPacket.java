package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.Config;

public class VersionCheckPacket extends L2GameServerPacket
{
	private byte[] _key;

	public VersionCheckPacket(byte[] key)
	{
		_key = key;
	}

	@Override
	public void writeImpl()
	{
		if(_key == null || _key.length == 0)
		{
			writeC(0x00);
			return;
		}
		writeC(0x01);
		for(int i = 0; i < 8; i++)
			writeC(_key[i]);
		writeD(0x01);
		writeD(Config.REQUEST_ID);	// Server ID
		writeC(0x01);
		writeD(0x00); // Seed (obfuscation key)
		writeC(0x01);	// Classic?
		writeC(0x00);	// Classic?
	}
}