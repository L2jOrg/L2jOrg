package org.l2j.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Servitor;

public class PartySmallWindowAllPacket extends L2GameServerPacket
{
	private int leaderId, loot;
	private List<PartySmallWindowMemberInfo> members = new ArrayList<PartySmallWindowMemberInfo>();

	public PartySmallWindowAllPacket(Party party, Player exclude)
	{
		leaderId = party.getPartyLeader().getObjectId();
		loot = party.getLootDistribution();

		for(Player member : party.getPartyMembers())
			if(member != exclude)
				members.add(new PartySmallWindowMemberInfo(member));
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(leaderId); // c3 party leader id
		writeByte(loot); //c3 party loot type (0,1,2,....)
		writeByte(members.size());
		for(PartySmallWindowMemberInfo mi : members)
		{
			writeInt(mi.member.objId);
			writeString(mi.member.name);
			writeInt(mi.member.curCp);
			writeInt(mi.member.maxCp);
			writeInt(mi.member.curHp);
			writeInt(mi.member.maxHp);
			writeInt(mi.member.curMp);
			writeInt(mi.member.maxMp);
			writeInt(0x00);
			writeByte(mi.member.level);
			writeShort(mi.member.classId);
			writeByte(mi.member.sex);
			writeShort(mi.member.raceId);
			writeInt(mi.m_servitors.size()); // Pet Count
			for(PartyMember servitor : mi.m_servitors)
			{
				writeInt(servitor.objId);
				writeInt(servitor.npcId);
				writeByte(servitor.type);
				writeString(servitor.name);
				writeInt(servitor.curHp);
				writeInt(servitor.maxHp);
				writeInt(servitor.curMp);
				writeInt(servitor.maxMp);
				writeByte(servitor.level);
			}
		}
	}

	public static class PartySmallWindowMemberInfo
	{
		public PartyMember member;
		public List<PartyMember> m_servitors;

		public PartySmallWindowMemberInfo(Player player)
		{
			member = new PartyMember();
			member.name = player.getName();
			member.objId = player.getObjectId();
			member.curCp = (int) player.getCurrentCp();
			member.maxCp = player.getMaxCp();
			member.curHp = (int) player.getCurrentHp();
			member.maxHp = player.getMaxHp();
			member.curMp = (int) player.getCurrentMp();
			member.maxMp = player.getMaxMp();
			member.level = player.getLevel();
			member.classId = player.getClassId().getId();
			member.raceId = player.getRace().ordinal();
			member.sex = player.getSex().ordinal();
			member.isPartySubstituteStarted = player.isPartySubstituteStarted() ? 1 : 0;

			m_servitors = new ArrayList<PartyMember>();

			for(Servitor s : player.getServitors())
			{
				PartyMember m_servitor = new PartyMember();
				m_servitor.name = s.getName();
				m_servitor.objId = s.getObjectId();
				m_servitor.npcId = s.getNpcId() + 1000000;
				m_servitor.curHp = (int) s.getCurrentHp();
				m_servitor.maxHp = s.getMaxHp();
				m_servitor.curMp = (int) s.getCurrentMp();
				m_servitor.maxMp = s.getMaxMp();
				m_servitor.level = s.getLevel();
				m_servitor.type = s.getServitorType();
				m_servitors.add(m_servitor);
			}
		}
	}

	public static class PartyMember
	{
		public String name;
		public int objId, npcId, curCp, maxCp, curHp, maxHp, curMp, maxMp, level, classId, raceId, type, sex, isPartySubstituteStarted;
	}
}