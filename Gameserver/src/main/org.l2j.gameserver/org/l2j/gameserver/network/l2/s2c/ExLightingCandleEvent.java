package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;

/**
 *
 * @author monithly
 */
@StaticPacket
public class ExLightingCandleEvent extends L2GameServerPacket
{
	public static final L2GameServerPacket ENABLED = new ExLightingCandleEvent(1);
	public static final L2GameServerPacket DISABLED = new ExLightingCandleEvent(0);

	private final int _value;

	private ExLightingCandleEvent(int value)
	{
		_value = value;
	}

	@Override
	protected void writeImpl()
	{
		writeShort(_value);	// Available
	}

	@Override
	protected int packetSize() {
		return 7;
	}
}
