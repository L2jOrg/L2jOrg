package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.NpcString;

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
	protected void writeImpl()
	{
		if(_isHide == 5) //zatuchka tyt nixyja ne verno
		{
			writeD(_objectId);
			writeD(_isHide); // 0: show timer, 1: hide timer
			writeD(0x00); // unknown
			writeD(0x00); // unknown
			writeS(String.valueOf(_isIncrease)); // "0": count negative, "1": count positive
			writeS(String.valueOf(_startTime)); // timer starting minute(s)
			writeS(String.valueOf(_endTime)); // timer length minute(s) (timer will disappear 10 seconds before it ends)
			writeS(String.valueOf(0)); // timer length second(s) (timer will disappear 10 seconds before it ends)
			writeS(String.valueOf(0)); // timer starting second(s)
			writeElements();
		}
		else if(_isHide == 2)
		{
			writeD(_objectId);
			writeD(_isHide); // 0: show timer, 1: hide timer
			writeD(1); // unknown
			writeD(0x00); // unknown
			writeS(String.valueOf(_isIncrease)); // "0": count negative, "1": count positive
			writeS(""+_startTime+"%"); // timer starting minute(s)
			writeS(String.valueOf(0)); // timer starting second(s)
			writeS(String.valueOf(_endTime)); // timer length minute(s) (timer will disappear 10 seconds before it ends)
			writeS(String.valueOf(0)); // timer length second(s) (timer will disappear 10 seconds before it ends)
			writeElements();	
		}
		else
		{
			writeD(_objectId);
			writeD(_isHide); // 0: show timer, 1: hide timer
			writeD(0x00); // unknown
			writeD(0x00); // unknown
			writeS(String.valueOf(_isIncrease)); // "0": count negative, "1": count positive
			writeS(String.valueOf(_startTime / 60)); // timer starting minute(s)
			writeS(String.valueOf(_startTime % 60)); // timer starting second(s)
			writeS(String.valueOf(_endTime / 60)); // timer length minute(s) (timer will disappear 10 seconds before it ends)
			writeS(String.valueOf(_endTime % 60)); // timer length second(s) (timer will disappear 10 seconds before it ends)
			writeElements();
		}
	}
}