package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

/**
 * Format:   dddddddddh [h] h [ddd]
 * Пример пакета:
 * 48
 * 86 99 00 4F  86 99 00 4F
 * EF 08 00 00  01 00 00 00
 * 00 00 00 00  00 00 00 00
 * F9 B5 FF FF  7D E0 01 00  68 F3 FF FF
 * 00 00 00 00
 */
public class MagicSkillUse extends L2GameServerPacket
{
	public static final int NONE = -1;

	private final int _targetId;
	private final int _skillId;
	private final int _skillLevel;
	private final int _hitTime;
	private final int _reuseDelay;
	private final int _chaId, _x, _y, _z, _tx, _ty, _tz;
	private int _reuseSkillId;
	private boolean _isServitorSkill;
	private int _actionId;
	private Location _groundLoc = null;
	private boolean _criticalBlow = false;

	public MagicSkillUse(Creature cha, Creature target, int skillId, int skillLevel, int hitTime, long reuseDelay, boolean isServitorSkill, int actionId)
	{
		_chaId = cha.getObjectId();
		_targetId = target.getObjectId();
		_skillId = skillId;
		_skillLevel = skillLevel;
		_hitTime = hitTime;
		_reuseDelay = (int) reuseDelay;
		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();
		_tx = target.getX();
		_ty = target.getY();
		_tz = target.getZ();
		_reuseSkillId = skillId; //TODO: [Bonux]
		_isServitorSkill = isServitorSkill;
		_actionId = actionId;
	}

	public MagicSkillUse(Creature cha, Creature target, int skillId, int skillLevel, int hitTime, long reuseDelay)
	{
		this(cha, target, skillId, skillLevel, hitTime, reuseDelay, false, 0);
	}

	public MagicSkillUse(Creature cha, int skillId, int skillLevel, int hitTime, long reuseDelay)
	{
		this(cha, cha, skillId, skillLevel, hitTime, reuseDelay, false, 0);
	}

	public MagicSkillUse setReuseSkillId(int id)
	{
		_reuseSkillId = id;
		return this;
	}

	public MagicSkillUse setServitorSkillInfo(int actionId)
	{
		_isServitorSkill = true;
		_actionId = actionId;
		return this;
	}

	public MagicSkillUse setGroundLoc(Location loc)
	{
		_groundLoc = loc;
		return this;
	}

	public MagicSkillUse setCriticalBlow(boolean value)
	{
		_criticalBlow = value;
		return this;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(0);
		buffer.putInt(_chaId);
		buffer.putInt(_targetId);
		buffer.putInt(_skillId);
		buffer.putInt(_skillLevel);
		buffer.putInt(_hitTime);
		buffer.putInt(_reuseSkillId);
		buffer.putInt(_reuseDelay);
		buffer.putInt(_x);
		buffer.putInt(_y);
		buffer.putInt(_z);

		if(_criticalBlow)
		{
			buffer.putShort((short) 0x02);
			for(int i = 0; i < 2; i++)
				buffer.putShort((short) 0x00);
		}
		else
			buffer.putShort((short) 0x00);

		if(_groundLoc != null)
		{
			buffer.putShort((short) 0x01);
			buffer.putInt(_groundLoc.x);
			buffer.putInt(_groundLoc.y);
			buffer.putInt(_groundLoc.z);
		}
		else
			buffer.putShort((short) 0x00);

		buffer.putInt(_tx);
		buffer.putInt(_ty);
		buffer.putInt(_tz);
		buffer.putInt(_isServitorSkill ? 0x01 : 0x00); // is Pet Skill
		buffer.putInt(_actionId); // Social Action ID
	}

	@Override
	public L2GameServerPacket packet(Player player)
	{
		if(player != null)
		{
			if(player.isNotShowBuffAnim())
				return _chaId == player.getObjectId() ? super.packet(player) : null;
		}
		return super.packet(player);
	}
}