package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

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
		writeD(_leaderObjectId);
		writeD(_loot);
		writeD(_member.objId);
		writeS(_member.name);
		writeD(_member.curCp);
		writeD(_member.maxCp);
		writeD(_member.curHp);
		writeD(_member.maxHp);
		writeD(_member.curMp);
		writeD(_member.maxMp);
		writeD(0x00);
		writeC(_member.level);
		writeH(_member.classId);
		writeC(_member.sex);
		writeH(_member.raceId);
	}
}