package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.ShortCut;
import l2s.gameserver.network.l2.s2c.ShortCutRegisterPacket;

public class RequestShortCutReg extends L2GameClientPacket
{
	private int _type, _id, _slot, _page, _lvl, _characterType;

	@Override
	protected void readImpl()
	{
		_type = readD();
		int slot = readD();
		_id = readD();
		_lvl = readD();
		_characterType = readD();

		readD();
		readD();

		_slot = slot % 12;
		_page = slot / 12;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
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