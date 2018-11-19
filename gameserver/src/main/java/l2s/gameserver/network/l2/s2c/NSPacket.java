package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;

public class NSPacket extends NpcStringContainer
{
	private int _objId;
	private int _type;
	private int _id;

	public NSPacket(NpcInstance npc, ChatType chatType, String text)
	{
		this(npc, chatType, NpcString.NONE, text);
	}

	public NSPacket(NpcInstance npc, ChatType chatType, NpcString npcString, String... params)
	{
		super(npcString, params);
		_objId = npc.getObjectId();
		_id = npc.getNpcId();
		_type = chatType.ordinal();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_objId);
		writeD(_type);
		writeD(1000000 + _id);
		writeElements();
	}
}