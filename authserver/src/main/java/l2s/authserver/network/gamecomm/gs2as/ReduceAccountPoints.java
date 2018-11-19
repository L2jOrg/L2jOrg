package l2s.authserver.network.gamecomm.gs2as;

import l2s.authserver.accounts.Account;
import l2s.authserver.network.gamecomm.ReceivablePacket;

public class ReduceAccountPoints extends ReceivablePacket
{
	private String account;
	private int count;

	@Override
	protected void readImpl()
	{
		account = readS();
		count = readD();
	}

	@Override
	protected void runImpl()
	{
		Account.reducePoints(account, count);
	}
}
