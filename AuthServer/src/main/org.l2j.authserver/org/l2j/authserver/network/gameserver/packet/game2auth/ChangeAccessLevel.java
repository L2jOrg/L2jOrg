package org.l2j.authserver.network.gameserver.packet.game2auth;

import org.l2j.authserver.controller.AuthController;

public class ChangeAccessLevel extends GameserverReadablePacket {
	
	private short level;
	private String account;

	@Override
	protected void readImpl()  {
		level = readShort();
		account = readString();
	}

	@Override
	protected void runImpl()  {
        AuthController.getInstance().setAccountAccessLevel(account, level);
	}
}