package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

import java.nio.ByteBuffer;

public class BonusRequest extends SendablePacket
{
	private String account;
	private int bonus;
	private int bonusExpire;

	public BonusRequest(String account, int bonus, int bonusExpire) {
		this.account = account;
		this.bonus = bonus;
		this.bonusExpire = bonusExpire;
	}

	protected void writeImpl(AuthServerClient client, ByteBuffer buffer) {
		buffer.put((byte)0x10);
		writeString(account, buffer);
		buffer.putInt(bonus);
		buffer.putInt(bonusExpire);
	}
}