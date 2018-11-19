package l2s.authserver.network.gamecomm.as2gs;

import l2s.authserver.network.gamecomm.SendablePacket;

public class KickPlayer extends SendablePacket
{
	private String account;
	
	public KickPlayer(String login)
	{
		this.account = login;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x03);
		writeS(account);
	}
}