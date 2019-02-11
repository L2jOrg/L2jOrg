package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.network.l2.components.ChatType;
import org.l2j.gameserver.network.l2.components.NpcString;
import org.l2j.gameserver.network.l2.components.SysString;
import org.l2j.gameserver.network.l2.components.SystemMsg;

import java.nio.ByteBuffer;

public class SayPacket2 extends NpcStringContainer
{
	// Flags
	private static final int IS_FRIEND = 1 << 0;
	private static final int IS_CLAN_MEMBER = 1 << 1;
	private static final int IS_MENTEE_OR_MENTOR = 1 << 2;
	private static final int IS_ALLIANCE_MEMBER = 1 << 3;
	private static final int IS_GM = 1 << 4;

	private ChatType _type;
	private SysString _sysString;
	private SystemMsg _systemMsg;

	private int _objectId;
	private String _charName;
	private int _mask;
	private int _charLevel = -1;
	private String _text;

	public SayPacket2(int objectId, ChatType type, SysString st, SystemMsg sm)
	{
		super(NpcString.NONE);
		_objectId = objectId;
		_type = type;
		_sysString = st;
		_systemMsg = sm;
	}

	public SayPacket2(int objectId, ChatType type, String charName, String text)
	{
		this(objectId, type, charName, NpcString.NONE, text);
	}

	public SayPacket2(int objectId, ChatType type, String charName, NpcString npcString, String... params)
	{
		super(npcString, params);
		_objectId = objectId;
		_type = type;
		_charName = charName;
		_text = params.length > 0 ? params[0] : null;
	}

	public void setCharName(String name)
	{
		_charName = name;
	}

	public void setSenderInfo(Player sender, Player receiver)
	{
		_charLevel = sender.getLevel();

		if(receiver.getFriendList().contains(sender.getObjectId()))
			_mask |= IS_FRIEND;

		if(receiver.getClanId() > 0 && receiver.getClanId() == sender.getClanId())
			_mask |= IS_CLAN_MEMBER;

		if(receiver.getAllyId() > 0 && receiver.getAllyId() == sender.getAllyId())
			_mask |= IS_ALLIANCE_MEMBER;

		// Does not shows level
		if(sender.isGM())
			_mask |= IS_GM;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objectId);
		buffer.putInt(_type.ordinal());
		switch(_type)
		{
			case SYSTEM_MESSAGE:
				buffer.putInt(_sysString.getId());
				buffer.putInt(_systemMsg.getId());
				break;
			case TELL:
				writeString(_charName, buffer);
				writeElements(buffer);
				buffer.put((byte)_mask);
				if((_mask & IS_GM) == 0)
					buffer.put((byte)_charLevel);
				break;
			default:
				writeString(_charName, buffer);
				writeElements(buffer);
				break;
		}

		if(_text != null)
		{
			Player player = client.getActiveChar();
			if(player != null)
				player.getListeners().onChatMessageReceive(_type, _charName, _text);
		}
	}
}