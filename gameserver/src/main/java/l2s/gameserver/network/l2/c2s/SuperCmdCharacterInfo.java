package l2s.gameserver.network.l2.c2s;

/**
 * Format chS
 * c: (id) 0x39
 * h: (subid) 0x00
 * S: the character name (or maybe cmd string ?)
 */
class SuperCmdCharacterInfo extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private String _characterName;

	@Override
	protected void readImpl()
	{
		_characterName = readS();
	}

	@Override
	protected void runImpl()
	{}
}