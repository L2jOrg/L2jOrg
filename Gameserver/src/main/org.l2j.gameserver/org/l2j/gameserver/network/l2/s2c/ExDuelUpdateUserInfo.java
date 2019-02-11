package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * chsddddddddd
 * FE type
 * 4F 00 ex_type
 * 4E 00 65 00 6C 00 75 00 44 00 69 00 6D 00 00 00 name
 * 04 A6 C0 4C objectID?????
 * 2C 00 00 00 class_id
 * 06 00 00 00 level
 * 00 01 00 00 cur_hp
 * 00 01 00 00 max_hp
 * 4B 00 00 00 cur_mp
 * 4B 00 00 00 max_mp
 * 80 00 00 00 cur_cp
 * 80 00 00 00 max_cp
 */
public class ExDuelUpdateUserInfo extends L2GameServerPacket
{
	private String _name;
	private int obj_id, class_id, level, curHp, maxHp, curMp, maxMp, curCp, maxCp;

	public ExDuelUpdateUserInfo(Player attacker)
	{
		_name = attacker.getName();
		obj_id = attacker.getObjectId();
		class_id = attacker.getClassId().getId();
		level = attacker.getLevel();
		curHp = (int) attacker.getCurrentHp();
		maxHp = attacker.getMaxHp();
		curMp = (int) attacker.getCurrentMp();
		maxMp = attacker.getMaxMp();
		curCp = (int) attacker.getCurrentCp();
		maxCp = attacker.getMaxCp();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		writeString(_name, buffer);
		buffer.putInt(obj_id);
		buffer.putInt(class_id);
		buffer.putInt(level);
		buffer.putInt(curHp);
		buffer.putInt(maxHp);
		buffer.putInt(curMp);
		buffer.putInt(maxMp);
		buffer.putInt(curCp);
		buffer.putInt(maxCp);
	}
}