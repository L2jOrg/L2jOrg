package l2s.gameserver.network.l2.s2c;

public class RestartResponsePacket extends L2GameServerPacket
{
	public static final RestartResponsePacket OK = new RestartResponsePacket(1), FAIL = new RestartResponsePacket(0);
	private String _message;
	private int _param;

	public RestartResponsePacket(int param)
	{
		_message = "bye";
		_param = param;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_param); //01-ok
		writeS(_message);
	}
}