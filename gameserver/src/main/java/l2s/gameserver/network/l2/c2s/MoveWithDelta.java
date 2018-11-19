package l2s.gameserver.network.l2.c2s;

/**
 * Format: (c) ddd
 * d: dx
 * d: dy
 * d: dz
 */
public class MoveWithDelta extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _dx, _dy, _dz;

	@Override
	protected void readImpl()
	{
		_dx = readD();
		_dy = readD();
		_dz = readD();
	}

	@Override
	protected void runImpl()
	{
		// TODO this
	}
}