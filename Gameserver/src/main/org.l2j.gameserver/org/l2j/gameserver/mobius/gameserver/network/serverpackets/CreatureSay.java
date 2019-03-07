package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.enums.ChatType;
import org.l2j.gameserver.mobius.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.NpcStringId;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;

import java.util.ArrayList;
import java.util.List;

public final class CreatureSay implements IClientOutgoingPacket
{
	private final int _objectId;
	private final ChatType _textType;
	private String _charName = null;
	private int _charId = 0;
	private String _text = null;
	private int _npcString = -1;
	private int _mask;
	private int _charLevel = -1;
	private List<String> _parameters;
	
	/**
	 * @param sender
	 * @param receiver
	 * @param name
	 * @param messageType
	 * @param text
	 */
	public CreatureSay(L2PcInstance sender, L2PcInstance receiver, String name, ChatType messageType, String text)
	{
		_objectId = sender.getObjectId();
		_charName = name;
		_charLevel = sender.getLevel();
		_textType = messageType;
		_text = text;
		if (receiver != null)
		{
			if (receiver.getFriendList().contains(sender.getObjectId()))
			{
				_mask |= 0x01;
			}
			if ((receiver.getClanId() > 0) && (receiver.getClanId() == sender.getClanId()))
			{
				_mask |= 0x02;
			}
			if ((MentorManager.getInstance().getMentee(receiver.getObjectId(), sender.getObjectId()) != null) || (MentorManager.getInstance().getMentee(sender.getObjectId(), receiver.getObjectId()) != null))
			{
				_mask |= 0x04;
			}
			if ((receiver.getAllyId() > 0) && (receiver.getAllyId() == sender.getAllyId()))
			{
				_mask |= 0x08;
			}
		}
		
		// Does not shows level
		if (sender.isGM())
		{
			_mask |= 0x10;
		}
	}
	
	/**
	 * Used by fake players.
	 * @param sender
	 * @param receiver
	 * @param name
	 * @param messageType
	 * @param text
	 */
	public CreatureSay(L2Npc sender, L2PcInstance receiver, String name, ChatType messageType, String text)
	{
		_objectId = sender.getObjectId();
		_charName = name;
		_charLevel = sender.getLevel();
		_textType = messageType;
		_text = text;
	}
	
	/**
	 * @param objectId
	 * @param messageType
	 * @param charName
	 * @param text
	 */
	public CreatureSay(int objectId, ChatType messageType, String charName, String text)
	{
		_objectId = objectId;
		_textType = messageType;
		_charName = charName;
		_text = text;
	}
	
	public CreatureSay(L2PcInstance player, ChatType messageType, String text)
	{
		_objectId = player.getObjectId();
		_textType = messageType;
		_charName = player.getAppearance().getVisibleName();
		_text = text;
	}
	
	public CreatureSay(int objectId, ChatType messageType, int charId, NpcStringId npcString)
	{
		_objectId = objectId;
		_textType = messageType;
		_charId = charId;
		_npcString = npcString.getId();
	}
	
	public CreatureSay(int objectId, ChatType messageType, String charName, NpcStringId npcString)
	{
		_objectId = objectId;
		_textType = messageType;
		_charName = charName;
		_npcString = npcString.getId();
	}
	
	public CreatureSay(int objectId, ChatType messageType, int charId, SystemMessageId sysString)
	{
		_objectId = objectId;
		_textType = messageType;
		_charId = charId;
		_npcString = sysString.getId();
	}
	
	/**
	 * String parameter for argument S1,S2,.. in npcstring-e.dat
	 * @param text
	 */
	public void addStringParameter(String text)
	{
		if (_parameters == null)
		{
			_parameters = new ArrayList<>();
		}
		_parameters.add(text);
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.SAY2.writeId(packet);
		
		packet.writeD(_objectId);
		packet.writeD(_textType.getClientId());
		if (_charName != null)
		{
			packet.writeS(_charName);
		}
		else
		{
			packet.writeD(_charId);
		}
		packet.writeD(_npcString); // High Five NPCString ID
		if (_text != null)
		{
			packet.writeS(_text);
			if ((_charLevel > 0) && (_textType == ChatType.WHISPER))
			{
				packet.writeC(_mask);
				if ((_mask & 0x10) == 0)
				{
					packet.writeC(_charLevel);
				}
			}
		}
		else if (_parameters != null)
		{
			for (String s : _parameters)
			{
				packet.writeS(s);
			}
		}
		return true;
	}
	
	@Override
	public final void runImpl(L2PcInstance player)
	{
		if (player != null)
		{
			player.broadcastSnoop(_textType, _charName, _text);
		}
	}
}
