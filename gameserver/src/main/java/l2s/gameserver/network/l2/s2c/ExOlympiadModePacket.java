package l2s.gameserver.network.l2.s2c;

public class ExOlympiadModePacket extends L2GameServerPacket
{
	// chc
	private int _mode;

	/**
	 * @param _mode (0 = return, 3 = spectate)
	 */
	public ExOlympiadModePacket(int mode)
	{
		_mode = mode;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(_mode);
	}
}