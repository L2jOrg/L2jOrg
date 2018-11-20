package org.l2j.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.data.xml.holder.ResidenceHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.residence.ClanHall;
import org.l2j.gameserver.model.pledge.Alliance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.model.pledge.UnitMember;

public class GMViewPledgeInfoPacket extends L2GameServerPacket
{
	private final String _charName;
	private int _clanObjectId, _clanCrestId, _level, _rank, _reputation, _allianceObjectId, _allianceCrestId;
	private int _hasCastle, _hasClanHall, _hasInstantClanHall;
	private boolean _isDisbanded, _atClanWar;
	private String _unitName, _leaderName, _allianceName;
	private final List<PledgePacketMember> _members = new ArrayList<PledgePacketMember>();

	public GMViewPledgeInfoPacket(Player activeChar)
	{
		_charName = activeChar.getName();

		Clan clan = activeChar.getClan();
		if(clan == null)
			return;

		_clanObjectId = clan.getClanId();
		_unitName = clan.getName();
		_leaderName = clan.getLeaderName();
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

		for(UnitMember m : clan)
			_members.add(new PledgePacketMember(m));
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(0x00);
		writeString(_charName);
		writeInt(_clanObjectId);
		writeInt(0x00);
		writeString(_unitName);
		writeString(_leaderName);

		writeInt(_clanCrestId);
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
		writeInt(0x00);
		writeInt(_rank);
		writeInt(_reputation);
		writeInt(_isDisbanded ? 3 : 0);
		writeInt(0x00);
		writeInt(_allianceObjectId);
		writeString(_allianceName);
		writeInt(_allianceCrestId);
		writeInt(_atClanWar);
		writeInt(0); // Territory castle ID

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
			writeByte(0x00);
		}
	}

	private class PledgePacketMember
	{
		private String _name;
		private int _level, _classId, _sex, _race, _online;
		private boolean _hasSponsor;

		public PledgePacketMember(UnitMember m)
		{
			_name = m.getName();
			_level = m.getLevel();
			_classId = m.getClassId();

			_sex = m.getSex();
			_race = 0;
			_online = m.isOnline() ? m.getObjectId() : 0;
			_hasSponsor = m.getSponsor() != 0;
		}
	}

}