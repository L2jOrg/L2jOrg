package org.l2j.authserver.network.gameserver.packet.game2auth;

import org.l2j.authserver.controller.AuthController;

import java.nio.ByteBuffer;

public class ChangeAccessLevel extends GameserverReadablePacket {
	
	private short level;
	private String account;

	@Override
	protected void readImpl(ByteBuffer buffer)  {
		level = buffer.getShort();
		account = readString(buffer);
	}

	@Override
	protected void runImpl()  {
        AuthController.getInstance().setAccountAccessLevel(account, level);
	}
}