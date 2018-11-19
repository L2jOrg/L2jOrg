package l2s.gameserver.network.authcomm.gs2as;

import l2s.gameserver.network.authcomm.SendablePacket;

public class PlayerLogout extends SendablePacket
{
	private String account;

	public PlayerLogout(String account)
	{
		this.account = account;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x04);
		writeS(account);
	}
}
