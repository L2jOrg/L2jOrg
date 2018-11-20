package org.l2j.gameserver.network.l2.s2c;

public class ExShowQuestMarkPacket extends L2GameServerPacket
{
	private final int _questId, _cond;

	public ExShowQuestMarkPacket(int questId, int cond)
	{
		_questId = questId;
		_cond = cond;
	}

	@Override
	protected void writeImpl()
	{
		writeInt(_questId);
		writeInt(_cond);
	}
}