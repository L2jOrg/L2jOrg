package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;

public class RequestShortCutDel extends L2GameClientPacket
{
	private int _slot;
	private int _page;

	/**
	 * packet type id 0x3F
	 * format:		cd
	 */
	@Override
	protected void readImpl()
	{
		int id = readD();
		_slot = id % 12;
		_page = id / 12;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		// client dont needs confirmation. this packet is just to inform the server
		activeChar.deleteShortCut(_slot, _page);
	}
}