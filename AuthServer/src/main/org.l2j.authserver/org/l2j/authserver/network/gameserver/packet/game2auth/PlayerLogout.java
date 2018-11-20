package org.l2j.authserver.network.gameserver.packet.game2auth;

public class PlayerLogout extends GameserverReadablePacket {
	
	private String account;

	public String getAccount()
	{
		return account;
	}

	@Override
	protected void readImpl() {
		account = readString();
	}

	@Override
	protected void runImpl()  {
		client.getGameServerInfo().removeAccount(account);
	}
}