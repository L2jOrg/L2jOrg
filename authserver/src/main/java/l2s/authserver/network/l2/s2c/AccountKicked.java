package l2s.authserver.network.l2.s2c;

public final class AccountKicked extends L2LoginServerPacket
{
	public static enum AccountKickedReason
	{
		REASON_FALSE_DATA_STEALER_REPORT(0x00),
		REASON_DATA_STEALER(0x01),
		REASON_SOUSPICION_DATA_STEALER(0x03),
		REASON_NON_PAYEMENT_CELL_PHONE(0x04),
		REASON_30_DAYS_SUSPENDED_CASH(0x08),
		REASON_PERMANENTLY_SUSPENDED_CASH(0x10), //The password you have entered is incorrect. Confirm your account information and log in again later.
		REASON_PERMANENTLY_BANNED(0x20),
		REASON_ACCOUNT_MUST_BE_VERIFIED(0x40);

		private final int _code;

		AccountKickedReason(int code)
		{
			_code = code;
		}

		public final int getCode()
		{
			return _code;
		}
	}

	private int reason;

	public AccountKicked(AccountKickedReason reason)
	{
		this.reason = reason.getCode();
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x02);
		writeD(reason);
	}
}
