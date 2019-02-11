package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.base.AcquireType;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Reworked: VISTALL
 */
public class ExAcquirableSkillListByClass extends L2GameServerPacket
{
	private AcquireType _type;
	private final List<Skill> _skills;

	class Skill
	{
		public int id;
		public int nextLevel;
		public int maxLevel;
		public int cost;
		public int requirements;
		public int subUnit;

		Skill(int id, int nextLevel, int maxLevel, int cost, int requirements, int subUnit)
		{
			this.id = id;
			this.nextLevel = nextLevel;
			this.maxLevel = maxLevel;
			this.cost = cost;
			this.requirements = requirements;
			this.subUnit = subUnit;
		}
	}

	public ExAcquirableSkillListByClass(AcquireType type, int size)
	{
		_skills = new ArrayList<Skill>(size);
		_type = type;
	}

	public void addSkill(int id, int nextLevel, int maxLevel, int Cost, int requirements, int subUnit)
	{
		_skills.add(new Skill(id, nextLevel, maxLevel, Cost, requirements, subUnit));
	}

	public void addSkill(int id, int nextLevel, int maxLevel, int Cost, int requirements)
	{
		_skills.add(new Skill(id, nextLevel, maxLevel, Cost, requirements, 0));
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putShort((short) _type.getId());
		buffer.putShort((short) _skills.size());

		for(Skill temp : _skills)
		{
			buffer.putInt(temp.id);
			buffer.putShort((short) temp.nextLevel);
			buffer.putShort((short) temp.maxLevel);
			buffer.put((byte)temp.requirements);
			buffer.putLong(temp.cost);
			buffer.put((byte)0x01); // UNK
			if(_type == AcquireType.SUB_UNIT)
				buffer.putShort((short) temp.subUnit);
		}
	}
}