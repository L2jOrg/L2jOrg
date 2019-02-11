package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

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
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putShort((short) _value);	// Available
	}

	@Override
	protected int size(GameClient client) {
		return 7;
	}
}
