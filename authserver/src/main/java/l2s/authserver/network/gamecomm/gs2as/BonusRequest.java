package l2s.authserver.network.gamecomm.gs2as;


import l2s.authserver.accounts.Account;
import l2s.authserver.network.gamecomm.ReceivablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BonusRequest extends ReceivablePacket
{
	private static final Logger log = LoggerFactory.getLogger(BonusRequest.class);

	private String account;
	private int bonus;
	private int bonusExpire;

	@Override
	protected void readImpl()
	{
		account = readS();
		bonus = readD();
		bonusExpire = readD();
	}

	@Override
	protected void runImpl()
	{
		Account acc = new Account(account);
		acc.restore();
		acc.setBonus(bonus);
		acc.setBonusExpire(bonusExpire);
		acc.update();
	}
}
