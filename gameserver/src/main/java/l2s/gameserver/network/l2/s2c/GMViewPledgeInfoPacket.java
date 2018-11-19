package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.residence.ClanHall;
import l2s.gameserver.model.pledge.Alliance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.UnitMember;

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
		writeD(0x00);
		writeS(_charName);
		writeD(_clanObjectId);
		writeD(0x00);
		writeS(_unitName);
		writeS(_leaderName);

		writeD(_clanCrestId);
		writeD(_level);
		writeD(_hasCastle);
		if(_hasInstantClanHall > 0)
		{
			writeD(0x01);
			writeD(_hasInstantClanHall);
		}
		else if(_hasClanHall != 0)
		{
			writeD(0x00);
			writeD(_hasClanHall);
		}
		else
		{
			writeD(0x00);
			writeD(0x00);
		}
		writeD(0x00);
		writeD(_rank);
		writeD(_reputation);
		writeD(_isDisbanded ? 3 : 0);
		writeD(0x00);
		writeD(_allianceObjectId);
		writeS(_allianceName);
		writeD(_allianceCrestId);
		writeD(_atClanWar);
		writeD(0); // Territory castle ID

		writeD(_members.size());
		for(PledgePacketMember m : _members)
		{
			writeS(m._name);
			writeD(m._level);
			writeD(m._classId);
			writeD(m._sex);
			writeD(m._race);
			writeD(m._online);
			writeD(m._hasSponsor ? 1 : 0);
			writeC(0x00);
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