package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.instancemanager.RaidBossSpawnManager;

public class ExRaidBossSpawnInfo extends L2GameServerPacket
{
	private final int[] _aliveBosses;

	public ExRaidBossSpawnInfo()
	{
		_aliveBosses = RaidBossSpawnManager.getInstance().getAliveRaidBosees();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_aliveBosses.length);
		for(int bossId : _aliveBosses)
			writeD(bossId);
	}
}