package l2s.gameserver.network.l2.s2c;

/**
 * @author Bonux
**/
public class ExBlockRemoveResult extends L2GameServerPacket
{
	private final String _blockName;

	public ExBlockRemoveResult(String name)
	{
		_blockName = name;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(1); //UNK
		writeS(_blockName);
	}
}
