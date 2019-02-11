package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.data.xml.holder.ResidenceHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.residence.ClanHall;
import org.l2j.gameserver.model.pledge.Alliance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.model.pledge.UnitMember;
import org.l2j.gameserver.network.l2.GameClient;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(0x00);
		writeString(_charName, buffer);
		buffer.putInt(_clanObjectId);
		buffer.putInt(0x00);
		writeString(_unitName, buffer);
		writeString(_leaderName, buffer);

		buffer.putInt(_clanCrestId);
		buffer.putInt(_level);
		buffer.putInt(_hasCastle);
		if(_hasInstantClanHall > 0)
		{
			buffer.putInt(0x01);
			buffer.putInt(_hasInstantClanHall);
		}
		else if(_hasClanHall != 0)
		{
			buffer.putInt(0x00);
			buffer.putInt(_hasClanHall);
		}
		else
		{
			buffer.putInt(0x00);
			buffer.putInt(0x00);
		}
		buffer.putInt(0x00);
		buffer.putInt(_rank);
		buffer.putInt(_reputation);
		buffer.putInt(_isDisbanded ? 3 : 0);
		buffer.putInt(0x00);
		buffer.putInt(_allianceObjectId);
		writeString(_allianceName, buffer);
		buffer.putInt(_allianceCrestId);
		buffer.putInt(_atClanWar ?  1 : 0);
		buffer.putInt(0); // Territory castle ID

		buffer.putInt(_members.size());
		for(PledgePacketMember m : _members)
		{
			writeString(m._name, buffer);
			buffer.putInt(m._level);
			buffer.putInt(m._classId);
			buffer.putInt(m._sex);
			buffer.putInt(m._race);
			buffer.putInt(m._online);
			buffer.putInt(m._hasSponsor ? 1 : 0);
			buffer.put((byte)0x00);
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