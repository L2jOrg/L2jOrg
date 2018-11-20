package org.l2j.gameserver.network.l2.s2c;

import java.util.List;

/**
 * Format: ch ddd [ddd]
 */
public class ExGetBossRecord extends L2GameServerPacket
{
	private List<BossRecordInfo> _bossRecordInfo;
	private int _ranking;
	private int _totalPoints;

	public ExGetBossRecord(int ranking, int totalScore, List<BossRecordInfo> bossRecordInfo)
	{
		_ranking = ranking; // char ranking
		_totalPoints = totalScore; // char total points
		_bossRecordInfo = bossRecordInfo;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_ranking); // char ranking
		writeInt(_totalPoints); // char total points

		writeInt(_bossRecordInfo.size()); // list size
		for(BossRecordInfo w : _bossRecordInfo)
		{
			writeInt(w._bossId);
			writeInt(w._points);
			writeInt(w._unk1);// don`t know
		}
	}

	public static class BossRecordInfo
	{
		public int _bossId;
		public int _points;
		public int _unk1;

		public BossRecordInfo(int bossId, int points, int unk1)
		{
			_bossId = bossId;
			_points = points;
			_unk1 = unk1;
		}
	}
}