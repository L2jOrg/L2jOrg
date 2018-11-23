package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.network.l2.s2c.ExBR_LoadEventTopRankers;

public class RequestExBR_EventRankerList extends L2GameClientPacket
{
	private static final String _C__D0_7B_BREVENTRANKERLIST = "[C] D0:7B BrEventRankerList";

	private int _eventId;
	private int _day;
	@SuppressWarnings("unused")
	private int _ranking;
	@Override
	protected void readImpl()
	{
		_eventId = readInt();
		_day = readInt(); // 0 - current, 1 - previous
		_ranking = readInt();
	}

	@Override
	protected void runImpl()
	{
		// TODO count, bestScore, myScore
		int count = 0;
		int bestScore = 0;
		int myScore = 0;
		getClient().sendPacket(new ExBR_LoadEventTopRankers(_eventId, _day, count, bestScore, myScore));
	}

	@Override
	public String getType()
	{
		return _C__D0_7B_BREVENTRANKERLIST;
	}
}