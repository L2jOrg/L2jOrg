package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Servitor;

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
	protected final void writeImpl()
	{
		writeInt(summonId);
		writeInt(npcId);
		writeInt(type);
		writeInt(ownerId);
		writeString(name);
		writeInt(curHp);
		writeInt(maxHp);
		writeInt(curMp);
		writeInt(maxMp);
		writeInt(level);
	}
}