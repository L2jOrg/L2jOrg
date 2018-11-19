package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;

import java.util.ArrayList;
import java.util.List;

public class RelationChangedPacket extends L2GameServerPacket
{
	public static final int RELATION_PARTY1 = 0x00001; // party member
	public static final int RELATION_PARTY2 = 0x00002; // party member
	public static final int RELATION_PARTY3 = 0x00004; // party member
	public static final int RELATION_PARTY4 = 0x00008; // party member (for information, see L2PcInstance.getRelation())
	public static final int RELATION_PARTYLEADER = 0x00010; // true if is party leader
	public static final int RELATION_HAS_PARTY = 0x00020; // true if is in party
	public static final int RELATION_CLAN_MEMBER = 0x00040; // true if is in clan
	public static final int RELATION_LEADER = 0x00080; // true if is clan leader
	public static final int RELATION_CLAN_MATE = 0x00100; // true if is in same clan
	public static final int RELATION_IN_SIEGE = 0x00200; // true if in siege
	public static final int RELATION_ATTACKER = 0x00400; // true when attacker
	public static final int RELATION_ALLY = 0x00800; // blue siege icon, cannot have if red
	public static final int RELATION_ENEMY = 0x01000; // true when red icon, doesn't matter with blue
	public static final int RELATION_MUTUAL_WAR = 0x04000; // double fist
	public static final int RELATION_1SIDED_WAR = 0x08000; // single fist
	public static final int RELATION_ALLY_MEMBER = 0x10000; // clan is in alliance
	public static final int RELATION_IN_DOMINION_WAR = 0x80000; // Territory Wars

	public static final int USER_RELATION_CLAN_MEMBER = 0x20;
	public static final int USER_RELATION_CLAN_LEADER = 0x40;
	public static final int USER_RELATION_IN_SIEGE = 0x80;
	public static final int USER_RELATION_ATTACKER = 0x100;
	public static final int USER_RELATION_IN_DOMINION_WAR = 0x1000;

	// Masks
	public static final byte SEND_DEFAULT = (byte) 0x01;
	public static final byte SEND_ONE = (byte) 0x02;
	public static final byte SEND_MULTI = (byte) 0x04;

	private byte _mask = (byte) 0x00;

	private final List<RelationChangedData> _datas = new ArrayList<RelationChangedData>(1);

	public RelationChangedPacket()
	{

	}

	public RelationChangedPacket(Playable about, Player target)
	{
		add(about, target);
	}

	public void add(Playable about, Player target)
	{
		RelationChangedData data = new RelationChangedData();
		data.objectId = about.getObjectId();
		data.karma = about.getKarma();
		data.pvpFlag = about.getPvpFlag();
		data.isAutoAttackable = about.isAutoAttackable(target);
		data.relation = about.getRelation(target);

		_datas.add(data);

		if(_datas.size() > 1)
			_mask |= SEND_MULTI;
		else if(_datas.size() == 1)
			_mask |= SEND_ONE;
	}

	@Override
	protected void writeImpl()
	{
		writeC(_mask);
		if((_mask & SEND_MULTI) == SEND_MULTI)
		{
			writeC(_datas.size());
			for(RelationChangedData data : _datas)
				writeRelation(data);
		}
		else if((_mask & SEND_ONE) == SEND_ONE)
			writeRelation(_datas.get(0));
		else if((_mask & SEND_DEFAULT) == SEND_DEFAULT)
			writeD(_datas.get(0).objectId);
	}

	private void writeRelation(RelationChangedData data)
	{
		writeD(data.objectId);
		writeD(data.relation);
		writeC(data.isAutoAttackable);
		writeD(data.karma);
		writeC(data.pvpFlag);
	}

	private static class RelationChangedData
	{
		public int objectId;
		public boolean isAutoAttackable;
		public int relation;
		public int karma;
		public int pvpFlag;
	}
}