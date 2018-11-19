package l2s.gameserver.network.l2.c2s;

public class ReplyGameGuardQuery extends L2GameClientPacket
{
	// Format: cdddd
	/* CCP Guard START
	private byte _data[] = new byte[80];
	** CCP Guard END*/

	@Override
	protected void readImpl()
	{
		/* CCP Guard START
		ccpGuard.Protection.doReadReplyGameGuard(getClient(), _buf, _data);
		** CCP Guard END*/
	}

	@Override
	protected void runImpl()
	{
		/* CCP Guard START
		ccpGuard.Protection.doReplyGameGuard(getClient(), _data);
		** CCP Guard END*/
	}
}