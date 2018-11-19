package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;

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
	protected final void writeImpl()
	{
		writeD(char_obj_id);
		writeD(_time);
		writeD(_fishHP);
		writeC(_HPmode); // 0 = HP stop, 1 = HP raise
		writeC(_GoodUse); // 0 = none, 1 = success, 2 = failed
		writeC(_Anim); // Anim: 0 = none, 1 = reeling, 2 = pumping
		writeD(_Penalty); // Penalty
		writeC(_hpBarColor); // 0 = normal hp bar, 1 = purple hp bar

	}
}