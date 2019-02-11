package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.instancemanager.RaidBossSpawnManager;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExRaidBossSpawnInfo extends L2GameServerPacket
{
	private final int[] _aliveBosses;

	public ExRaidBossSpawnInfo()
	{
		_aliveBosses = RaidBossSpawnManager.getInstance().getAliveRaidBosees();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_aliveBosses.length);
		for(int bossId : _aliveBosses)
			buffer.putInt(bossId);
	}
}