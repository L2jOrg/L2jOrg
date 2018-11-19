package l2s.gameserver.network.l2.s2c;

public class JoinPartyPacket extends L2GameServerPacket
{
	public static final L2GameServerPacket SUCCESS = new JoinPartyPacket(1);
	public static final L2GameServerPacket FAIL = new JoinPartyPacket(0);

	private int _response;

	public JoinPartyPacket(int response)
	{
		_response = response;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_response);
	}
}