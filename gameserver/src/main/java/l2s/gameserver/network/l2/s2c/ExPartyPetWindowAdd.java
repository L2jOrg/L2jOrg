package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Servitor;

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
		writeD(summonId);
		writeD(npcId);
		writeD(type);
		writeD(ownerId);
		writeS(name);
		writeD(curHp);
		writeD(maxHp);
		writeD(curMp);
		writeD(maxMp);
		writeD(level);
	}
}