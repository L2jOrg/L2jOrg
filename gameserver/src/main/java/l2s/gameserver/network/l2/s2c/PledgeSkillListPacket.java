package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.SubUnit;
import l2s.gameserver.skills.SkillEntry;

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
	protected final void writeImpl()
	{
		writeD(_allSkills.size());
		writeD(_unitSkills.size());

		for(SkillInfo info : _allSkills)
		{
			writeD(info._id);
			writeD(info._level);
		}

		for(UnitSkillInfo info : _unitSkills)
		{
			writeD(info._type);
			writeD(info._id);
			writeD(info._level);
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