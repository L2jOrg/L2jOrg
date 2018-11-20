package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Creature;

/**
 * Format (ch)dddcc
 */
public class ExFishingStartCombatPacket extends L2GameServerPacket
{
	int _time, _hp;
	int _lureType, _deceptiveMode, _mode;
	private int char_obj_id;

	public ExFishingStartCombatPacket(Creature character, int time, int hp, int mode, int lureType, int deceptiveMode)
	{
		char_obj_id = character.getObjectId();
		_time = time;
		_hp = hp;
		_mode = mode;
		_lureType = lureType;
		_deceptiveMode = deceptiveMode;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(char_obj_id);
		writeInt(_time);
		writeInt(_hp);
		writeByte(_mode); // mode: 0 = resting, 1 = fighting
		writeByte(_lureType); // 0 = newbie lure, 1 = normal lure, 2 = night lure
		writeByte(_deceptiveMode); // Fish Deceptive Mode: 0 = no, 1 = yes
	}
}