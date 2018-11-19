package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;

/**
 * @author Bonux
**/
public class ExSendSelectedQuestZoneID extends L2GameClientPacket
{
	private int _questZoneId;

	@Override
	protected void readImpl()
	{
		_questZoneId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.setQuestZoneId(_questZoneId);

		if(activeChar.isGM())
			activeChar.sendMessage("Current quest zone ID: " + _questZoneId);
	}
}