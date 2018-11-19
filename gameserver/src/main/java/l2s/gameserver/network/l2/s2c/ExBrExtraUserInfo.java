package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

//@Deprecated
public class ExBrExtraUserInfo extends L2GameServerPacket
{
	private int _objectId;
	private int _effect3;
	private int _lectureMark;

	public ExBrExtraUserInfo(Player cha)
	{
		_objectId = cha.getObjectId();
		_effect3 = 0/*cha.getAbnormalEffect3()*/;
		_lectureMark = cha.getLectureMark();
	}

	@Override
	protected void writeImpl()
	{
		writeD(_objectId); //object id of player
		writeD(_effect3); // event effect id
		writeC(_lectureMark);
	}
}