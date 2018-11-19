package l2s.gameserver.network.authcomm.gs2as;

import l2s.gameserver.network.authcomm.SendablePacket;

public class ChangeAllowedHwid extends SendablePacket
{
	private String account;
	private String hwid;

	public ChangeAllowedHwid(String account, String hwid)
	{
		this.account = account;
		this.hwid = hwid;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x09);
		writeS(account);
		writeS(hwid);
	}
}