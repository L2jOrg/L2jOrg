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
package l2s.authserver.network.l2.c2s;

import l2s.authserver.network.l2.L2LoginClient;
import l2s.authserver.network.l2.L2LoginClient.LoginClientState;
import l2s.authserver.network.l2.s2c.GGAuth;
import l2s.authserver.network.l2.s2c.LoginFail;

/**
 * @author -Wooden-
 * Format: ddddd
 *
 */
public class AuthGameGuard extends L2LoginClientPacket
{
	private int _sessionId;
	//private int _data1;
	//private int _data2;
	//private int _data3;
	//private int _data4;

	@Override
	protected void readImpl()
	{
		_sessionId = readD();
		/*_data1 = readD();
		_data2 = readD();
		_data3 = readD();
		_data4 = readD(); */
	}

	@Override
	protected void runImpl()
	{
		L2LoginClient client = getClient();

		if(_sessionId == 0 || _sessionId == client.getSessionId())
		{
			client.setState(LoginClientState.AUTHED_GG);
			client.sendPacket(new GGAuth(client.getSessionId()));
		}
		else
			client.close(LoginFail.LoginFailReason.REASON_ACCESS_FAILED);
	}
}
