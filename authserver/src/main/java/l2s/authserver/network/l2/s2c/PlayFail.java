/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */
package l2s.authserver.network.l2.s2c;

/**
 * This class ...
 *
 * @version $Revision: 1.2.4.1 $ $Date: 2005/03/27 15:30:11 $
 */
public final class PlayFail extends L2LoginServerPacket
{
	public static final int REASON_SYSTEM_ERROR = 1; // This Account is already in use. Access denied.
	public static final int REASON_ACCESS_FAILED_1 = 2; // Access failed. Please try again later.
	public static final int REASON_ACCOUNT_INFO_INCORRECT = 3; // Your account information is incorrect. For more details please contact our Support Center at http://support.plaync.com
	public static final int REASON_PASSWORD_INCORRECT_1 = 4; // Password does not match this account. Confirm your account information and log in again later.
	public static final int REASON_PASSWORD_INCORRECT_2 = 5; // Password does not match this account. Confirm your account information and log in again later.
	public static final int REASON_NO_REASON = 6;
	public static final int REASON_SYS_ERROR = 7; // System error, please log in again later.
	public static final int REASON_ACCESS_FAILED_2 = 8; // Access failed. Please try again later.
	public static final int REASON_HIGH_SERVER_TRAFFIC = 9; // Due to high server traffic, your login attempt has failed. Please try again soon.
	public static final int REASON_MIN_AGE = 10; // Lineage II game service may used by individuals 15 years of age or older except for PvP server, which may only be used by adults 18 years of age and older. (Korea Only)

	private int _reason;

	public PlayFail(int reason)
	{
		_reason = reason;
	}

	/**
	 * @see l2s.commons.net.nio.impl.SendablePacket#write()
	 */
	@Override
	protected void writeImpl()
	{
		writeC(0x06);
		writeC(_reason);
	}
}
