package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient.GameClientState;
import org.l2j.gameserver.network.l2.s2c.ActionFailPacket;
import org.l2j.gameserver.network.l2.s2c.CharacterSelectedPacket;
import org.l2j.gameserver.utils.AutoBan;

import java.nio.ByteBuffer;

public class CharacterSelected extends L2GameClientPacket
{
	private int _charSlot;

	/**
	 * Format: cdhddd
     * @param buffer
     */
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_charSlot = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
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

		sendPacket(new CharacterSelectedPacket(activeChar, client.getSessionKey().gameserverSession));
	}
}