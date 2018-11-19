package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Skill.SkillMagicType;

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
		writeD(_type);
		writeF(_value);
	}
}