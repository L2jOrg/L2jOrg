package l2s.gameserver.network.l2.s2c;

public class TradeRequestPacket extends L2GameServerPacket
{
	private int _senderId;

	public TradeRequestPacket(int senderId)
	{
		_senderId = senderId;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_senderId);
	}
}