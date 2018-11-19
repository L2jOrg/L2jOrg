package l2s.gameserver.network.l2.s2c;

/**
 * sample
 * <p>
 * 7d
 * c1 b2 e0 4a
 * 00 00 00 00
 * <p>
 *
 * format
 * cdd
 */
public class AskJoinAlliancePacket extends L2GameServerPacket
{
	private String _requestorName;
	private String _requestorAllyName;
	private int _requestorId;

	public AskJoinAlliancePacket(int requestorId, String requestorName, String requestorAllyName)
	{
		_requestorName = requestorName;
		_requestorAllyName = requestorAllyName;
		_requestorId = requestorId;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_requestorId);
		writeS(_requestorName);
		writeS("");
		writeS(_requestorAllyName);
	}
}