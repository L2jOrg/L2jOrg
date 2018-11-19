package l2s.gameserver.network.l2.s2c;

/**
 * @author VISTALL
 */
public class ExResponseShowContents extends L2GameServerPacket
{
	private final String _contents;

	public ExResponseShowContents(String contents)
	{
		_contents = contents;
	}

	@Override
	protected void writeImpl()
	{
		writeS(_contents);
	}
}