package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Skill.SkillMagicType;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author : Bonux
 */
public class ExChangeMPCost extends L2GameServerPacket
{
	private final int _type;
	private final double _value;

	public ExChangeMPCost(SkillMagicType type, double value)
	{
		_type = type.ordinal();
		_value = value;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_type);
		buffer.putDouble(_value);
	}
}