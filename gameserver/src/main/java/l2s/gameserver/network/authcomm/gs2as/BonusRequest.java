package l2s.gameserver.network.authcomm.gs2as;

import l2s.gameserver.network.authcomm.SendablePacket;

public class BonusRequest extends SendablePacket
{
	private String account;
	private int bonus;
	private int bonusExpire;

	public BonusRequest(String account, int bonus, int bonusExpire)
	{
		this.account = account;
		this.bonus = bonus;
		this.bonusExpire = bonusExpire;
	}

	protected void writeImpl()
	{
		writeC(0x10);
		writeS(account);
		writeD(bonus);
		writeD(bonusExpire);
	}
}