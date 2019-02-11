package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_leaderObjectId);
		buffer.putInt(_loot);
		buffer.putInt(_member.objId);
		writeString(_member.name, buffer);
		buffer.putInt(_member.curCp);
		buffer.putInt(_member.maxCp);
		buffer.putInt(_member.curHp);
		buffer.putInt(_member.maxHp);
		buffer.putInt(_member.curMp);
		buffer.putInt(_member.maxMp);
		buffer.putInt(0x00);
		buffer.put((byte)_member.level);
		buffer.putShort((short) _member.classId);
		buffer.put((byte)_member.sex);
		buffer.putShort((short) _member.raceId);
	}
}