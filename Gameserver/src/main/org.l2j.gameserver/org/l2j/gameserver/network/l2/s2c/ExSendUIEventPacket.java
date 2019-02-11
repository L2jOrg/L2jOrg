package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.network.l2.components.NpcString;

import java.nio.ByteBuffer;

public class ExSendUIEventPacket extends NpcStringContainer
{
	private int _objectId;
	private int _isHide;
	private int _isIncrease;
	private int _startTime;
	private int _endTime;

	public ExSendUIEventPacket(Player player, int isHide, int isIncrease, int startTime, int endTime, String... params)
	{
		this(player, isHide, isIncrease, startTime, endTime, NpcString.NONE, params);
	}

	public ExSendUIEventPacket(Player player, int isHide, int isIncrease, int startTime, int endTime, NpcString npcString, String... params)
	{
		super(npcString, params);
		_objectId = player.getObjectId();
		_isHide = isHide;
		_isIncrease = isIncrease;
		_startTime = startTime;
		_endTime = endTime;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		if(_isHide == 5) //zatuchka tyt nixyja ne verno
		{
			buffer.putInt(_objectId);
			buffer.putInt(_isHide); // 0: show timer, 1: hide timer
			buffer.putInt(0x00); // unknown
			buffer.putInt(0x00); // unknown
			writeString(String.valueOf(_isIncrease), buffer); // "0": count negative, "1": count positive
			writeString(String.valueOf(_startTime), buffer); // timer starting minute(s)
			writeString(String.valueOf(_endTime), buffer); // timer length minute(s) (timer will disappear 10 seconds before it ends)
			writeString(String.valueOf(0), buffer); // timer length second(s) (timer will disappear 10 seconds before it ends)
			writeString(String.valueOf(0), buffer); // timer starting second(s)
			writeElements(buffer);
		}
		else if(_isHide == 2)
		{
			buffer.putInt(_objectId);
			buffer.putInt(_isHide); // 0: show timer, 1: hide timer
			buffer.putInt(1); // unknown
			buffer.putInt(0x00); // unknown
			writeString(String.valueOf(_isIncrease), buffer); // "0": count negative, "1": count positive
			writeString(""+_startTime+"%", buffer); // timer starting minute(s)
			writeString(String.valueOf(0), buffer); // timer starting second(s)
			writeString(String.valueOf(_endTime), buffer); // timer length minute(s) (timer will disappear 10 seconds before it ends, buffer)
			writeString(String.valueOf(0), buffer); // timer length second(s) (timer will disappear 10 seconds before it ends, buffer)
			writeElements(buffer);
		}
		else
		{
			buffer.putInt(_objectId);
			buffer.putInt(_isHide); // 0: show timer, 1: hide timer
			buffer.putInt(0x00); // unknown
			buffer.putInt(0x00); // unknown
			writeString(String.valueOf(_isIncrease), buffer); // "0": count negative, "1": count positive
			writeString(String.valueOf(_startTime / 60), buffer); // timer starting minute(s)
			writeString(String.valueOf(_startTime % 60), buffer); // timer starting second(s)
			writeString(String.valueOf(_endTime / 60), buffer); // timer length minute(s) (timer will disappear 10 seconds before it ends, buffer)
			writeString(String.valueOf(_endTime % 60), buffer); // timer length second(s) (timer will disappear 10 seconds before it ends, buffer)
			writeElements(buffer);
		}
	}
}