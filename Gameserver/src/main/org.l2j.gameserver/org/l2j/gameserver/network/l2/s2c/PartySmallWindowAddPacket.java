package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;

public class PartySmallWindowAddPacket extends L2GameServerPacket
{
	private final int _leaderObjectId, _loot;
	private final PartySmallWindowAllPacket.PartyMember _member;

	public PartySmallWindowAddPacket(Player player, Player member)
	{
		_leaderObjectId = member.getParty().getPartyLeader().getObjectId();
		_loot = member.getParty().getLootDistribution();
		_member = new PartySmallWindowAllPacket.PartySmallWindowMemberInfo(member).member;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_leaderObjectId);
		writeInt(_loot);
		writeInt(_member.objId);
		writeString(_member.name);
		writeInt(_member.curCp);
		writeInt(_member.maxCp);
		writeInt(_member.curHp);
		writeInt(_member.maxHp);
		writeInt(_member.curMp);
		writeInt(_member.maxMp);
		writeInt(0x00);
		writeByte(_member.level);
		writeShort(_member.classId);
		writeByte(_member.sex);
		writeShort(_member.raceId);
	}
}