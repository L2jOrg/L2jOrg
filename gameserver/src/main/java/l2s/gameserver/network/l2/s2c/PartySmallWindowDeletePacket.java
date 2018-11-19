package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

public class PartySmallWindowDeletePacket extends L2GameServerPacket
{
	private final int _objId;
	private final String _name;

	public PartySmallWindowDeletePacket(Player member)
	{
		_objId = member.getObjectId();
		_name = member.getName();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_objId);
		writeS(_name);
	}
}