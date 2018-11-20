package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Skill.SkillMagicType;

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
	protected void writeImpl()
	{
		writeInt(_type);
		writeF(_value);
	}
}