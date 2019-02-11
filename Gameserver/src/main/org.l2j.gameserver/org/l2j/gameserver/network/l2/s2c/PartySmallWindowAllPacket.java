package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.network.l2.GameClient;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(leaderId); // c3 party leader id
		buffer.put((byte)loot); //c3 party loot type (0,1,2,....)
		buffer.put((byte)members.size());
		for(PartySmallWindowMemberInfo mi : members)
		{
			buffer.putInt(mi.member.objId);
			writeString(mi.member.name, buffer);
			buffer.putInt(mi.member.curCp);
			buffer.putInt(mi.member.maxCp);
			buffer.putInt(mi.member.curHp);
			buffer.putInt(mi.member.maxHp);
			buffer.putInt(mi.member.curMp);
			buffer.putInt(mi.member.maxMp);
			buffer.putInt(0x00);
			buffer.put((byte)mi.member.level);
			buffer.putShort((short) mi.member.classId);
			buffer.put((byte)mi.member.sex);
			buffer.putShort((short) mi.member.raceId);
			buffer.putInt(mi.m_servitors.size()); // Pet Count
			for(PartyMember servitor : mi.m_servitors)
			{
				buffer.putInt(servitor.objId);
				buffer.putInt(servitor.npcId);
				buffer.put((byte)servitor.type);
				writeString(servitor.name, buffer);
				buffer.putInt(servitor.curHp);
				buffer.putInt(servitor.maxHp);
				buffer.putInt(servitor.curMp);
				buffer.putInt(servitor.maxMp);
				buffer.put((byte)servitor.level);
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