package l2s.gameserver.network.l2.c2s;

/**
 * format: chS
 */
public class RequestPCCafeCouponUse extends L2GameClientPacket
{
	// format: (ch)S
	private String _unknown;

	@Override
	protected void readImpl()
	{
		_unknown = readS();
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}