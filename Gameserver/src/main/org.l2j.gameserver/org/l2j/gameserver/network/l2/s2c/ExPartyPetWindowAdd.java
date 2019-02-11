package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExPartyPetWindowAdd extends L2GameServerPacket
{
	private final int ownerId, npcId, type, curHp, maxHp, curMp, maxMp, level;
	private final int summonId;
	private final String name;

	public ExPartyPetWindowAdd(Servitor summon)
	{
		summonId = summon.getObjectId();
		ownerId = summon.getPlayer().getObjectId();
		npcId = summon.getNpcId() + 1000000;
		type = summon.getServitorType();
		name = summon.getName();
		curHp = (int) summon.getCurrentHp();
		maxHp = summon.getMaxHp();
		curMp = (int) summon.getCurrentMp();
		maxMp = summon.getMaxMp();
		level = summon.getLevel();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(summonId);
		buffer.putInt(npcId);
		buffer.putInt(type);
		buffer.putInt(ownerId);
		writeString(name, buffer);
		buffer.putInt(curHp);
		buffer.putInt(maxHp);
		buffer.putInt(curMp);
		buffer.putInt(maxMp);
		buffer.putInt(level);
	}
}