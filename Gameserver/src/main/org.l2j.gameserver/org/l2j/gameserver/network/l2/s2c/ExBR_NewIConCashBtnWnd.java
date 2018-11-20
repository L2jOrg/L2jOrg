package org.l2j.gameserver.network.l2.s2c;

/**
 * @author Bonux
**/
public class ExBR_NewIConCashBtnWnd extends L2GameServerPacket
{
	public static final L2GameServerPacket HAS_UPDATES = new ExLightingCandleEvent(1);
	public static final L2GameServerPacket NO_UPDATES = new ExLightingCandleEvent(0);

	private final int _value;

	public ExBR_NewIConCashBtnWnd(int value)
	{
		_value = value;
	}

	@Override
	protected void writeImpl()
	{
		writeShort(_value);	// Has Updates
	}
}
