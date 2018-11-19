package l2s.gameserver.network.l2.c2s;

/**
 * Format chS
 * c: (id) 0x39
 * h: (subid) 0x01
 * S: the summon name (or maybe cmd string ?)
 *
 */
class SuperCmdSummonCmd extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private String _summonName;

	/**
	 * @param buf
	 * @param client
	 */
	@Override
	protected void readImpl()
	{
		_summonName = readS();
	}

	@Override
	protected void runImpl()
	{}
}