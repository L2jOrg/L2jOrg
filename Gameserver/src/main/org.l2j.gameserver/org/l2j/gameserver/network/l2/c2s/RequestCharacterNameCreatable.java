package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.dao.CharacterDAO;
import org.l2j.gameserver.network.l2.s2c.ExIsCharNameCreatable;
import org.l2j.gameserver.utils.Util;

public class RequestCharacterNameCreatable extends L2GameClientPacket
{
	private String _charname;

	@Override
	protected void readImpl()
	{
		_charname = readString();
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
