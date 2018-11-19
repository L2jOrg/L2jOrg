package l2s.authserver.network.l2.s2c;

/**
 * Fromat: d
 * d: the failure reason
 */
public final class LoginFail extends L2LoginServerPacket
{
	public static enum LoginFailReason
	{
		REASON_NO_MESSAGE(0),
		REASON_SYSTEM_ERROR(1), //There is a system error. Please log in again later
		REASON_PASS_WRONG(2), //The password you have entered is incorrect. Confirm your ...
		REASON_USER_OR_PASS_WRONG(3),
		REASON_ACCESS_FAILED_TRYA1(4), //Access failed. Please try again later.
		REASON_ACCOUNT_INFO_INCORR(5), //Your account information is incorrect. For more details ...
		REASON_ACCESS_FAILED_TRYA2(6), //Access failed. Please try again later.
		REASON_ACCOUNT_IN_USE(7), //Account is already in use. Unable to log in.
		// 8 — Access failed. Please try again later. .
		// 9 — Access failed. Please try again later. .
		// 10 — Access failed. Please try again later. .
		// 11 — Access failed. Please try again later. .
		REASON_MIN_AGE(12), //Lineage II game services may be used by individuals 15 years of age or older ...
		// 13 — Access failed. Please try again later. .
		// 14 — Access failed. Please try again later. .
		// 15 — Due to high server traffic, your login attempt has failed.  Please try again soon.
		REASON_SERVER_MAINTENANCE(16), //Currently undergoing game server maintenance. Please log in again later
		REASON_CHANGE_TEMP_PASS(17), //Please login after changing your temporary password.
		REASON_USAGE_TEMP_EXPIRED(18), //Your usage term has expired. PlayNC website ...
		REASON_TIME_LEFT_EXPIRED(19), //There is no time left on this account.
		REASON_SYS_ERR(20), //System Error.
		REASON_ACCESS_FAILED(21), //Access Failed.
		REASON_ATTEMPTED_RESTRICTED_IP(22), //Game connection attempted through a restricted IP.g.
		// 23-29 unused
		REASON_WEEK_USAGE_TIME_END(30), //This week's usage time has finished.
		REASON_SECURITY_CARD_NUMB_I(31), //The security card number is invalid.
		REASON_VERIFY_AGE(32), //Users who have not verified their age may not log in ...
		REASON_CANNOT_ACC_COUPON(33), //This server cannot be accessed by the coupon you are using.
		// 34 unused
		REASON_DUAL_BOX(35),
		REASON_ACCOUNT_INACTIVE(36), //Your account is currently inactive because you have not logged ...
		REASON_USER_AGREEMENT_DIS(37), //You must accept the User Agreement before this account ...
		REASON_GUARDIAN_CONSENT_REQ(38), //A guardian's consent is required before this account ...
		REASON_USER_AGREEMENT_DEC(39), //This account has declined the User Agreement or is pending ...
		REASON_ACCOUNT_SUSPENDED(40), //This account has been suspended ...
		REASON_CHANGE_PASS_AND_QUIZ(41), //Your account can only be used after changing your password and quiz ...
		REASON_LOGGED_INTO_10_ACCS(42); //You are currently logged into 10 of your accounts and can no longer ...
		// 43 — The master account of your account has been restricted.

		private final int _code;

		LoginFailReason(int code)
		{
			_code = code;
		}

		public final int getCode()
		{
			return _code;
		}
	}

	private int reason_code;

	public LoginFail(LoginFailReason reason)
	{
		reason_code = reason.getCode();
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x01);
		writeD(reason_code);
	}
}
