package l2s.gameserver.network.l2.s2c;

/**
 * Format: ch S
 */
public class ExAskJoinPartyRoom extends L2GameServerPacket
{
	private String _charName;
	private String _roomName;

	public ExAskJoinPartyRoom(String charName, String roomName)
	{
		_charName = charName;
		_roomName = roomName;
	}

	@Override
	protected final void writeImpl()
	{
		writeS(_charName);
		writeS(_roomName);
	}
}