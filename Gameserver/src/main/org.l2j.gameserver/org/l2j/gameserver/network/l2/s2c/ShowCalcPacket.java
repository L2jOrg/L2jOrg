package org.l2j.gameserver.network.l2.s2c;

/**
 * sample: d
 */
public class ShowCalcPacket extends L2GameServerPacket
{
	private int _calculatorId;

	public ShowCalcPacket(int calculatorId)
	{
		_calculatorId = calculatorId;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_calculatorId);
	}
}