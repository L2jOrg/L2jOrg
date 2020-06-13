/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.authserver.network.client.packet.auth2client;


import org.l2j.authserver.network.client.AuthClient;
import org.l2j.authserver.network.client.packet.AuthServerPacket;

/**
 * Fromat: d d: the failure reason
 */
public final class LoginFail extends AuthServerPacket {
	
	private final LoginFailReason _reason;
	
	public LoginFail(LoginFailReason reason)
	{
		_reason = reason;
	}
	
	@Override
	protected void writeImpl(AuthClient client) {
		writeByte(0x01);
		writeInt(_reason.getCode());
	}

	@Override
	public String toString() {
		return "LoginFail {" + _reason + '}';
	}

	public enum LoginFailReason  {
		REASON_NO_MESSAGE(0),
		REASON_SYSTEM_ERROR(1), //There is a system error. Please log in again later
		REASON_PASS_WRONG(2), //The password you have entered is incorrect. Confirm your ...
		// REASON_USER_OR_PASS_WRONG(3), Brokens the client
		REASON_ACCESS_FAILED_TRYA1(4), //Access failed. Please try again later.
		REASON_ACCOUNT_INFO_INCORR(5), //Your account information is incorrect. For more details ...
		REASON_ACCESS_FAILED_TRYA2(6), //Access failed. Please try again later.
		REASON_ACCOUNT_IN_USE(7), //Account is already in use. Unable to log in.
		// 8 — Access failed. Please try again later. .
		// 9 — Access failed. Please try again later. .
		// 10 — Access failed. Please try again later. .
		// 11 — Access failed. Please try again later. .
		REASON_MIN_AGE(12), // game services may be used by individuals 15 years of age or older ...
		// 13 — Access failed. Please try again later. .
		// 14 — Access failed. Please try again later. .
		// 15 — Due to high server traffic, your login attempt has failed.  Please try again soon.
		REASON_SERVER_MAINTENANCE(16), //Currently undergoing game server maintenance. Please log in again later
		REASON_CHANGE_TEMP_PASS(17), //Please login after changing your temporary password.
		REASON_USAGE_TEMP_EXPIRED(18), //Your usage term has expired. website ...
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
}
