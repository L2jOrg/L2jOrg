package org.l2j.gameserver.network.l2.s2c;

/**
 * @author Bonux
**/
public class ExBR_NewIConCashBtnWnd extends L2GameServerPacket {

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
