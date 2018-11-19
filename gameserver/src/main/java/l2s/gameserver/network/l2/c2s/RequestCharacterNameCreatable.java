package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.dao.CharacterDAO;
import l2s.gameserver.network.l2.s2c.ExIsCharNameCreatable;
import l2s.gameserver.utils.Util;

public class RequestCharacterNameCreatable extends L2GameClientPacket
{
	private String _charname;

	@Override
	protected void readImpl()
	{
		_charname = readS();
	}

	@Override
	protected void runImpl()
	{
		if(CharacterDAO.getInstance().accountCharNumber(getClient().getLogin()) >= 8)
		{
			sendPacket(ExIsCharNameCreatable.TOO_MANY_CHARACTERS);
			return;
		}

		if(_charname == null || _charname.isEmpty())
		{
			sendPacket(ExIsCharNameCreatable.ENTER_CHAR_NAME__MAX_16_CHARS);
			return;
		}
		else if(!Util.isMatchingRegexp(_charname, Config.CNAME_TEMPLATE))
		{
			sendPacket(ExIsCharNameCreatable.WRONG_NAME);
			return;
		}
		else if(CharacterDAO.getInstance().getObjectIdByName(_charname) > 0)
		{
			sendPacket(ExIsCharNameCreatable.NAME_ALREADY_EXISTS);
			return;
		}
		sendPacket(ExIsCharNameCreatable.SUCCESS);
	}
}
