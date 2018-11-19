package l2s.gameserver.network.l2.s2c;

/**
 * Asks the player to join a Command Channel
 */
public class ExAskJoinMPCCPacket extends L2GameServerPacket
{
	private String _requestorName;

	/**
	 * @param String Name of CCLeader
	 */
	public ExAskJoinMPCCPacket(String requestorName)
	{
		_requestorName = requestorName;
	}

	@Override
	protected void writeImpl()
	{
		writeS(_requestorName); // лидер CC
		writeD(0x00);
	}
}