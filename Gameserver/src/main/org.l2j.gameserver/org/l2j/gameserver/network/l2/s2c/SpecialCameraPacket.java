package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class SpecialCameraPacket extends L2GameServerPacket
{
	private int _id;
	private int _dist;
	private int _yaw;
	private int _pitch;
	private int _time;
	private int _duration;
	private final int _turn;
	private final int _rise;
	private final int _widescreen;
	private final int _unknown;

	public SpecialCameraPacket(int id, int dist, int yaw, int pitch, int time, int duration)
	{
		_id = id;
		_dist = dist;
		_yaw = yaw;
		_pitch = pitch;
		_time = time;
		_duration = duration;
		_turn = 0;
		_rise = 0;
		_widescreen = 0;
		_unknown = 0;
	}

	public SpecialCameraPacket(int id, int dist, int yaw, int pitch, int time, int duration, int turn, int rise, int widescreen, int unk)
	{
		_id = id;
		_dist = dist;
		_yaw = yaw;
		_pitch = pitch;
		_time = time;
		_duration = duration;
		_turn = turn;
		_rise = rise;
		_widescreen = widescreen;
		_unknown = unk;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		// ddddddddddd
		buffer.putInt(_id); // object id
		buffer.putInt(_dist); //расстояние до объекта
		buffer.putInt(_yaw); // North=90, south=270, east=0, west=180
		buffer.putInt(_pitch); // > 0:looks up,pitch < 0:looks down (угол наклона)
		buffer.putInt(_time); //faster that small value is
		buffer.putInt(_duration); //время анимации
		buffer.putInt(_turn);
		buffer.putInt(_rise);
		buffer.putInt(_widescreen);
		buffer.putInt(_unknown);
	}
}