package org.l2j.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.holder.ResidenceHolder;
import org.l2j.gameserver.model.entity.residence.ClanHall;
import org.l2j.gameserver.model.pledge.Alliance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.model.pledge.SubUnit;
import org.l2j.gameserver.model.pledge.UnitMember;

public class PledgeShowMemberListAllPacket extends L2GameServerPacket
{
	private int _clanObjectId, _clanCrestId, _level, _rank, _reputation, _allianceObjectId, _allianceCrestId;
	private int _hasCastle, _hasClanHall, _hasInstantClanHall;
	private boolean _isDisbanded, _atClanWar;
	private String _unitName, _leaderName, _allianceName;
	private int _pledgeType;
	private List<PledgePacketMember> _members;

	public PledgeShowMemberListAllPacket(Clan clan, final SubUnit sub)
	{
		_pledgeType = sub.getType();
		_clanObjectId = clan.getClanId();
		_unitName = sub.getName();
		_leaderName = sub.getLeaderName();
		_clanCrestId = clan.getCrestId();
		_level = clan.getLevel();
		_hasCastle = clan.getCastle();

		ClanHall clanHall = ResidenceHolder.getInstance().getResidence(ClanHall.class, clan.getHasHideout());
		if(clanHall != null)
		{
			_hasClanHall = clanHall.getId();
			_hasInstantClanHall = clanHall.getInstantZoneId();
		}
		else
		{
			_hasClanHall = 0;
			_hasInstantClanHall = 0;
		}

		_rank = clan.getRank();
		_reputation = clan.getReputationScore();
		_atClanWar = clan.isAtWar();
		_isDisbanded = clan.isPlacedForDisband();

		Alliance ally = clan.getAlliance();

		if(ally != null)
		{
			_allianceObjectId = ally.getAllyId();
			_allianceName = ally.getAllyName();
			_allianceCrestId = ally.getAllyCrestId();
		}

		_members = new ArrayList<PledgePacketMember>(sub.size());

		for(UnitMember m : sub.getUnitMembers())
			_members.add(new PledgePacketMember(m));
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_pledgeType == Clan.SUBUNIT_MAIN_CLAN ? 0 : 1);
		writeInt(_clanObjectId);
		writeInt(Config.REQUEST_ID);
		writeInt(_pledgeType);
		writeString(_unitName);
		writeString(_leaderName);

		writeInt(_clanCrestId); // crest id .. is used again
		writeInt(_level);
		writeInt(_hasCastle);
		if(_hasInstantClanHall > 0)
		{
			writeInt(0x01);
			writeInt(_hasInstantClanHall);
		}
		else if(_hasClanHall != 0)
		{
			writeInt(0x00);
			writeInt(_hasClanHall);
		}
		else
		{
			writeInt(0x00);
			writeInt(0x00);
		}
		writeInt(0);
		writeInt(_rank);
		writeInt(_reputation);
		writeInt(_isDisbanded ? 3 : 0);
		writeInt(0x00);
		writeInt(_allianceObjectId);
		writeString(_allianceName);
		writeInt(_allianceCrestId);
		writeInt(_atClanWar);
		writeInt(0x00);//territory Id

		writeInt(_members.size());
		for(PledgePacketMember m : _members)
		{
			writeString(m._name);
			writeInt(m._level);
			writeInt(m._classId);
			writeInt(m._sex);
			writeInt(m._race);
			writeInt(m._online);
			writeInt(m._hasSponsor ? 1 : 0);
			writeByte(m._attendance);
		}
	}

	private class PledgePacketMember
	{
		private String _name;
		private int _level;
		private int _classId;
		private int _sex;
		private int _race;
		private int _online;
		private boolean _hasSponsor;
		private int _attendance;

		public PledgePacketMember(UnitMember m)
		{
			_name = m.getName();
			_level = m.getLevel();
			_classId = m.getClassId();
			_sex = m.getSex();
			_race = 0; //TODO m.getRace()
			_online = m.isOnline() ? m.getObjectId() : 0;
			_hasSponsor = m.getSponsor() != 0;
			_attendance = m.getAttendanceType().ordinal();
		}
	}
}