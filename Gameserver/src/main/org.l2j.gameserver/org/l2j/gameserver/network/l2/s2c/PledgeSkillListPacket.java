package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.model.pledge.SubUnit;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.skills.SkillEntry;

/**
 * Reworked: VISTALL
 */
public class PledgeSkillListPacket extends L2GameServerPacket
{
	private List<SkillInfo> _allSkills = Collections.emptyList();
	private List<UnitSkillInfo> _unitSkills = new ArrayList<UnitSkillInfo>();

	public PledgeSkillListPacket(Clan clan)
	{
		Collection<SkillEntry> skills = clan.getSkills();
		_allSkills = new ArrayList<SkillInfo>(skills.size());

		for(SkillEntry sk : skills)
			_allSkills.add(new SkillInfo(sk.getId(), sk.getLevel()));

		for(SubUnit subUnit : clan.getAllSubUnits())
		{
			for(SkillEntry sk : subUnit.getSkills())
				_unitSkills.add(new UnitSkillInfo(subUnit.getType(), sk.getId(), sk.getLevel()));
		}
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_allSkills.size());
		buffer.putInt(_unitSkills.size());

		for(SkillInfo info : _allSkills)
		{
			buffer.putInt(info._id);
			buffer.putInt(info._level);
		}

		for(UnitSkillInfo info : _unitSkills)
		{
			buffer.putInt(info._type);
			buffer.putInt(info._id);
			buffer.putInt(info._level);
		}
	}

	static class SkillInfo
	{
		public int _id, _level;

		public SkillInfo(int id, int level)
		{
			_id = id;
			_level = level;
		}
	}

	static class UnitSkillInfo extends SkillInfo
	{
		private int _type;

		public UnitSkillInfo(int type, int id, int level)
		{
			super(id, level);
			_type = type;
		}
	}
}