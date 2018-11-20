package org.l2j.authserver.network.client.packet.client2auth;

import org.l2j.authserver.network.client.packet.L2LoginClientPacket;
import org.l2j.authserver.network.client.packet.auth2client.LoginFail;
import org.l2j.authserver.network.client.packet.auth2client.ServerList;

/**
 * Format: ddc d: fist part of session id d: second part of session id c: list Type
 *
 * TYPE_BARE=0 - Indicates that each game server will have its basic information specified.
 *
 * TYPE_C0=1 - Indicates that each game server will have its additional and dynamic information specified.
 *
 * TYPE_NAMED=2 - Indicates that each game server will have its name specified.
 *
 * TYPE_C1=3 - Indicates that each game server will have its type mask specified.
 *
 * TYPE_C2=4 - Indicates that each game server will have its bracket flag specified.
 *
 * TYPE_FREYA=5 - Indicates that each game server will have reader's character count(s) specified.
 */
public class RequestServerList extends L2LoginClientPacket
{
	private int accountId;
	private int authId;
    private byte listType;

	@Override
	public boolean readImpl() {
        accountId = readInt();
        authId = readInt();
        listType = readByte();
		return true;
	}
	
	@Override
	public void run()
	{
		if (getClient().getSessionKey().checkLoginPair(accountId, authId))
		{
			getClient().sendPacket(new ServerList(listType));
		}
		else
		{
			getClient().close(LoginFail.LoginFailReason.REASON_ACCESS_FAILED);
		}
	}
}
