package l2s.gameserver.network.l2.s2c;

/**
 * @author : Ragnarok
 * @date : 28.03.12  16:23
 */
public class ExCallToChangeClass extends L2GameServerPacket
{
	private int _classId;
	private boolean _showMsg;

	public ExCallToChangeClass(int classId, boolean showMsg)
	{
		_classId = classId;
		_showMsg = showMsg;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_classId); // New Class Id
		writeD(_showMsg); // Show Message
	}
}
