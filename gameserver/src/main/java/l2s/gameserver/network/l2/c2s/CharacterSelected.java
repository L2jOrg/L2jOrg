package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.l2.GameClient.GameClientState;
import l2s.gameserver.network.l2.s2c.ActionFailPacket;
import l2s.gameserver.network.l2.s2c.CharacterSelectedPacket;
import l2s.gameserver.utils.AutoBan;

public class CharacterSelected extends L2GameClientPacket
{
	private int _charSlot;

	/**
	 * Format: cdhddd
	 */
	@Override
	protected void readImpl()
	{
		_charSlot = readD();
	}

	@Override
	protected void runImpl()
	{
		GameClient client = getClient();

		if(client.getActiveChar() != null)
			return;

		if(!client.secondaryAuthed())
		{
			sendPacket(ActionFailPacket.STATIC);
			return;
		}

		int objId = client.getObjectIdForSlot(_charSlot);
		if(AutoBan.isBanned(objId))
		{
			sendPacket(ActionFailPacket.STATIC);
			return;
		}

		Player activeChar = client.loadCharFromDisk(_charSlot);
		if(activeChar == null)
		{
			sendPacket(ActionFailPacket.STATIC);
			return;
		}

		if(activeChar.getAccessLevel() < 0)
			activeChar.setAccessLevel(0);

		client.setState(GameClientState.IN_GAME);

		sendPacket(new CharacterSelectedPacket(activeChar, client.getSessionKey().playOkID1));
	}
}