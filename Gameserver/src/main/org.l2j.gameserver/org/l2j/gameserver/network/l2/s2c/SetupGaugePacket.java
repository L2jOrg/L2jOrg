package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class SetupGaugePacket extends L2GameServerPacket
{
	public static enum Colors
	{
		NONE,
		RED,
		BLUE,
		GREEN;
	}

	private int _charId;
	private int _color;
	private int _time;
	private int _lostTime;

	public SetupGaugePacket(Creature character, Colors color, int time)
	{
		this(character, color, time, time);
	}

	public SetupGaugePacket(Creature character, Colors color, int time, int lostTime)
	{
		_charId = character.getObjectId();
		_color = color.ordinal();
		_time = time;
		_lostTime = lostTime;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_charId);
		buffer.putInt(_color);
		buffer.putInt(_lostTime);
		buffer.putInt(_time);
	}
}