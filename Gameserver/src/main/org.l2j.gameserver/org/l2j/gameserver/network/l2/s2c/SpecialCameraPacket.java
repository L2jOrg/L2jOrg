package org.l2j.gameserver.network.l2.s2c;

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
	protected final void writeImpl()
	{
		// ddddddddddd
		writeInt(_id); // object id
		writeInt(_dist); //расстояние до объекта
		writeInt(_yaw); // North=90, south=270, east=0, west=180
		writeInt(_pitch); // > 0:looks up,pitch < 0:looks down (угол наклона)
		writeInt(_time); //faster that small value is
		writeInt(_duration); //время анимации
		writeInt(_turn);
		writeInt(_rise);
		writeInt(_widescreen);
		writeInt(_unknown);
	}
}