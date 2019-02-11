package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.ShortCut;
import org.l2j.gameserver.network.l2.s2c.ShortCutRegisterPacket;

import java.nio.ByteBuffer;

public class RequestShortCutReg extends L2GameClientPacket
{
	private int _type, _id, _slot, _page, _lvl, _characterType;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_type = buffer.getInt();
		int slot = buffer.getInt();
		_id = buffer.getInt();
		_lvl = buffer.getInt();
		_characterType = buffer.getInt();

		buffer.getInt();
		buffer.getInt();

		_slot = slot % 12;
		_page = slot / 12;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		if(_page < 0 || _page > ShortCut.PAGE_MAX)
		{
			activeChar.sendActionFailed();
			return;
		}

		ShortCut shortCut = new ShortCut(_slot, _page, _type, _id, _lvl, _characterType);
		activeChar.sendPacket(new ShortCutRegisterPacket(activeChar, shortCut));
		activeChar.registerShortCut(shortCut);
	}
}