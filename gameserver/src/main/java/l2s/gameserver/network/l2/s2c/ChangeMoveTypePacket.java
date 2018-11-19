package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;

/**
 * 0000: 3e 2a 89 00 4c 01 00 00 00                         .|...
 *
 * format   dd
 */
public class ChangeMoveTypePacket extends L2GameServerPacket
{
	public static int WALK = 0;
	public static int RUN = 1;

	private int _chaId;
	private boolean _running;

	public ChangeMoveTypePacket(Creature cha)
	{
		_chaId = cha.getObjectId();
		_running = cha.isRunning();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_chaId);
		writeD(_running ? 1 : 0);
		writeD(0); //c2
	}
}