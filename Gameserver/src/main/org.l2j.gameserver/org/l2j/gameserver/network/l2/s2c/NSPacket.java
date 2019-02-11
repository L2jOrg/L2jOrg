package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.network.l2.components.ChatType;
import org.l2j.gameserver.network.l2.components.NpcString;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objId);
		buffer.putInt(_type);
		buffer.putInt(1000000 + _id);
		writeElements(buffer);
	}
}