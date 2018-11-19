package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.network.l2.s2c.VersionCheckPacket;
import l2s.gameserver.network.l2.s2c.SendStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * packet type id 0x0E
 * format:	cdbd
 */
public class ProtocolVersion extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(ProtocolVersion.class);

	private int _version;

	@Override
	protected void readImpl()
	{
		_version = readD();
	}

	@Override
	protected void runImpl()
	{
		if(_version == -2)
		{
			_client.closeNow(false);
			return;
		}
		else if(_version == -3)
		{
			_log.info("Status request from IP : " + getClient().getIpAddr());
			getClient().close(new SendStatus());
			return;
		}
		else if(!Config.AVAILABLE_PROTOCOL_REVISIONS.contains(_version))
		{
			_log.warn("Unknown protocol revision : " + _version + ", client : " + _client);
			getClient().close(new VersionCheckPacket(null));
			return;
		}

		getClient().setRevision(_version);
		sendPacket(new VersionCheckPacket(_client.enableCrypt()));
	}
}