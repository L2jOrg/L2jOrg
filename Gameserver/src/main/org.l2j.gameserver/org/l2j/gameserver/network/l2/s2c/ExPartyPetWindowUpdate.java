package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExPartyPetWindowUpdate extends L2GameServerPacket
{
	private int owner_obj_id, npc_id, _type, curHp, maxHp, curMp, maxMp, level;
	private int obj_id = 0;
	private String _name;

	public ExPartyPetWindowUpdate(Servitor summon)
	{
		obj_id = summon.getObjectId();
		owner_obj_id = summon.getPlayer().getObjectId();
		npc_id = summon.getNpcId() + 1000000;
		_type = summon.getServitorType();
		_name = summon.getName();
		curHp = (int) summon.getCurrentHp();
		maxHp = summon.getMaxHp();
		curMp = (int) summon.getCurrentMp();
		maxMp = summon.getMaxMp();
		level = summon.getLevel();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(obj_id);
		buffer.putInt(npc_id);
		buffer.putInt(_type);
		buffer.putInt(owner_obj_id);
		writeString(_name, buffer);
		buffer.putInt(curHp);
		buffer.putInt(maxHp);
		buffer.putInt(curMp);
		buffer.putInt(maxMp);
		buffer.putInt(level);
	}
}