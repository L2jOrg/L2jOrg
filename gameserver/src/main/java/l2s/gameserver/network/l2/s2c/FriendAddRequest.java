package l2s.gameserver.network.l2.s2c;

/**
 * format: cS
 */
public class FriendAddRequest extends L2GameServerPacket
{
	private String _requestorName;

	public FriendAddRequest(String requestorName)
	{
		_requestorName = requestorName;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0); // 0
		writeS(_requestorName);
	}
}