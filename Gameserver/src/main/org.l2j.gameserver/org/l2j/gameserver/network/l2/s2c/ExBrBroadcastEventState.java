package org.l2j.gameserver.network.l2.s2c;

public class ExBrBroadcastEventState extends L2GameServerPacket
{
	private int _eventId;
	private int _eventState;
	private int _param0;
	private int _param1;
	private int _param2;
	private int _param3;
	private int _param4;
	private String _param5;
	private String _param6;

	public static final int APRIL_FOOLS = 20090401;
	public static final int EVAS_INFERNO = 20090801; // event state (0 - hide, 1 - show), day (1-14), percent (0-100)
	public static final int HALLOWEEN_EVENT = 20091031; // event state (0 - hide, 1 - show)
	public static final int RAISING_RUDOLPH = 20091225; // event state (0 - hide, 1 - show)
	public static final int LOVERS_JUBILEE = 20100214; // event state (0 - hide, 1 - show)
	public static final int APRIL_FOOLS_10 = 20100401; // event state (0 - hide, 1 - show)

	public ExBrBroadcastEventState(int eventId, int eventState)
	{
		_eventId = eventId;
		_eventState = eventState;
	}

	public ExBrBroadcastEventState(int eventId, int eventState, int param0, int param1, int param2, int param3, int param4, String param5, String param6)
	{
		_eventId = eventId;
		_eventState = eventState;
		_param0 = param0;
		_param1 = param1;
		_param2 = param2;
		_param3 = param3;
		_param4 = param4;
		_param5 = param5;
		_param6 = param6;
	}

	@Override
	protected void writeImpl()
	{
		writeInt(_eventId);
		writeInt(_eventState);
		writeInt(_param0);
		writeInt(_param1);
		writeInt(_param2);
		writeInt(_param3);
		writeInt(_param4);
		writeString(_param5);
		writeString(_param6);
	}
}
