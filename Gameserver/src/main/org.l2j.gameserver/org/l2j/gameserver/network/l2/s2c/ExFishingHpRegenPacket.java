package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * Format (ch)dddcccd
 * d: cahacter oid
 * d: time left
 * d: fish hp
 * c:
 * c:
 * c: 00 if fish gets damage 02 if fish regens
 * d:
 */
public class ExFishingHpRegenPacket extends L2GameServerPacket
{
	private int _time, _fishHP, _HPmode, _Anim, _GoodUse, _Penalty, _hpBarColor;
	private int char_obj_id;

	public ExFishingHpRegenPacket(Creature character, int time, int fishHP, int HPmode, int GoodUse, int anim, int penalty, int hpBarColor)
	{
		char_obj_id = character.getObjectId();
		_time = time;
		_fishHP = fishHP;
		_HPmode = HPmode;
		_GoodUse = GoodUse;
		_Anim = anim;
		_Penalty = penalty;
		_hpBarColor = hpBarColor;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(char_obj_id);
		buffer.putInt(_time);
		buffer.putInt(_fishHP);
		buffer.put((byte)_HPmode); // 0 = HP stop, 1 = HP raise
		buffer.put((byte)_GoodUse); // 0 = none, 1 = success, 2 = failed
		buffer.put((byte)_Anim); // Anim: 0 = none, 1 = reeling, 2 = pumping
		buffer.putInt(_Penalty); // Penalty
		buffer.put((byte)_hpBarColor); // 0 = normal hp bar, 1 = purple hp bar

	}
}