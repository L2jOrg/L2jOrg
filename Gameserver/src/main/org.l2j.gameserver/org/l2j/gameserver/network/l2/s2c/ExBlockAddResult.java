package org.l2j.gameserver.network.l2.s2c;

/**
 * @author Bonux
**/
public class ExBlockAddResult extends L2GameServerPacket
{
	private final String _blockName;

	public ExBlockAddResult(String name)
	{
		_blockName = name;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(1); //UNK
		writeString(_blockName);
	}
}
