package l2s.gameserver.network.authcomm.gs2as;

import l2s.gameserver.network.authcomm.SendablePacket;

/**
 * @Author: SYS -> changed by Iqman 02.02.2012 12:12
 * @Date: 10/4/2008
 */
public class LockAccountIP extends SendablePacket
{
	String _account;
	String _IP;
	int _time;

	public LockAccountIP(String account, String IP, int time)
	{
		_account = account;
		_IP = IP;
		_time = time;
	}

	@Override
	protected void writeImpl()
	{	
		writeC(0x0b);
		writeS(_account);
		writeS(_IP);
		writeD(_time);
	}
}