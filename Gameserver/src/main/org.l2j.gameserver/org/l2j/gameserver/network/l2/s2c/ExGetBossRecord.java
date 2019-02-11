package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;
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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_ranking); // char ranking
		buffer.putInt(_totalPoints); // char total points

		buffer.putInt(_bossRecordInfo.size()); // list size
		for(BossRecordInfo w : _bossRecordInfo)
		{
			buffer.putInt(w._bossId);
			buffer.putInt(w._points);
			buffer.putInt(w._unk1);// don`t know
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