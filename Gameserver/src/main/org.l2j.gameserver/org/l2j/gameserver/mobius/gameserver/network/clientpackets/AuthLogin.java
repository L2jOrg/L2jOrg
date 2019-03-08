package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.LoginServerThread;
import org.l2j.gameserver.mobius.gameserver.LoginServerThread.SessionKey;

import java.nio.ByteBuffer;

/**
 * This class ...
 * @version $Revision: 1.9.2.3.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public final class AuthLogin extends IClientIncomingPacket
{
	
	// loginName + keys must match what the loginserver used.
	private String _loginName;
	/*
	 * private final long _key1; private final long _key2; private final long _key3; private final long _key4;
	 */
	private int _playKey1;
	private int _playKey2;
	private int _loginKey1;
	private int _loginKey2;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_loginName = readString(packet).toLowerCase();
		_playKey2 = packet.getInt();
		_playKey1 = packet.getInt();
		_loginKey1 = packet.getInt();
		_loginKey2 = packet.getInt();
	}
	
	@Override
	public void runImpl()
	{
		if (_loginName.isEmpty() || !client.isProtocolOk())
		{
			client.closeNow();
			return;
		}
		
		final SessionKey key = new SessionKey(_loginKey1, _loginKey2, _playKey1, _playKey2);
		
		// avoid potential exploits
		if (client.getAccountName() == null)
		{
			// Preventing duplicate login in case client login server socket was disconnected or this packet was not sent yet
			if (LoginServerThread.getInstance().addGameServerLogin(_loginName, client))
			{
				client.setAccountName(_loginName);
				LoginServerThread.getInstance().addWaitingClientAndSendRequest(_loginName, client, key);
			}
			else
			{
				client.close(null);
			}
		}
	}
}
