package l2s.gameserver.network.l2.s2c;

/**
 * @author Bonux
**/
public class ExBlockDefailInfo extends L2GameServerPacket
{
	private final String _blockName;
	private final String _blockMemo;

	public ExBlockDefailInfo(String name, String memo)
	{
		_blockName = name;
		_blockMemo = memo;

	}

	@Override
	protected final void writeImpl()
	{
		writeS(_blockName);
		writeS(_blockMemo);
	}
}
